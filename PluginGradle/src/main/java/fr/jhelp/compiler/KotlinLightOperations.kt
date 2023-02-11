package fr.jhelp.compiler

import fr.jhelp.compiler.command.CommandFactory
import fr.jhelp.compiler.command.CommandManager
import fr.jhelp.compiler.command.CompilationErrorCollector
import fr.jhelp.compiler.command.LogVerbosity
import java.io.File
import kotlin.math.max
import kotlin.system.exitProcess

/**
 * Clone (if necessary), and update all Kotlin light project dependencies
 * @param directoryCloned Directory where put cloned projects
 * @param clonedSourceDirectories Collect cloned source file repositories
 */
fun cloneUpdateKotlinLightDependencies(directoryCloned: File, clonedSourceDirectories: ArrayList<File>)
{
    createDirectoryAndHierarchyIfNeed(directoryCloned)

    for (kotlinLightImplementation in kotlinLightDependencies)
    {
        val start = max(0, kotlinLightImplementation.gitToClone.lastIndexOf('/'))
        var end = kotlinLightImplementation.gitToClone.lastIndexOf('.')

        if (end < start + 1)
        {
            end = kotlinLightImplementation.gitToClone.length
        }

        val directoryName = kotlinLightImplementation.gitToClone.substring(start + 1, end)
        val directoryClone = File(directoryCloned, directoryName)

        if (!directoryClone.exists())
        {
            gitClone(directoryCloned, kotlinLightImplementation.gitToClone, directoryName)
        }

        checkoutUpdateBranch(directoryClone, kotlinLightImplementation.branch)
        clonedSourceDirectories += File(directoryClone, KOTLIN_SOURCE_RELATIVE_PATH)
    }
}

/**
 * Transpile all swift sources
 */
fun transpileSwiftSources(fileKotlinToSwiftCommand: File, clonedSourceDirectories: List<File>, projectDirectory: File)
{
    val command = StringBuilder()
    command.append(fileKotlinToSwiftCommand.absolutePath)
    command.append(CommandFactory.commandOS.executableExtension)
    command.append(" -removeOpen -disableAutomaticPublic -header Shared ")

    if(kotlinLightCompileVerbose) {
        command.append(" -verbose ")
    }

    for (clonedSourceDirectory in clonedSourceDirectories)
    {
        command.append(clonedSourceDirectory.absolutePath)
        command.append(" ")
    }

    command.append(File(projectDirectory, KOTLIN_SOURCE_RELATIVE_PATH).absolutePath)
    command.append(" ")
    command.append(File(projectDirectory, SWIFT_SOURCE_RELATIVE_PATH).absolutePath)
    command.append(" ")
    command.append(CommandFactory.commandOS.redirectionFooter)
    CommandManager.launchCommands(arrayOf(command.toString()), projectDirectory, LogVerbosity.FULL)

    if (CompilationErrorCollector.errorCount > 0)
    {
        System.err.println("${CompilationErrorCollector.errorCount} errors in swift conversion")
        exitProcess(-1)
    }
}

/**
 * Transpile all swift Tests
 */
fun transpileSwiftTests(fileKotlinToSwiftCommand: File, clonedSourceDirectories: List<File>, projectDirectory: File)
{
    val command = StringBuilder()
    command.append(fileKotlinToSwiftCommand.absolutePath)
    command.append(CommandFactory.commandOS.executableExtension)
    command.append(" -removeOpen -disableAutomaticPublic -header Shared ")

    for (clonedSourceDirectory in clonedSourceDirectories)
    {
        command.append(clonedSourceDirectory.absolutePath)
        command.append(" ")
    }

    command.append(File(projectDirectory, KOTLIN_SOURCE_RELATIVE_PATH).absolutePath)
    command.append(" ")
    command.append(File(projectDirectory, KOTLIN_TESTS_RELATIVE_PATH).absolutePath)
    command.append(" ")
    command.append(File(projectDirectory, SWIFT_TESTS_RELATIVE_PATH).absolutePath)
    command.append(" ")
    command.append(CommandFactory.commandOS.redirectionFooter)
    CommandManager.launchCommands(arrayOf(command.toString()), projectDirectory, LogVerbosity.FULL)

    if (CompilationErrorCollector.errorCount > 0)
    {
        System.err.println("${CompilationErrorCollector.errorCount} errors in swift tests conversion")
        exitProcess(-1)
    }
}
