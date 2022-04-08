package org.araqnid.appstatus

import kotlinx.serialization.Serializable

@Serializable
data class Report(val status: Status?, val text: String) {
    enum class Status {
        OK, WARNING, CRITICAL
    }

    override fun toString(): String {
        return if (status != null) "$status $text" else text
    }
}

fun Report.limitPriority(maxStatus: Report.Status) =
    if (status == null) this
    else copy(
        status = status.coerceAtMost(maxStatus)
    )