package org.araqnid.appstatus

import java.util.function.Supplier

abstract class StatusComponent(val id: String, val label: String) {
    abstract fun report(): StatusReport

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
