package fr.jhelp.compiler

import fr.jhelp.compiler.command.CommandFactory
import fr.jhelp.compiler.command.CommandManager
import fr.jhelp.compiler.command.CompilationErrorCollector
import fr.jhelp.compiler.command.LogVerbosity
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import kotlin.system.exitProcess

val kotlwiftDependencies = ArrayList<KotlwiftImplementation>()
const val SWIFT_RELATIVE_PATH = "src/main/swift"
const val SWIFT_COMMAND_FILE = "swiftCommand"
const val KOTLIN_SOURCE_RELATIVE_PATH = "src/main/kotlin"
const val SWIFT_SOURCE_RELATIVE_PATH = "$SWIFT_RELATIVE_PATH/Sources/swift/gen"
const val KOTLIN_TESTS_RELATIVE_PATH = "src/test/kotlin"
const val SWIFT_TESTS_RELATIVE_PATH = "$SWIFT_RELATIVE_PATH/Tests/swiftTests/gen"
const val KOTLIN_TO_SWIFT_RELATIVE_PATH = "KotlinToSwift/bin/KotlinToSwift"
const val REMOTES_ORIGIN_BRANCH = "remotes/origin/"
const val REMOTES_ORIGIN_BRANCH_LENGTH = REMOTES_ORIGIN_BRANCH.length
const val FETCH_DONE = "-<Fetch done>-"

var kotlwiftCompileVerbose = false

class CompilePlugin : Plugin<Project> {
    @Override
    override fun apply(target: Project) {
        // Check if swift command tool is defined, if not it is useless to continue
        val swiftCommandFile = File(target.rootDir, SWIFT_COMMAND_FILE)

        if (!swiftCommandFile.exists()) {
            println(" /!\\ WARNING /!\\ ")
            println("No swift tool defined, the plugin can't work without it !")
            println("Specify it in $SWIFT_COMMAND_FILE")
            println(" /!\\ WARNING /!\\ ")
            return
        }

        // Get swift command tool
        val swiftCommandText = swiftCommandFile.readText().trim()
        val swiftCommandCreatedFile = File(target.buildDir, "temp/swiftCommand")
        createFileAndHierarchyIfNeed(swiftCommandCreatedFile)
        swiftCommandCreatedFile.writeText(swiftCommandText)
        makeExecutable(swiftCommandCreatedFile, target.projectDir)
        val swiftCommand = swiftCommandCreatedFile.absolutePath
        val swiftDirectory = File(target.projectDir, SWIFT_RELATIVE_PATH)
        initializeSwiftWorkingDirectoryIfNeed(swiftCommand, swiftDirectory)

        // Register action for transpile Kotlwift to Swift
        target.tasks.register("transpileKotlwiftToSwift").configure {
            // Clone dependencies
            val directoryCloned = File(target.buildDir, "cloned")
            val clonedSourceDirectories = ArrayList<File>()
            cloneUpdateKotlwiftDependencies(directoryCloned, clonedSourceDirectories)

            // Get transpiler tool
            val kotlinToSwiftZip = CompilePlugin::class.java.getResource("KotlinToSwift.zip")
            val kotlinToSwiftDirectory = File(target.buildDir, "kotlinToSwiftDirectory")
            createDirectoryAndHierarchyIfNeed(kotlinToSwiftDirectory)
            unzipKotlinToSwiftTranspiler(kotlinToSwiftZip!!, kotlinToSwiftDirectory)

            // Make the kotlin light transpiler executable
            val fileKotlinToSwiftCommand = File(kotlinToSwiftDirectory, KOTLIN_TO_SWIFT_RELATIVE_PATH)
            makeExecutable(fileKotlinToSwiftCommand, target.projectDir)

            // Transpile swift sources and tests
            transpileSwiftSources(fileKotlinToSwiftCommand, clonedSourceDirectories, target.projectDir)
            transpileSwiftTests(fileKotlinToSwiftCommand, clonedSourceDirectories, target.projectDir)
        }

        // Register action for validate transpiled swift and launch unit tests in Swift
        target.tasks.register("swiftValidation").configure {
            // Check if Swift compile
            CommandManager.launchCommands(arrayOf("$swiftCommand run ${CommandFactory.commandOS.redirectionFooter}"), swiftDirectory, LogVerbosity.NONE)

            if (CompilationErrorCollector.errorCount > 0) {
                System.err.println("${CompilationErrorCollector.errorCount} errors in swift compilation")
                exitProcess(-1)
            }

            val reportSwiftFile = File(target.buildDir, "reports/reportSwift.txt")
            createFileAndHierarchyIfNeed(reportSwiftFile)
            val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(reportSwiftFile)))
            // Check if swift tests pass
            CommandManager.launchCommands(arrayOf("$swiftCommand test --enable-test-discovery ${CommandFactory.commandOS.redirectionFooter}"), swiftDirectory, LogVerbosity.TEST)
            { line ->
                writer.write(line)
                writer.newLine()
            }

            writer.flush()
            writer.close()

            if (CompilationErrorCollector.errorCount > 0) {
                System.err.println("${CompilationErrorCollector.errorCount} errors in swift test")
                exitProcess(-1)
            }
        }
    }
}
