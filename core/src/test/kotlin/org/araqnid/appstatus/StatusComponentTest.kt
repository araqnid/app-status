package org.araqnid.appstatus

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class StatusComponentTest {
    private val report = StatusReport(StatusReport.Priority.WARNING, "Example")
    private val component = StatusComponent.from("test", "Test") { report }

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
}
