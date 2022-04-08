package org.araqnid.appstatus

import org.araqnid.kotlin.assertthat.assertThat
import org.araqnid.kotlin.assertthat.equalTo
import org.junit.Test

class ReportTest {
    private val baseReport = Report(Report.Status.WARNING, "basic text")

    @Test
    fun `toString has specific output`() {
        assertThat(baseReport.toString(), equalTo("WARNING basic text"))
    }

    @Test
    fun `can limit priority of report`() {
        assertThat(baseReport.limitPriority(Report.Status.OK), equalTo(Report(Report.Status.OK, "basic text")))
    }
}
