package fr.jhelp.compiler

import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * Extension for specify a Kotlin light project dependency
 */
fun DependencyHandlerScope.kotlinLightImplementation(dependencyCreator: KotlinLightImplementation.() -> Unit): Dependency?
{
    val kotlinLightImplementation = KotlinLightImplementation()
    dependencyCreator(kotlinLightImplementation)

    if (!kotlinLightDependencies.contains(kotlinLightImplementation))
    {
        kotlinLightDependencies += kotlinLightImplementation
    }

    return this.add("api", kotlinLightImplementation.dependencyNotation)
}
