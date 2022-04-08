package org.araqnid.appstatus

import kotlinx.serialization.Serializable

@Serializable
data class AppStatusReport(
    val name: String,
    val version: String,
    val ready: Boolean,
    val reports: Map<String, ComponentReport>
)

@Serializable
data class ComponentReport(
    val name: String,
    val status: Status?,
    val text: String
)

fun AppStatus.toReport(): AppStatusReport {
    return AppStatusReport(
        name = this.applicationName,
        version = this.applicationVersion,
        ready = this.ready,
        reports = this.components.associate { component ->
            component.id to component.report().let { report ->
                ComponentReport(
                    name = component.name,
                    status = report.status,
                    text = report.text
                )
            }
        }
    )
}
