package org.araqnid.appstatus

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class StatusPageTest {
    @Test fun `empty status page has status ok`() {
        val page = StatusPage.build(emptyList())
        assertThat(page, equalTo(StatusPage(StatusReport.Priority.OK, emptyMap())))
    }

    @Test fun `status page with info component has status ok`() {
        val page = StatusPage.build(listOf(StatusComponent.info("id", "label", "content")))
        assertThat(page, equalTo(StatusPage(StatusReport.Priority.OK,
                mapOf("id" to StatusPage.LabelledStatusReport("label", StatusReport(StatusReport.Priority.INFO, "content"))))))
    }

    @Test fun `status page with info and ok component has status ok`() {
        val infoComponent = StatusComponent.from("id1", "label1", StatusReport(StatusReport.Priority.INFO, "info content"))
        val okComponent = StatusComponent.from("id2", "label2", StatusReport(StatusReport.Priority.OK, "ok content"))
        val page = StatusPage.build(listOf(infoComponent, okComponent))
        assertThat(page, equalTo(StatusPage(StatusReport.Priority.OK,
                mapOf("id1" to StatusPage.LabelledStatusReport("label1", StatusReport(StatusReport.Priority.INFO, "info content")),
                        "id2" to StatusPage.LabelledStatusReport("label2", StatusReport(StatusReport.Priority.OK, "ok content"))))))
    }

    @Test fun `status page with ok and warning component has status warning`() {
        val okComponent = StatusComponent.from("id1", "label1", StatusReport(StatusReport.Priority.OK, "ok content"))
        val warningComponent = StatusComponent.from("id2", "label2", StatusReport(StatusReport.Priority.WARNING, "warning content"))
        val page = StatusPage.build(listOf(okComponent, warningComponent))
        assertThat(page, equalTo(StatusPage(StatusReport.Priority.WARNING,
                mapOf("id1" to StatusPage.LabelledStatusReport("label1", StatusReport(StatusReport.Priority.OK, "ok content")),
                        "id2" to StatusPage.LabelledStatusReport("label2", StatusReport(StatusReport.Priority.WARNING, "warning content"))))))
    }

    @Test fun `status page with warning and critical component has status critical`() {
        val warningComponent = StatusComponent.from("id1", "label1", StatusReport(StatusReport.Priority.WARNING, "warning content"))
        val criticalComponent = StatusComponent.from("id2", "label2", StatusReport(StatusReport.Priority.CRITICAL, "critical content"))
        val page = StatusPage.build(listOf(warningComponent, criticalComponent))
        assertThat(page, equalTo(StatusPage(StatusReport.Priority.CRITICAL,
                mapOf("id1" to StatusPage.LabelledStatusReport("label1", StatusReport(StatusReport.Priority.WARNING, "warning content")),
                        "id2" to StatusPage.LabelledStatusReport("label2", StatusReport(StatusReport.Priority.CRITICAL, "critical content"))))))
    }
}
