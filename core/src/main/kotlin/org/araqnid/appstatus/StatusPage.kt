package org.araqnid.appstatus

import com.fasterxml.jackson.annotation.JsonUnwrapped

data class StatusPage(val status: StatusReport.Priority, val components: Map<String, LabelledStatusReport>) {
    companion object {
        fun build(statusComponents: Collection<StatusComponent>): StatusPage {
            val reports = statusComponents.associate { it.id to LabelledStatusReport(it.label, it.report()) }

            val overallPriority = reports.values.map { it.report.priority }.maxOrNull()

            return StatusPage(when (overallPriority) {
                StatusReport.Priority.INFO, null -> StatusReport.Priority.OK
                else -> overallPriority
            }, reports)
        }
    }

    data class LabelledStatusReport(val label: String, @get:JsonUnwrapped val report: StatusReport)
}
