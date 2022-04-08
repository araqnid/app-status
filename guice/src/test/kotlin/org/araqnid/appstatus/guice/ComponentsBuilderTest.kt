package org.araqnid.appstatus.guice

import com.google.inject.*
import com.google.inject.name.Names
import org.araqnid.appstatus.ComponentReport
import org.araqnid.appstatus.Report
import org.araqnid.appstatus.toReport
import org.araqnid.kotlin.assertthat.assertThat
import org.araqnid.kotlin.assertthat.equalTo
import org.junit.Test
import javax.inject.Named
import javax.inject.Qualifier

class ComponentsBuilderTest {
    @Test
    fun component_returning_string_from_property() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            val fixedString = "test text"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("fixedString" to ComponentReport("fixedString", null, "test text")))
        )
    }

    @Test
    fun component_returning_report_from_property() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            val fixedReport = Report(Report.Status.OK, "test report")
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("fixedReport" to ComponentReport("fixedReport", Report.Status.OK, "test report")))
        )
    }

    @Test
    fun component_returning_string_from_property_with_get_accessor() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            val fixedString
                get() = "test text"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("fixedString" to ComponentReport("fixedString", null, "test text")))
        )
    }

    @Test
    fun component_returning_report_from_property_with_get_accessor() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            val fixedReport
                get() = Report(Report.Status.OK, "test report")
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("fixedReport" to ComponentReport("fixedReport", Report.Status.OK, "test report")))
        )
    }

    @Test
    fun component_returning_string_from_no_args() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            fun fixedString() = "test text"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("fixedString" to ComponentReport("fixedString", null, "test text")))
        )
    }

    @Test
    fun component_returning_report_from_no_args() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            fun fixedReport() = Report(Report.Status.OK, "test report")
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("fixedReport" to ComponentReport("fixedReport", Report.Status.OK, "test report")))
        )
    }

    @Test
    fun component_returning_string_from_one_arg() {
        val value = "xyzzy"

        data class Test(val value: String)

        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Test::class.java).toInstance(Test(value))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            fun parameterisedString(v: Test) = "text: ${v.value}"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("parameterisedString" to ComponentReport("parameterisedString", null, "text: $value")))
        )
    }

    @Test
    fun component_returning_string_from_one_parameterised_arg() {
        val value = "xyzzy"
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Key.get(object : TypeLiteral<ParameterisedTest<@JvmSuppressWildcards String>>() {})).toInstance(
                    ParameterisedTest(value)
                )
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            fun parameterisedString(v: ParameterisedTest<String>) = "text: ${v.value}"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("parameterisedString" to ComponentReport("parameterisedString", null, "text: $value")))
        )
    }

    @Test
    fun component_returning_string_from_two_args() {
        val alphaValue = "xyzzy"
        val betaValue = "abcde"

        data class Alpha(val value: String)
        data class Beta(val value: String)

        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Alpha::class.java).toInstance(Alpha(alphaValue))
                bind(Beta::class.java).toInstance(Beta(betaValue))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            fun parameterisedString(a: Alpha, b: Beta) = "text: ${a.value} - ${b.value}"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("parameterisedString" to ComponentReport("parameterisedString", null, "text: $alphaValue - $betaValue")))
        )
    }

    @Test
    fun parameter_qualifying_annotation_instance_used() {
        val value = "xyzzy"

        data class Test(val value: String)

        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Test::class.java).annotatedWith(Names.named("test")).toInstance(Test(value))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            fun parameterisedString(@Named("test") v: Test) = "text: ${v.value}"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("parameterisedString" to ComponentReport("parameterisedString", null, "text: $value")))
        )
    }

    @Test
    fun parameter_qualifying_annotation_class_used() {
        val value = "xyzzy"

        data class Test(val value: String)

        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Test::class.java).annotatedWith(TestAnnotation::class.java).toInstance(Test(value))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val appReport = componentsBuilder.buildAppStatus("test", "test", object {
            @OnStatusPage
            fun parameterisedString(@TestAnnotation v: Test) = "text: ${v.value}"
        }).toReport()

        assertThat(
            appReport.reports,
            equalTo(mapOf("parameterisedString" to ComponentReport("parameterisedString", null, "text: $value")))
        )
    }

    @Qualifier
    annotation class TestAnnotation

    data class ParameterisedTest<out T>(val value: T)
}
