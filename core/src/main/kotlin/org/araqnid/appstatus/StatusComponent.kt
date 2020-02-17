package org.araqnid.appstatus

import java.util.function.Supplier

abstract class StatusComponent(val id: String, val label: String) {
    abstract fun report(): StatusReport

    fun mapReport(fn: (StatusReport) -> StatusReport): StatusComponent = object : StatusComponent(id, label) {
        override fun report() = fn(this@StatusComponent.report())
        override fun toString() = this@StatusComponent.toString()
    }

    fun mapText(fn: (String) -> String): StatusComponent = mapReport { it.mapText(fn) }
    fun limitPriority(maxPriority: StatusReport.Priority): StatusComponent = mapReport { it.limitPriority(maxPriority) }
    fun pending() = object : StatusComponent(id, "$label (Pending)") {
        override fun report(): StatusReport {
            val underlying = this@StatusComponent.report()
            return StatusReport(StatusReport.Priority.INFO, "${underlying.text} (pending; priority was ${underlying.priority})")
        }
        override fun toString() = "${this@StatusComponent} (Pending)"
    }

    companion object {
        @JvmStatic fun info(id: String, label: String, content: String) = info(id, label, { content })
        @JvmStatic fun info(id: String, label: String, supplier: Supplier<String>) = info(id, label, supplier::get)
        @JvmStatic fun info(id: String, label: String, supplier: () -> String) = from(id, label, { StatusReport(StatusReport.Priority.INFO, supplier()) })

        @JvmStatic fun from(id: String, label: String, content: StatusReport) = from(id, label, { content })
        @JvmStatic fun from(id: String, label: String, supplier: Supplier<StatusReport>) = from(id, label, supplier::get)
        @JvmStatic fun from(id: String, label: String, supplier: () -> StatusReport) = object : StatusComponent(id, label) {
            override fun report(): StatusReport = supplier()
        }
    }
}
