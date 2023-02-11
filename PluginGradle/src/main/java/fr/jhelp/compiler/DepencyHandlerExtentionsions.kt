package fr.jhelp.compiler

import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * Extension for specify a Kotlin light project dependency
 */
fun DependencyHandlerScope.kotlwiftImplementation(dependencyCreator: KotlwiftImplementation.() -> Unit): Dependency?
{
    val kotlwiftImplementation = KotlwiftImplementation()
    dependencyCreator(kotlwiftImplementation)

    if (!kotlwiftDependencies.contains(kotlwiftImplementation))
    {
        kotlwiftDependencies += kotlwiftImplementation
    }

    return this.add("api", kotlwiftImplementation.dependencyNotation)
}
