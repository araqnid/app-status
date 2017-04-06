package org.araqnid.appstatus

data class AppVersion(val title: String?, val version: String?, val vendor: String?) {
    companion object {
        fun fromPackageManifest(anchor: Class<*>) = with(anchor.`package`) { AppVersion(title = implementationTitle, vendor = implementationVendor, version = implementationVersion) }
    }
}
