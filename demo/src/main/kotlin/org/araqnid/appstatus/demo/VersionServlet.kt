package org.araqnid.appstatus.demo

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.araqnid.appstatus.AppStatus
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class VersionServlet(appStatus: AppStatus) : HttpServlet() {
    private val appVersion = AppVersion(appStatus.applicationName, appStatus.applicationVersion)

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = "application/json"
        resp.outputStream.use { output ->
            Json.encodeToStream(appVersion, output)
        }
    }
}

@Serializable
private data class AppVersion(val name: String, val version: String)
