package fr.jhelp.kotlinToSwift.postTreatment

import java.util.regex.Pattern

private val forDetectionPattern = Pattern.compile("(\\s*for\\s+)([a-zA-Z0-9_]+)(\\s+in)")
private const val FOR_GROUP = 1
private const val PARAMETER_NAME_GROUP = 2
private const val IN_GROUP = 3

fun detectUnusedVariableInFor(file: String): String
{
    val matcher = forDetectionPattern.matcher(file)
    val transformed = StringBuilder()
    var start = 0
    val fileCharacters = file.toCharArray()

    while (matcher.find())
    {
        val name = matcher.group(PARAMETER_NAME_GROUP)

        if (doesNameNotUsed(name, fileCharacters, matcher.end()))
        {
            transformed.append(file.substring(start, matcher.start()))
            transformed.append(matcher.group(FOR_GROUP))
            transformed.append('_')
            transformed.append(matcher.group(IN_GROUP))
            start = matcher.end()
        }
    }

    transformed.append(file.substring(start))
    return transformed.toString()
}

private fun doesNameNotUsed(name: String, fileCharacters: CharArray, start: Int): Boolean
{
    var currentWord = StringBuilder()
    var curlyCount = 0

    for (index in start until fileCharacters.size)
    {
        val character = fileCharacters[index]

        when
        {
            character == '{'                              ->
            {
                currentWord = StringBuilder()
                curlyCount++
            }
            character == '}'                              ->
            {
                currentWord = StringBuilder()
                curlyCount--

                if (curlyCount <= 0)
                {
                    return true
                }
            }
            isWorldCharacter(character) && curlyCount > 0 -> currentWord.append(character)
            else                                          ->
            {
                if (currentWord.toString() == name)
                {
                    return false
                }

                currentWord = StringBuilder()
            }
        }
    }

    return true
}

private fun isWorldCharacter(character: Char): Boolean =
    character == '_' || (character in '0'..'9') || (character in 'a'..'z') || (character in 'A'..'Z')
