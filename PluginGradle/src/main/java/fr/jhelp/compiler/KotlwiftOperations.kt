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
fun cloneUpdateKotlwiftDependencies(directoryCloned: File, clonedSourceDirectories: ArrayList<File>)
{
    createDirectoryAndHierarchyIfNeed(directoryCloned)

    for (kotlwiftImplementation in kotlwiftDependencies)
    {
        val start = max(0, kotlwiftImplementation.gitToClone.lastIndexOf('/'))
        var end = kotlwiftImplementation.gitToClone.lastIndexOf('.')

        if (end < start + 1)
        {
            end = kotlwiftImplementation.gitToClone.length
        }

        val directoryName = kotlwiftImplementation.gitToClone.substring(start + 1, end)
        val directoryClone = File(directoryCloned, directoryName)

        if (!directoryClone.exists())
        {
            gitClone(directoryCloned, kotlwiftImplementation.gitToClone, directoryName)
        }

        checkoutUpdateBranch(directoryClone, kotlwiftImplementation.branch)
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

    if(kotlwiftCompileVerbose) {
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
