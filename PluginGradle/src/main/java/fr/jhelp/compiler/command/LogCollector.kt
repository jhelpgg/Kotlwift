package fr.jhelp.compiler.command

import fr.jhelp.compiler.kotlinLightCompileVerbose
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Collect commands output and react to them.
 *
 * Print line on screen depends on verbosity level
 *
 * Can add custom reaction to output lines
 */
class LogCollector(inputStream: InputStream,private  val logVerbosity: LogVerbosity, private val additionalReaction:(String)->Unit)
{
    private val bufferedReader = BufferedReader(InputStreamReader(inputStream))

    init
    {
        Thread(this::collectLog).start()
    }

    private fun collectLog()
    {
        var line = this.bufferedReader.readLine()
        var testStarted = false

        while (line != null)
        {
            this.additionalReaction(line)
            testStarted = testStarted || line.startsWith(TEST_START)

            val printLine =
                when (logVerbosity)
                {
                    LogVerbosity.FULL -> true
                    LogVerbosity.TEST -> testStarted
                    LogVerbosity.NONE -> false
                }

            if (printLine || kotlinLightCompileVerbose)
            {
                println(line)
            }

            if (line == END_MESSAGE)
            {
                CommandManager.endReached()
            }
            else
            {
                CompilationErrorCollector.parseLine(line)
            }

            line = this.bufferedReader.readLine()
        }

        this.bufferedReader.close()
    }
}