package org.araqnid.appstatus.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.araqnid.appstatus.AppVersion
import org.araqnid.appstatus.Readiness
import org.araqnid.appstatus.StatusComponent
import org.araqnid.appstatus.StatusReport
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.time.Clock

private val clock: Clock = Clock.systemDefaultZone()
private val jiffies: Int
    get() = (clock.millis() / 1000L).toInt()

private val statusComponents = listOf(
        StatusComponent.info("info-test", "Test info component") { "jiffies = $jiffies" },
        StatusComponent.from("report-test", "Prioritised component") {
            when (jiffies / 2 % 3) {
                1 -> StatusReport(StatusReport.Priority.WARNING, "the warning content")
                2 -> StatusReport(StatusReport.Priority.CRITICAL, "the critical content")
                else -> StatusReport(StatusReport.Priority.OK, "the ok content")
            }
        }
)

private fun readiness() = if (jiffies % 2 == 0) Readiness.READY else Readiness.NOT_READY

object Main {
    @JvmStatic fun main(args: Array<String>) {
        val jettyServer = Server(System.getenv("PORT")?.toInt() ?: 8080)
        val servletContext = ServletContextHandler()
        servletContext.resourceBase = System.getenv("DOCUMENT_ROOT") ?: "ui/build/site"
        servletContext.addServlet(ServletHolder(StatusServlet(statusComponents)), "/_api/info/status")
        servletContext.addServlet(ServletHolder(ReadinessServlet(::readiness)), "/_api/info/readiness")
        servletContext.addServlet(ServletHolder(VersionServlet(AppVersion(null, null, null))), "/_api/info/version")
        servletContext.addServlet(DefaultServlet::class.java, "/")
        jettyServer.handler = servletContext
        jettyServer.stopAtShutdown = true
        jettyServer.start()
    }
}

val objectMapper: ObjectMapper = jacksonObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
