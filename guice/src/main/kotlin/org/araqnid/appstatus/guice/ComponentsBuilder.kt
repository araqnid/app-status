package org.araqnid.appstatus.guice

import com.google.common.collect.ImmutableList
import com.google.inject.BindingAnnotation
import com.google.inject.Injector
import com.google.inject.Key
import org.araqnid.appstatus.Component
import org.araqnid.appstatus.MutableAppStatus
import org.araqnid.appstatus.Report
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Qualifier
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType

class ComponentsBuilder @Inject constructor(val injector: Injector) {
    fun buildStatusComponents(vararg sources: Any): Collection<Component> {
        val components = mutableListOf<Component>()
        sources.forEach { source ->
            source.javaClass.kotlin.functions.mapNotNullTo(components) { makeComponentFromFunction(source, it) }
            source.javaClass.kotlin.memberProperties.mapNotNullTo(components) { makeComponentFromProperty(source, it) }
        }
        return ImmutableList.copyOf(components)
    }

    fun buildAppStatus(name: String, version: String, vararg sources: Any): MutableAppStatus {
        return MutableAppStatus(name, version).apply {
            for (component in buildStatusComponents(*sources)) {
                register(component)
            }
        }
    }

    private fun makeComponentFromProperty(source: Any, property: KProperty1<*, *>): Component? {
        val annotation = property.findAnnotation<OnStatusPage>() ?: return null
        val id = property.name
        val label = if (annotation.label.isEmpty()) id else annotation.label
        val method = property.getter.javaMethod ?: throw IllegalStateException("No getter for $property")
        val providers = providersForMethod(method)
        return when (property.returnType.javaType) {
            Report::class.java -> Component.from(
                    id,
                    label,
                    wrapInvocation(Report::class.java,
                            method,
                            source,
                            providers))
            String::class.java -> Component.info(id,
                    label,
                    wrapInvocation(String::class.java, method, source, providers))
            else -> throw IllegalStateException("Invalid type from @OnStatusPage property $property: ${property.returnType.javaType}")
        }
    }

    private fun makeComponentFromFunction(source: Any, function: KFunction<*>): Component? {
        val annotation = function.findAnnotation<OnStatusPage>() ?: return null
        if (function.name.indexOf('$') >= 0) return null
        val id = function.name
        val label = if (annotation.label.isEmpty()) id else annotation.label
        val method = function.javaMethod ?: throw IllegalStateException("No method for $function")
        val providers = providersForMethod(method)
        return when (function.returnType.javaType) {
            Report::class.java -> Component.from(
                    id,
                    label,
                    wrapInvocation(Report::class.java,
                            method,
                            source,
                            providers))
            String::class.java -> Component.info(id,
                    label,
                    wrapInvocation(String::class.java, method, source, providers))
            else -> throw IllegalStateException("Invalid return type from @OnStatusPage function $function: ${function.returnType.javaType}")
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
        val methodHandle = MethodHandles.lookup().unreflect(method).bindTo(source)
        return when (providers.size) {
            0 -> {
                { returnType.cast(methodHandle.invoke()) }
            }
            1 -> {
                { returnType.cast(methodHandle.invoke(providers[0].get())) }
            }
            else -> {
                val spreaderHandle = methodHandle.asSpreader(Array<Any>::class.java, providers.size);
                {
                    val args = Array<Any?>(providers.size) { providers[it].get() }
                    returnType.cast(spreaderHandle.invoke(args))
                }
            }
        }
    }
}
