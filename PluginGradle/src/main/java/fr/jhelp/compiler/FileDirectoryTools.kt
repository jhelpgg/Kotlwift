package fr.jhelp.compiler

import fr.jhelp.compiler.command.CommandFactory
import fr.jhelp.compiler.command.CommandManager
import fr.jhelp.compiler.command.CompilationErrorCollector
import fr.jhelp.compiler.command.LogVerbosity
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

/**
 * Create a directory if it not exists.
 *
 * It also creates necessary parents, if they not exists
 *
 * This method failed if path already exists, but not a directory
 *
 * This method failed also if not allowed to create the directory or one of its parent
 */
fun createDirectoryAndHierarchyIfNeed(directory: File) {
    if (directory.exists()) {
        if (!directory.isDirectory) {
            System.err.println("Not a directory : ${directory.absolutePath}")
            exitProcess(-1)
        }

        return
    }

    if (!directory.mkdirs()) {
        System.err.println("Can't create directory : ${directory.absolutePath}")
        exitProcess(-1)
    }
}

/**
 * Create a file if it not exists.
 *
 * It also creates necessary parents directory, if they not exists
 *
 * This method failed if path already exists, but not a file
 *
 * This method failed also if not allowed to create the file or one of its parent
 */
fun createFileAndHierarchyIfNeed(file: File) {
    if (file.exists()) {
        if (!file.isFile) {
            System.err.println("Not a file : ${file.absolutePath}")
            exitProcess(-1)
        }

        return
    }

    createDirectoryAndHierarchyIfNeed(file.parentFile)

    if (!file.createNewFile()) {
        System.err.println("Can't create file : ${file.absolutePath}")
        exitProcess(-1)
    }
}

/**
 * Unzip transpiler to able to use it
 * @param kotlinToSwiftZip Zip location to unzip
 * @param kotlinToSwiftDirectory Destination directory
 */
fun unzipKotlinToSwiftTranspiler(kotlinToSwiftZip: URL, kotlinToSwiftDirectory: File) {
    val zipInputStream = ZipInputStream(kotlinToSwiftZip.openStream())
    var zipEntry = zipInputStream.nextEntry
    var fileWhereCopy: File
    val buffer = ByteArray(4096)
    var read: Int

    while (zipEntry != null) {
        var name = zipEntry.name
        val indexSlash = name.indexOf('/')

        if (indexSlash >= 0) {
            name = "KotlinToSwift" + name.substring(indexSlash)
        } else {
            name = "KotlinToSwift"
        }

        println("${zipEntry.name} -> $name")

        if (zipEntry.isDirectory) {
            createDirectoryAndHierarchyIfNeed(File(kotlinToSwiftDirectory, name))
        } else {
            fileWhereCopy = File(kotlinToSwiftDirectory, name)
            createFileAndHierarchyIfNeed(fileWhereCopy)
            val outputStream = FileOutputStream(fileWhereCopy)
            read = zipInputStream.read(buffer)

            while (read >= 0) {
                outputStream.write(buffer, 0, read)
                read = zipInputStream.read(buffer)
            }

            outputStream.flush()
            outputStream.close()
        }

        zipInputStream.closeEntry()
        zipEntry = zipInputStream.nextEntry
    }

    zipInputStream.close()
}

/**
 * Make a file executable
 */
fun makeExecutable(fileKotlinToSwiftCommand: File, projectDirectory: File) {
    val makeExecutable = CommandFactory.commandOS.makeExecutable(fileKotlinToSwiftCommand.absolutePath)

    if (makeExecutable.isNotEmpty()) {
        CommandManager.launchCommands(arrayOf(makeExecutable), projectDirectory, LogVerbosity.NONE)

        if (CompilationErrorCollector.errorCount > 0) {
            System.err.println("${CompilationErrorCollector.errorCount} errors in make executable")
            exitProcess(-1)
        }
    }
}


