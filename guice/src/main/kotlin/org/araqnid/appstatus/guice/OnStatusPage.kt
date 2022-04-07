package org.araqnid.appstatus.guice

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class OnStatusPage(val label: String = "")
