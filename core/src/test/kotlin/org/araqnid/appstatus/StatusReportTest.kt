package org.araqnid.appstatus

import org.araqnid.kotlin.assertthat.assertThat
import org.araqnid.kotlin.assertthat.equalTo
import org.junit.Test

class StatusReportTest {
    private val baseReport = StatusReport(StatusReport.Priority.WARNING, "basic text")

    @Test
    fun `toString has specific output`() {
        assertThat(baseReport.toString(), equalTo("WARNING basic text"))
    }

    @Test
    fun `can transform text of report`() {
        assertThat(baseReport.mapText { "$it extra" }, equalTo(StatusReport(StatusReport.Priority.WARNING, "basic text extra")))
    }

    @Test
    fun `can append text with addition operator`() {
        assertThat(baseReport + " extra", equalTo(StatusReport(StatusReport.Priority.WARNING, "basic text extra")))
    }

    @Test
    fun `can limit priority of report`() {
        assertThat(baseReport.limitPriority(StatusReport.Priority.OK), equalTo(StatusReport(StatusReport.Priority.OK, "basic text")))
    }
}
