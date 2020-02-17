package org.araqnid.appstatus

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.junit.Test

class StatusComponentTest {
    private val report = StatusReport(StatusReport.Priority.WARNING, "Example")
    private val component = StatusComponent("test", "Test") { report }

    @Test
    fun `can map text of produced reports`() {
        assertThat(component.mapText { "$it extra" }.report(),
                equalTo(report + " extra"))
    }

    @Test
    fun `can limit priority of produced reports`() {
        assertThat(component.limitPriority(StatusReport.Priority.OK).report(),
                equalTo(report.copy(priority = StatusReport.Priority.OK)))
    }

    @Test
    fun `can arbitrarily transform produced reports`() {
        assertThat(component.mapReport { StatusReport(StatusReport.Priority.INFO, "suppressed: $it") }.report(),
                equalTo(StatusReport(StatusReport.Priority.INFO, "suppressed: WARNING Example")))
    }

    @Test
    fun `can build with report-supplier convenience methods`() {
        assertThat(StatusComponent.from("a", "b", report).report(), sameInstance(report))
        assertThat(StatusComponent.from("a", "b", java.util.function.Supplier { report }).report(), sameInstance(report))
        assertThat(StatusComponent.from("a", "b") { report }.report(), sameInstance(report))
    }

    @Test
    fun `can build with string-supplier convenience methods`() {
        val expected = StatusReport(StatusReport.Priority.INFO, "test")
        assertThat(StatusComponent.info("a", "b", "test").report(), equalTo(expected))
        assertThat(StatusComponent.info("a", "b", java.util.function.Supplier { "test" }).report(), equalTo(expected))
        assertThat(StatusComponent.info("a", "b") { "test" }.report(), equalTo(expected))
    }
}
