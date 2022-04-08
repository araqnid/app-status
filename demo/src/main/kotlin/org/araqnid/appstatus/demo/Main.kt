package org.araqnid.appstatus.demo

import org.araqnid.appstatus.Component
import org.araqnid.appstatus.MutableAppStatus
import org.araqnid.appstatus.Report
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.time.Clock

private val clock: Clock = Clock.systemDefaultZone()
private val jiffies: Int
    get() = (clock.millis() / 1000L).toInt()

private val appStatus = MutableAppStatus("demo", "demo").apply {
    register(Component.info("info-test", "Test info component") { "jiffies = $jiffies" })
    register(Component.from("report-test", "Prioritised component") {
        when (jiffies / 2 % 3) {
            1 -> Report(Report.Status.WARNING, "the warning content")
            2 -> Report(Report.Status.CRITICAL, "the critical content")
            else -> Report(Report.Status.OK, "the ok content")
        }
    })
}

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val jettyServer = Server(System.getenv("PORT")?.toInt() ?: 8080).apply {
            handler = ServletContextHandler().apply {
                resourceBase = System.getenv("DOCUMENT_ROOT") ?: "ui/build/site"
                addServlet(ServletHolder(StatusServlet(appStatus)), "/_api/info/status")
                addServlet(ServletHolder(ReadinessServlet(appStatus)), "/_api/info/readiness")
                addServlet(ServletHolder(VersionServlet(appStatus)), "/_api/info/version")
                addServlet(DefaultServlet::class.java, "/")
                insertHandler(GzipHandler())
            }
            stopAtShutdown = true
        }
        jettyServer.start()
    }
}
