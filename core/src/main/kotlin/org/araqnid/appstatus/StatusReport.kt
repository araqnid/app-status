package org.araqnid.appstatus

data class StatusReport(val priority: Priority, val text: String) : ReportConfigurable<StatusReport> {
    enum class Priority {
        INFO, OK, WARNING, CRITICAL
    }

    override fun mapText(fn: (String) -> String) = copy(text = fn(text))
    override fun limitPriority(maxPriority: Priority) = if (priority > maxPriority) copy(priority = maxPriority) else this

    operator fun plus(moreText: String) = copy(text = text + moreText)

    override fun toString(): String = "$priority $text"
}
