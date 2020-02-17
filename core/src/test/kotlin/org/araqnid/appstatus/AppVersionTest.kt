package org.araqnid.appstatus

import com.natpryce.hamkrest.anything
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import org.junit.Test

class AppVersionTest {
    @Test
    fun `provides a version from package manifest`() {
        // can't really verify much more than that it doesn't crash
        assertThat(AppVersion.fromPackageManifest(AppVersion::class.java), present(anything))
    }
}
