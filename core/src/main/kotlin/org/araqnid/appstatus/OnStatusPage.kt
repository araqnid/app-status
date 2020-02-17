package org.araqnid.appstatus

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class OnStatusPage(val label: String = "")
