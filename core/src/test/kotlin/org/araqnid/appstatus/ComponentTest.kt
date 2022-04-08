package org.araqnid.appstatus

import org.araqnid.kotlin.assertthat.assertThat
import org.araqnid.kotlin.assertthat.equalTo
import org.araqnid.kotlin.assertthat.sameInstance
import org.junit.Test

class ComponentTest {
    private val report = Report(Status.WARNING, "Example")
    private val component = Component.from("test", "Test") { report }

    @Test
    fun `can map text of produced reports`() {
        assertThat(
            component.mapText { "$it extra" }.report(),
            equalTo(report.copy(text = "${report.text} extra"))
        )
    }

    @Test
    fun `can limit priority of produced reports`() {
        assertThat(
            component.limitPriority(Status.OK).report(),
            equalTo(report.copy(status = Status.OK))
        )
    }

    @Test
    fun `can arbitrarily transform produced reports`() {
        assertThat(
            component.mapReport { Report(null, "suppressed: $it") }.report(),
            equalTo(Report(null, "suppressed: WARNING Example"))
        )
    }

    @Test
    fun `can build with report-supplier convenience methods`() {
        assertThat(Component.from("a", "b", report).report(), sameInstance(report))
        assertThat(Component.from("a", "b") { report }.report(), sameInstance(report))
    }

    @Test
    fun `can build with string-supplier convenience methods`() {
        val expected = Report(null, "test")
        assertThat(Component.info("a", "b", "test").report(), equalTo(expected))
        assertThat(Component.info("a", "b") { "test" }.report(), equalTo(expected))
    }
}
