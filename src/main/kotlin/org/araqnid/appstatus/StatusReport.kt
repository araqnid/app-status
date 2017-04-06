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
}
