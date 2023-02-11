package fr.jhelp.compiler

import fr.jhelp.compiler.command.CommandFactory
import fr.jhelp.compiler.command.CommandManager
import fr.jhelp.compiler.command.CompilationErrorCollector
import fr.jhelp.compiler.command.LogVerbosity
import java.io.File
import kotlin.system.exitProcess

/**
 * Check if swift working directory exists, if not, it creates it
 */
fun initializeSwiftWorkingDirectoryIfNeed(swiftCommand: String, swiftDirectory: File)
{
    if (!swiftDirectory.exists())
    {
        createDirectoryAndHierarchyIfNeed(swiftDirectory)
        CommandManager.launchCommands(arrayOf("$swiftCommand package init --type executable ${CommandFactory.commandOS.redirectionFooter}"),
                                      swiftDirectory,
                                      LogVerbosity.NONE)

        if (CompilationErrorCollector.errorCount > 0)
        {
            System.err.println("${CompilationErrorCollector.errorCount} errors in swift compilation")
            exitProcess(-1)
        }
    }
}