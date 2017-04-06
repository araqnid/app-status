package org.araqnid.appstatus.demo

import org.araqnid.appstatus.Readiness
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ReadinessServlet(val readiness: () -> Readiness) : HttpServlet() {
    constructor(value: Readiness) : this(readiness = { value })

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = "text/plain"
        resp.characterEncoding = "UTF-8"
        resp.writer.use { writer ->
            writer.print(readiness())
        }
    }
}