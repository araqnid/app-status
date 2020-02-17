package org.araqnid.appstatus

interface ReportConfigurable<out T : ReportConfigurable<T>> {
    fun mapText(fn: (String) -> String): T
    fun limitPriority(maxPriority: StatusReport.Priority): T

    @JvmDefault
    fun mapText(fn: java.util.function.UnaryOperator<String>): T = mapText(fn::apply)
}
