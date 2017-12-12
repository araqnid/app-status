package org.araqnid.appstatus

import com.google.inject.AbstractModule
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Key
import com.google.inject.Module
import com.google.inject.TypeLiteral
import com.google.inject.name.Names
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import javax.inject.Named
import javax.inject.Qualifier

class ComponentsBuilderTest {
    @Rule @JvmField val thrown = ExpectedException.none()!!

    @Test fun component_returning_string_from_property() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            val fixedString = "test text"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("fixedString"))
                           and has(StatusComponent::label, equalTo("fixedString"))
                           and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "test text")))
        ))
    }

    @Test fun component_returning_report_from_property() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            val fixedReport = StatusReport(StatusReport.Priority.OK, "test report")
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("fixedReport"))
                        and has(StatusComponent::label, equalTo("fixedReport"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.OK, "test report")))
        ))
    }

    @Test fun component_returning_string_from_property_with_get_accessor() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            val fixedString
                get() = "test text"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("fixedString"))
                        and has(StatusComponent::label, equalTo("fixedString"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "test text")))
        ))
    }

    @Test fun component_returning_report_from_property_with_get_accessor() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            val fixedReport
                get() = StatusReport(StatusReport.Priority.OK, "test report")
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("fixedReport"))
                        and has(StatusComponent::label, equalTo("fixedReport"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.OK, "test report")))
        ))
    }

    @Test fun component_returning_string_from_no_args() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            fun fixedString() = "test text"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("fixedString"))
                        and has(StatusComponent::label, equalTo("fixedString"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "test text")))
        ))
    }

    @Test fun component_returning_report_from_no_args() {
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings))
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            fun fixedReport() = StatusReport(StatusReport.Priority.OK, "test report")
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("fixedReport"))
                        and has(StatusComponent::label, equalTo("fixedReport"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.OK, "test report")))
        ))
    }

    @Test fun component_returning_string_from_one_arg() {
        val value = "xyzzy"
        data class Test(val value: String)
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Test::class.java).toInstance(Test(value))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            fun parameterisedString(v: Test) = "text: ${v.value}"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("parameterisedString"))
                        and has(StatusComponent::label, equalTo("parameterisedString"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "text: $value")))
        ))
    }

    @Test fun component_returning_string_from_one_parameterised_arg() {
        val value = "xyzzy"
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Key.get(object : TypeLiteral<ParameterisedTest<@JvmSuppressWildcards String>>(){})).toInstance(ParameterisedTest(value))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            fun parameterisedString(v: ParameterisedTest<String>) = "text: ${v.value}"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("parameterisedString"))
                        and has(StatusComponent::label, equalTo("parameterisedString"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "text: $value")))
        ))
    }

    @Test fun component_returning_string_from_two_args() {
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
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            fun parameterisedString(a: Alpha, b: Beta) = "text: ${a.value} - ${b.value}"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("parameterisedString"))
                        and has(StatusComponent::label, equalTo("parameterisedString"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "text: $alphaValue - $betaValue")))
        ))
    }

    @Test fun parameter_qualifying_annotation_instance_used() {
        val value = "xyzzy"
        data class Test(val value: String)
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Test::class.java).annotatedWith(Names.named("test")).toInstance(Test(value))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            fun parameterisedString(@Named("test") v: Test) = "text: ${v.value}"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("parameterisedString"))
                        and has(StatusComponent::label, equalTo("parameterisedString"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "text: $value")))
        ))
    }

    @Test fun parameter_qualifying_annotation_class_used() {
        val value = "xyzzy"
        data class Test(val value: String)
        val injector = Guice.createInjector(Module(Binder::requireExplicitBindings), object : AbstractModule() {
            override fun configure() {
                bind(Test::class.java).annotatedWith(TestAnnotation::class.java).toInstance(Test(value))
            }
        })
        val componentsBuilder = ComponentsBuilder(injector)
        val statusComponents = componentsBuilder.buildStatusComponents(object {
            @OnStatusPage
            fun parameterisedString(@TestAnnotation v: Test) = "text: ${v.value}"
        })
        assertThat(statusComponents, containsOnly(
                has(StatusComponent::id, equalTo("parameterisedString"))
                        and has(StatusComponent::label, equalTo("parameterisedString"))
                        and has(StatusComponent::report, equalTo(StatusReport(StatusReport.Priority.INFO, "text: $value")))
        ))
    }

    @Qualifier
    annotation class TestAnnotation

    data class ParameterisedTest<out T>(val value: T)
}
