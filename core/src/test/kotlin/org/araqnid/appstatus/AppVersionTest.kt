package org.araqnid.appstatus

import org.araqnid.kotlin.assertthat.anything
import org.araqnid.kotlin.assertthat.assertThat
import org.araqnid.kotlin.assertthat.present
import org.junit.Test

class AppVersionTest {
    @Test
    fun `provides a version from package manifest`() {
        // can't really verify much more than that it doesn't crash
        assertThat(AppVersion.fromPackageManifest(AppVersion::class.java), present(anything))
    }
}
