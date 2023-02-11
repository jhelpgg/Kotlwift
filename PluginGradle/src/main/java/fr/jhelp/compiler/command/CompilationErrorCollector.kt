package fr.jhelp.compiler.command

import fr.jhelp.compiler.SWIFT_RELATIVE_PATH

private const val ERROR_LINE = "error:"
internal const val TEST_START = "Test "
private const val TEST_FAILED = "failed"

/**
 * Analyse command line output, detect errors and signal them
 */
object CompilationErrorCollector
{
    var errorCount: Int = 0
        private set
    private var insideError: Boolean = false

    /**
     * Reinitialize the errors counter
     */
    fun startCompilation()
    {
        this.errorCount = 0
        this.insideError = false
    }

    /**
     * Analyse output line
     */
    fun parseLine(line: String)
    {
        if (line.contains(SWIFT_RELATIVE_PATH) || line.startsWith(TEST_START))
        {
            this.insideError = line.contains(ERROR_LINE) || line.contains(TEST_FAILED)

            if (this.insideError)
            {
                this.errorCount++
            }
        }

        if (this.insideError)
        {
            System.err.println(line)
        }
    }
}