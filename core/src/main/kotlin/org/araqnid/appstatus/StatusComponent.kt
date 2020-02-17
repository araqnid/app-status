package org.araqnid.appstatus

class StatusComponent(val id: String, val label: String, private val supplier: () -> StatusReport) : ReportConfigurable<StatusComponent> {
    fun report(): StatusReport = supplier()

    fun mapReport(fn: (StatusReport) -> StatusReport) =
            StatusComponent(id, label) { fn(supplier()) }

    override fun mapText(fn: (String) -> String): StatusComponent =
            mapReport { it.mapText(fn) }
    override fun limitPriority(maxPriority: StatusReport.Priority): StatusComponent =
            mapReport { it.limitPriority(maxPriority) }

    companion object {
        @JvmStatic fun info(id: String, label: String, content: String) = info(id, label) { content }
        @JvmStatic fun info(id: String, label: String, supplier: java.util.function.Supplier<String>) = info(id, label, supplier::get)
        @JvmStatic fun info(id: String, label: String, supplier: () -> String) =
                StatusComponent(id, label) { StatusReport( StatusReport.Priority.INFO, supplier()) }

        @JvmStatic fun from(id: String, label: String, content: StatusReport) = StatusComponent(id, label) { content }
        @JvmStatic fun from(id: String, label: String, supplier: java.util.function.Supplier<StatusReport>) = StatusComponent(id, label, supplier::get)
        @JvmStatic fun from(id: String, label: String, supplier: () -> StatusReport) = StatusComponent(id, label, supplier)
    }
}
