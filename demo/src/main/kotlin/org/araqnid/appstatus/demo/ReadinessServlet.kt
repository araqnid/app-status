package org.araqnid.appstatus.demo

import org.araqnid.appstatus.AppStatus
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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