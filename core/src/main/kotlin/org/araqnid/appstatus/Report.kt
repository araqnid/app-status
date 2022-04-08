package org.araqnid.appstatus

import kotlinx.serialization.Serializable

enum class Status {
    OK, WARNING, CRITICAL
}

@Serializable
data class Report(val status: Status?, val text: String) {
    override fun toString(): String {
        return if (status != null) "$status $text" else text
    }
}

fun Report.limitPriority(maxStatus: Status) =
    if (status == null) this
    else copy(
        status = status.coerceAtMost(maxStatus)
    )