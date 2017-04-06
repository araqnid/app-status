package org.araqnid.appstatus.demo

import com.fasterxml.jackson.databind.ObjectWriter
import org.araqnid.appstatus.StatusComponent
import org.araqnid.appstatus.StatusPage
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StatusServlet(val components: Collection<StatusComponent>) : HttpServlet() {
    val statusPageWriter: ObjectWriter = objectMapper.writerFor(StatusPage::class.java)

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val statusPage = StatusPage.build(components)
        resp.contentType = "application/json"
        resp.outputStream.use { output ->
            statusPageWriter.writeValue(output, statusPage)
        }
    }
}
