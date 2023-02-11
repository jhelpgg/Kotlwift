package fr.jhelp.kotlinToSwift.postTreatment

import fr.jhelp.kotlinToSwift.KotlinToSwiftOptions
import fr.jhelp.kotlinToSwift.protocol.parseProtocolsInFiles
import java.io.File
import java.util.regex.Pattern

private val INTERNAL_OPEN_PATTERN = Pattern.compile("internal\\s+open\\s+")
private const val INTERNAL_OPEN_REPLACEMENT = "open "

fun postTreatments(files: List<File>)
{
    var transformed: String

    for (file in files)
    {
        try
        {
            if (KotlinToSwiftOptions.verbose)
            {
                println("postTreatments : ${file.absolutePath}")
            }

            transformed = parseCompanionInFile(file.readText())

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseCompanionInFile")
            }

            transformed = parseWhenInFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseWhenInFile")
            }

            transformed = parseConstructorInFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseConstructorInFile")
            }

            transformed = parseEnumFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseEnumFile")
            }

            transformed = parseExceptionFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseExceptionFile")
            }

            transformed = parseTestFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseTestFile")
            }

            transformed = parseEqualsInFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseEqualsInFile")
            }

            transformed = parseComparableInFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseComparableInFile")
            }

            transformed = parseToStringInFile(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseToStringInFile")
            }

            transformed = parseExtension(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : parseExtension")
            }

            transformed = detectUnusedVariableInFor(transformed)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : detectUnusedVariableInFor")
            }

            transformed = INTERNAL_OPEN_PATTERN.matcher(transformed).replaceAll(INTERNAL_OPEN_REPLACEMENT)

            if (KotlinToSwiftOptions.verbose)
            {
                println(" => postTreatments : INTERNAL_OPEN_PATTERN")
            }

            file.writeText(transformed)
        }
        catch (exception: Exception)
        {
            throw Exception("Failed to port treatment of : ${file.absolutePath}", exception)
        }
    }

    if (KotlinToSwiftOptions.verbose)
    {
        println("postTreatments : GLOBAL")
    }

    parseProtocolsInFiles(files)

    if (KotlinToSwiftOptions.verbose)
    {
        println("postTreatments : parseProtocolsInFiles")
    }

    removeOpen(files)

    if (KotlinToSwiftOptions.verbose)
    {
        println("postTreatments : removeOpen")
    }

    addHeaderClass(files)

    if (KotlinToSwiftOptions.verbose)
    {
        println("postTreatments : addHeaderClass")
    }
}