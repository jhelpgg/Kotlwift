package fr.jhelp.kotlinToSwift

import fr.jhelp.kotlinToSwift.lineParser.FORCE_LINE_CONTINUE
import fr.jhelp.kotlinToSwift.lineParser.INTERNAL_SET
import fr.jhelp.kotlinToSwift.lineParser.PRIVATE_SET
import fr.jhelp.kotlinToSwift.lineParser.parseLine
import fr.jhelp.kotlinToSwift.postTreatment.postTreatments
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.Stack

const val KotlinExtension = "kt"
const val SwiftExtension = ".swift"

object KotlinFileFilter : FileFilter
{
    override fun accept(pathname: File) =
        pathname.isDirectory || KotlinExtension.equals(pathname.extension, true)
}

enum class ParseStatus
{
    MULTI_LINE_COMMENT,
    PARSED,
    LINE_CONTINUE
}

fun swiftTransformer(directorySource: File, directoryDestination: File)
{
    if (KotlinToSwiftOptions.verbose)
    {
        println("swiftTransformer : ${directorySource.absolutePath} -> ${directoryDestination.absolutePath}")
    }

    if (!directorySource.exists() || !directorySource.isDirectory || !directorySource.canRead())
    {
        throw IllegalArgumentException(
            "Source ${directorySource.absolutePath} not exits or not a directory or can't read")
    }

    if (!directoryDestination.exists())
    {
        if (!directoryDestination.mkdirs())
        {
            throw IOException("Can't create ${directoryDestination.absolutePath}")
        }
    }

    if (!directoryDestination.isDirectory || !directoryDestination.canWrite())
    {
        throw IllegalArgumentException(
            "Destination ${directoryDestination.absolutePath} not a directory or can't write on it")
    }

    val stack = Stack<Pair<File, File>>()
    stack.push(Pair(directorySource, directoryDestination))
    var swiftFile: File
    val listSwiftFiles = ArrayList<File>()

    while (stack.isNotEmpty())
    {
        val (source, destination) = stack.pop()

        if (source.isDirectory)
        {
            if (!destination.exists())
            {
                destination.mkdirs()
            }

            source.listFiles(KotlinFileFilter)?.forEach { stack.push(Pair(it, File(destination, it.name))) }
        }
        else
        {
            swiftFile = File(destination.parent, destination.nameWithoutExtension + SwiftExtension)
            internalSwiftTransformer(source, swiftFile)
            listSwiftFiles += swiftFile
        }
    }

    postTreatments(listSwiftFiles)
}

fun internalSwiftTransformer(source: File, destination: File)
{
    if (KotlinToSwiftOptions.verbose)
    {
        println("internalSwiftTransformer : ${source.absolutePath} -> ${destination.absolutePath}")
    }

    if (!destination.exists())
    {
        if (!destination.createNewFile())
        {
            throw IOException("Can't create file : ${destination.absolutePath}")
        }
    }

    var kotlinReader: BufferedReader? = null
    var swiftWriter: BufferedWriter? = null
    var previous = ""

    try
    {
        kotlinReader = BufferedReader(InputStreamReader(FileInputStream(source)))
        swiftWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(destination)))
        println("${source.absolutePath} -> ${destination.absolutePath}")
        var line = kotlinReader.readLine()
        var lineAfter: String?
        var lineAfterTrimmed: String?
        var inMultilineComment = false

        while (line != null)
        {
            line = previous + line
            previous = ""
            lineAfter = kotlinReader.readLine()
            lineAfterTrimmed = lineAfter?.trim()

            if (PRIVATE_SET == lineAfterTrimmed || INTERNAL_SET == lineAfterTrimmed)
            {
                line += " $lineAfter"
                lineAfter = kotlinReader.readLine()
            }

            when (parserLine(line, swiftWriter, inMultilineComment))
            {
                ParseStatus.MULTI_LINE_COMMENT -> inMultilineComment = true
                ParseStatus.PARSED             -> inMultilineComment = false
                ParseStatus.LINE_CONTINUE      -> previous = line + "\n"
            }

            line = lineAfter
        }

        swiftWriter.flush()
    }
    catch (exception: Exception)
    {
        throw IOException("Failed to transform ${source.absolutePath} to ${destination.absolutePath}", exception)
    }
    finally
    {
        if (kotlinReader != null)
        {
            try
            {
                kotlinReader.close()
            }
            catch (ignored: Exception)
            {
            }
        }

        if (swiftWriter != null)
        {
            try
            {
                swiftWriter.close()
            }
            catch (ignored: Exception)
            {
            }
        }
    }
}

private fun parserLine(line: String, swiftWriter: BufferedWriter, inMultilineComment: Boolean): ParseStatus
{
    val indexStart = line.indexOfFirst { it > ' ' }
    val trimLine = line.trim()

    //Empty lines
    if (trimLine.isEmpty())
    {
        swiftWriter.newLine()
        return if (inMultilineComment) ParseStatus.MULTI_LINE_COMMENT else ParseStatus.PARSED
    }

    //Comments
    if (inMultilineComment)
    {
        swiftWriter.write(line)
        swiftWriter.newLine()
        return if (!trimLine.endsWith("*/")) ParseStatus.MULTI_LINE_COMMENT else ParseStatus.PARSED
    }

    if (trimLine.startsWith("/*"))
    {
        swiftWriter.write(line)
        swiftWriter.newLine()
        return if (!trimLine.endsWith("*/")) ParseStatus.MULTI_LINE_COMMENT else ParseStatus.PARSED
    }

    if (trimLine.startsWith("//"))
    {
        swiftWriter.write(line)
        swiftWriter.newLine()
        return ParseStatus.PARSED
    }

    //Ignore package and import declarations
    if (trimLine.startsWith("package") || trimLine.startsWith("import"))
    {
        return ParseStatus.PARSED
    }

    val keyWordsReplaced = keyWordReplacement(trimLine)
    val stringInterpreted = stringInterpret(keyWordsReplaced)

    val transformed = parseLine(stringInterpreted)

    if (transformed == FORCE_LINE_CONTINUE || countParenthesis(stringInterpreted) != 0)
    {
        return ParseStatus.LINE_CONTINUE
    }

    if (transformed.isNotEmpty())
    {
        if (indexStart > 0)
        {
            swiftWriter.write(line.substring(0, indexStart))
        }

        swiftWriter.write(transformed)
        swiftWriter.newLine()
        return ParseStatus.PARSED
    }

    if (stringInterpreted.contains("@Test"))
    {
        swiftWriter.write(stringInterpreted)
        swiftWriter.newLine()
        return ParseStatus.PARSED
    }

    // Detect if line is not finished
    if (stringInterpreted.startsWith('@')
        || stringInterpreted.endsWith('=')
        || countParenthesis(stringInterpreted) != 0
        || stringInterpreted.contains("constructor"))
    {
        return ParseStatus.LINE_CONTINUE
    }

    // Final transformations
    if (indexStart > 0)
    {
        swiftWriter.write(line.substring(0, indexStart))
    }

    val interpretEquals = invokeEqualInterpreter(stringInterpreted)
    swiftWriter.write(interpretEquals)
    swiftWriter.newLine()
    return ParseStatus.PARSED
}


fun countParenthesis(string: String): Int
{
    var count = 0
    var insideString = false
    var insideQuote = false
    var escaped = false

    for (character in string.toCharArray())
    {
        when (character)
        {
            '\\' -> escaped = !escaped
            '"'  ->
                when
                {
                    escaped      -> escaped = false
                    !insideQuote -> insideString = !insideString
                }
            '\'' ->
                when
                {
                    escaped       -> escaped = false
                    !insideString -> insideQuote = !insideQuote
                }
            '('  ->
                when
                {
                    escaped                       -> escaped = false
                    !insideString && !insideQuote -> count++
                }
            ')'  ->
                when
                {
                    escaped                       -> escaped = false
                    !insideString && !insideQuote -> count--
                }
        }
    }

    return count
}