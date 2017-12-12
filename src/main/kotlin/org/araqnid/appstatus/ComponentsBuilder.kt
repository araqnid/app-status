package org.araqnid.appstatus

import com.google.common.collect.ImmutableList
import com.google.inject.BindingAnnotation
import com.google.inject.Injector
import com.google.inject.Key
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Qualifier
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType

class ComponentsBuilder @Inject constructor(val injector: Injector) {
    fun buildStatusComponents(vararg sources: Any): Collection<StatusComponent> {
        val components = mutableListOf<StatusComponent>()
        sources.forEach { source ->
            source.javaClass.methods.mapNotNullTo(components) { makeComponent(source, it) }
            source.javaClass.kotlin.memberProperties.mapNotNullTo(components) { makeComponentFromProperty(source, it) }
        }
        return ImmutableList.copyOf(components)
    }

    private fun makeComponentFromProperty(source: Any, property: KProperty1<*, *>): StatusComponent? {
        val annotation = property.findAnnotation<OnStatusPage>() ?: return null
        val id = property.name
        val label = if (annotation.label.isEmpty()) id else annotation.label
        val method = property.getter.javaMethod ?: throw IllegalStateException("No getter for $property")
        val providers = providersForMethod(method)
        return when (property.returnType.javaType) {
            StatusReport::class.java -> StatusComponent.from(id, label, wrapInvocation(StatusReport::class.java, method, source, providers))
            String::class.java -> StatusComponent.info(id, label, wrapInvocation(String::class.java, method, source, providers))
            else -> throw IllegalStateException("Invalid type from @OnStatusPage property: $property")
        }
    }

    private fun makeComponent(source: Any, method: Method): StatusComponent? {
        val annotation = method.getAnnotation(OnStatusPage::class.java) ?: return null
        if (method.name.indexOf('$') >= 0) return null
        val id = method.name
        val label = if (annotation.label.isEmpty()) id else annotation.label
        val providers = providersForMethod(method)
        return when (method.returnType) {
            StatusReport::class.java -> StatusComponent.from(id, label, wrapInvocation(StatusReport::class.java, method, source, providers))
            String::class.java -> StatusComponent.info(id, label, wrapInvocation(String::class.java, method, source, providers))
            else -> throw IllegalStateException("Invalid return type from @OnStatusPage function: $method")
        }
    }

    private fun providersForMethod(method: Method): Array<Provider<*>> {
        return Array(method.parameterCount) { index ->
            val type = method.genericParameterTypes[index]
            val qualifier = method.parameterAnnotations[index].find { paramAnnotation ->
                val metaAnnotations = paramAnnotation.annotationClass.annotations
                metaAnnotations.count { it is Qualifier || it is BindingAnnotation } > 0
            }
            val key = if (qualifier == null)
                Key.get(type)
            else
                Key.get(type, qualifier)
            injector.getProvider(key)
        }
    }

    private fun <T> wrapInvocation(returnType: Class<T>, method: Method, source: Any, providers: Array<Provider<*>>): () -> T {
        // Kotlin seems to always call MethodHandle.invoke by passing an Object[], hence we always have to use a spreading handle
        val methodHandle = MethodHandles.lookup().unreflect(method)
        return when (providers.size) {
            0 -> {
                val spreaderHandle = methodHandle.asSpreader(Array<Any>::class.java, 1);
                { returnType.cast(spreaderHandle.invoke(source)) }
            }
            1 -> {
                val provider = providers[0]
                val spreaderHandle = methodHandle.asSpreader(Array<Any>::class.java, 2);
                { returnType.cast(spreaderHandle.invoke(source, provider.get())) }
            }
            else -> {
                val spreaderHandle = methodHandle.asSpreader(Array<Any>::class.java, providers.size + 1);
                {
                    val args = Array<Any?>(providers.size) { providers[it].get() }
                    returnType.cast(spreaderHandle.invoke(source, *args))
                }
            }
        }
    }
}
