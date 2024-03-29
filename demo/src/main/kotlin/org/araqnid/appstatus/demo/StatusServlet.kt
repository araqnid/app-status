package org.araqnid.appstatus.demo

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.araqnid.appstatus.AppStatus
import org.araqnid.appstatus.AppStatusReport
import org.araqnid.appstatus.Report
import org.araqnid.appstatus.toReport

class StatusServlet(private val appStatus: AppStatus) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val page = appStatus.toReport().toPage()
        resp.contentType = "application/json"
        resp.outputStream.use { output ->
            Json.encodeToStream(page, output)
        }
    }
}

@Serializable
private data class StatusPageReport(val label: String, val priority: String, val text: String)

@Serializable
private data class StatusPage(val status: Report.Status?, val components: Map<String, StatusPageReport>)

private fun AppStatusReport.toPage() = StatusPage(
    status = reports.values.mapNotNull { it.status }.maxOrNull(),
    components = reports.mapValues { (_, report) ->
        StatusPageReport(
            report.name,
            report.status?.toString() ?: "INFO",
            report.text
        )
    }
)
