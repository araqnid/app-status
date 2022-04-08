package org.araqnid.appstatus.demo

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.araqnid.appstatus.AppStatus

class ReadinessServlet(private val appStatus: AppStatus) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val ready = appStatus.ready
        val useStatusCode = req.queryString == "check"
        resp.contentType = "text/plain"
        resp.characterEncoding = "UTF-8"
        if (useStatusCode && !ready)
            resp.status = 400
        resp.writer.use { writer ->
            writer.print(if (ready) "READY" else "NOT_READY")
        }
    }
}