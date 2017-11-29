package org.araqnid.appstatus

data class StatusReport(val priority: Priority, val text: String) {
    override fun toString(): String {
        return "$priority $text"
    }

    enum class Priority {
        INFO, OK, WARNING, CRITICAL;

        companion object {
            @JvmStatic fun higher(l: Priority, r: Priority): Priority = if (l > r) l else r
        }
    }

    fun mapText(fn: (String) -> String) = copy(text = fn(text))
    fun limitPriority(maxPriority: Priority) = if (priority > maxPriority) copy(priority = maxPriority) else this

    operator fun plus(moreText: String) = copy(text = text + moreText)
}
