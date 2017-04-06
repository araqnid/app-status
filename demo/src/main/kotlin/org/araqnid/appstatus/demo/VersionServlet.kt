package org.araqnid.appstatus.demo

import com.fasterxml.jackson.databind.ObjectWriter
import org.araqnid.appstatus.AppVersion
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class VersionServlet(val appVersion: AppVersion) : HttpServlet() {
    val versionWriter: ObjectWriter = objectMapper.writerFor(AppVersion::class.java)

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = "application/json"
        resp.outputStream.use { output ->
            versionWriter.writeValue(output, appVersion)
        }
    }
}