package fr.jhelp.compiler.command

import fr.jhelp.compiler.kotlwiftCompileVerbose
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicBoolean

object CommandManager
{
    private val waitingEnd = AtomicBoolean(true)
    private val lockEnd = Object()

    /**
     * Launch commands in the specified directory
     *
     * Log output depends on verbosity
     *
     * Possibility to add custom reaction to output lines
     */
    fun launchCommands(commands: Array<String>, directory: File, logVerbosity: LogVerbosity, additionalAction: (String) -> Unit = {})
    {
        CompilationErrorCollector.startCompilation()
        this.waitingEnd.set(true)
        val process = ProcessBuilder().directory(directory).command(CommandFactory.commandOS.shellCommand).start()
        LogCollector(process.errorStream, logVerbosity, additionalAction)
        LogCollector(process.inputStream, logVerbosity, additionalAction)
        val bufferedWriter = BufferedWriter(OutputStreamWriter(process.outputStream))
        bufferedWriter.write("cd ${directory.absolutePath}")

        if(kotlwiftCompileVerbose)
        {
            println("$> cd ${directory.absolutePath}")
        }

        bufferedWriter.newLine()

        for (command in commands)
        {
            bufferedWriter.write(command)

            if(kotlwiftCompileVerbose)
            {
                println("$> $command")
            }

            bufferedWriter.newLine()
        }

        // Process.waitFor() not work, so we do a trick to write a specific END message to know when all commands are completes
        bufferedWriter.write("echo \"$END_MESSAGE\"")

        if(kotlwiftCompileVerbose)
        {
            println("$> echo \"$END_MESSAGE\"")
        }

        bufferedWriter.newLine()

        bufferedWriter.flush()

        // Make current thread wait until end message
        while (this.waitingEnd.get())
        {
            synchronized(this.lockEnd)
            {
                this.lockEnd.wait()
            }
        }

        process.destroy()
    }

    /**
     * Called when end message read and free end message waiter
     */
    internal fun endReached()
    {
        if (this.waitingEnd.compareAndSet(true, false))
        {
            synchronized(this.lockEnd)
            {
                this.lockEnd.notify()
            }
        }
    }
}