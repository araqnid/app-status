package org.araqnid.appstatus

enum class Readiness {
    READY, NOT_READY;

    companion object {
        fun readyWhen(predicate: () -> Boolean) = if (predicate()) READY else NOT_READY
    }
}
