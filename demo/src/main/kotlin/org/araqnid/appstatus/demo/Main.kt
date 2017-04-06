package org.araqnid.appstatus.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.araqnid.appstatus.AppVersion
import org.araqnid.appstatus.Readiness
import org.araqnid.appstatus.StatusComponent
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

object Main {
    val statusComponents = listOf(StatusComponent.info("test", "Test", "test"))

    @JvmStatic fun main(args: Array<String>) {
        val jettyServer = Server(System.getenv("PORT")?.toInt() ?: 8080)
        val servletContext = ServletContextHandler()
        servletContext.resourceBase = "www"
        servletContext.addServlet(ServletHolder(StatusServlet(statusComponents)), "/_api/info/status")
        servletContext.addServlet(ServletHolder(ReadinessServlet(Readiness.READY)), "/_api/info/readiness")
        servletContext.addServlet(ServletHolder(VersionServlet(AppVersion(null, null, null))), "/_api/info/version")
        servletContext.addServlet(DefaultServlet::class.java, "/")
        jettyServer.handler = servletContext
        jettyServer.stopAtShutdown = true
        jettyServer.start()
    }
}

val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule())
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
