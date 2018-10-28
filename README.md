App-Status
==========

[ ![Build Status](https://travis-ci.org/araqnid/app-status.svg?branch=master)](https://travis-ci.org/araqnid/app-status) [ ![Download](https://api.bintray.com/packages/araqnid/maven/app-status/images/download.svg) ](https://bintray.com/araqnid/maven/app-status/_latestVersion) [ ![Kotlin](https://img.shields.io/badge/kotlin-1.2.71-blue.svg)](http://kotlinlang.org)

When you're running a webservice, you may need a way to monitor if it is currently working, or discovering
problems with it. This library allows for the application to collect together various monitoring components,
and their results can be presented along with an aggregated health as a single resource.

Currently this library only defines and populates a data structure for that page --- the "demo" subproject shows
how some simple servlets can then serve the result. The library also contains a simple UI for loading a JSON
representation of the status and displaying it.

Status data
-----------

The status is divided into three parts:

- Version (just a string representing the application's version)
- Readiness (indicates than the application is OK to serve requests)
- Status components (a list of id/label/status/description items)

### Readiness

"Readiness" is here to address on issue with applications that take a while to start up --- the orchestration that
starts them has to know when it has finally started sufficiently to take requests. Once the application has finally
initialised everything it changes its readiness to "ready" and the orchestrator can (after some last-minute sanity
checks) go ahead and put this app server into the worker pool.

It would also be possible to continuously monitor readiness, and take app servers back out of the pool if they unmark
themselves as ready.

### Status components

This is simply a list of health checks inside the application: each component produces a report that can include a
priority of ok/warning/critical, or just be informational. For example:

Id            | Label               | Priority | Text
--------------|---------------------|----------|-----------------------------------------
jvmVersion    | JVM version         | Info     | 9.0.1
kotlinVersion | Kotlin version      | Info     | 1.2.0
dbConnection  | Database connection | Ok       | Last checked 2s ago
mailQueueSize | Mail queue size     | Warning  | 95 items in queue (oldest is 12m7s old)

The page produces an aggregated priority (which would be "warning" for the above list) and the supplied UI presents
each of these items on a page highlighted in grey/green/yellow/red etc

The status components are produced by adding functions to produce them to an object which is then passed to a
`ComponentsBuilder`. This produces a `Collection<StatusComponent>` that can simply be iterated over to map them to
a `Collection<StatusReport>` and returned.

Some health checks probably should not be performed every time the status page is queried --- for example, in case of
network problems, you don't want the status page to stop loading because the connection to the database is slow/failing,
you want the status page to return quickly and report the failure. Typically you have some (external) way to schedule
those health checks, which these status components simply observe.

The input to `ComponentsBuilder` doesn't have a fixed interface, the builder will scan it for functions or properties
annotated with `@OnStatusPage` and infer how to convert their return type to a status report. Typically these either
simply return a status report or return a string (which is converted to an info status report). The status component's
id is the name of the function/property, and the label is taken from the annotation's parameter, if present. Parameters
to functions are provided by Guice --- the components builder needs to be created with an Injector for this.

For example:

```kotlin
class StatusComponents(private val dbConnectionHealthCheck: HealthCheck) {
    @OnStatusPage("JVM version")
    val jvmVersion: String = System.getProperty("java.version")
    
    @OnStatusPage("Kotlin version")
    val kotlinVersion: String = KotlinVersion.CURRENT.toString()
    
    @OnStatusPage("Database connection")
    fun dbConnection() = dbConnectionHealthCheck.statusReport()
    // injected directly, could also be a property with a get()
    
    @OnStatusPage("Mail queue size")
    fun mailQueueSize(queue: MailQueue /* injected by Guice */) =
        when {
            queue.isEmpty() -> StatusReport(OK, "Queue is empty")
            else -> StatusReport(
                when {
                    queue.size < 50 -> OK
                    queue.size < 500 -> WARNING
                    else -> CRITICAL
                },
                "${queue.size} items in queue (oldest is ${formatDuration(queue.oldest ?: Duration.ZERO)} old)")
        }
}

val components = ComponentsBuilder(injector).buildStatusComponents(StatusComponents(dbConnectionHealthCheck))

fun reportAll() {
   components.forEach { component ->
       val report = component.report()
       println("${component.id} ${report.priority} ${report.text}")
   }
}
```

Another fun thing to worry about: synchronization between the thread requesting a component's status report and the
underlying data supplying it (e.g. the mail queue above may transition from non-empty to empty before requesting the
oldest item's age)

Displaying the data
-------------------

As mentioned, the demo shows how these data can be presented using some simple servlets. It may be better to recruit
`$your_framework` here, the library is agnostic about that. (Although not about dependency injection, currently)

Example app
-----------

The app on https://fuel.araqnid.org/

- See the status page at https://fuel.araqnid.org/_status/
- See the source at https://github.com/araqnid/fuel-log/

Get the library
---------------

App-Status is published on [JCenter](https://bintray.com/bintray/jcenter). You need something like this in
`build.gradle` or `build.gradle.kts`:

```kotlin
repositories {
    jcenter()
}
dependencies {
    compile("org.araqnid:app-status:0.0.13")
}
```

 
Related projects
----------------

Largely inspired by Tucker: https://github.com/tim-group/Tucker
