package org.araqnid.appstatus

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

interface AppStatus {
    val applicationName: String
    val applicationVersion: String
    val ready: Boolean
    val components: List<Component>
}

class MutableAppStatus(override val applicationName: String, override val applicationVersion: String) : AppStatus {
    private val readyFlag = AtomicBoolean(false)
    private val componentsList = CopyOnWriteArrayList<Component>()

    override val ready: Boolean
        get() = readyFlag.get()

    override val components: List<Component>
        get() = componentsList

    fun markReady() {
        readyFlag.set(true)
    }

    fun register(component: Component) {
        componentsList.add(component)
    }
}

