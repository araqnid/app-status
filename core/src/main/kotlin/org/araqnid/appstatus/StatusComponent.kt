package org.araqnid.appstatus

import java.util.function.Supplier

abstract class StatusComponent(val id: String, val label: String) : ReportConfigurable<StatusComponent> {
    abstract fun report(): StatusReport

    fun mapReport(fn: (StatusReport) -> StatusReport): StatusComponent = object : StatusComponent(id, label) {
        override fun report() = fn(this@StatusComponent.report())
        override fun toString() = this@StatusComponent.toString()
    }

    override fun mapText(fn: (String) -> String): StatusComponent = mapReport { it.mapText(fn) }
    override fun limitPriority(maxPriority: StatusReport.Priority): StatusComponent = mapReport { it.limitPriority(maxPriority) }

    companion object {
        @JvmStatic fun info(id: String, label: String, content: String) = info(id, label) { content }
        @JvmStatic fun info(id: String, label: String, supplier: Supplier<String>) = info(id, label, supplier::get)
        @JvmStatic fun info(id: String, label: String, supplier: () -> String) = from(id, label) { StatusReport(StatusReport.Priority.INFO, supplier()) }

        @JvmStatic fun from(id: String, label: String, content: StatusReport) = from(id, label) { content }
        @JvmStatic fun from(id: String, label: String, supplier: Supplier<StatusReport>) = from(id, label, supplier::get)
        @JvmStatic fun from(id: String, label: String, supplier: () -> StatusReport) = object : StatusComponent(id, label) {
            override fun report(): StatusReport = supplier()
        }
    }
}
