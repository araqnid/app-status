package org.araqnid.appstatus.demo

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.araqnid.appstatus.AppStatus
import org.araqnid.appstatus.AppStatusReport
import org.araqnid.appstatus.Status
import org.araqnid.appstatus.toReport
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
private data class StatusPage(val status: Status, val components: Map<String, StatusPageReport>)

private fun AppStatusReport.toPage() = StatusPage(
    status = Status.OK,
    components = reports.mapValues { (_, report) ->
        StatusPageReport(
            report.name,
            report.status?.toString() ?: "INFO",
            report.text
        )
    }
)
