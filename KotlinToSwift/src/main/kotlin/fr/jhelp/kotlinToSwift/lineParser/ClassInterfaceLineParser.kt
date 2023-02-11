package fr.jhelp.kotlinToSwift.lineParser

import fr.jhelp.kotlinToSwift.KotlinToSwiftOptions
import java.util.regex.Pattern

/**
 * Pattern for match class/interface declaration.
 *
 * Class line looks like:
 *
 *       [<public|internal>] [<abstract|open>] class <name_generic> [: <implements_extends>] [{]
 *       [<public|internal>] interface <name_generic> [: <implements_extends>] [{]
 *
 *       +--------------------------------------------+-----+-----------------------------+
 *       |                  Group                     | ID  |           Capture           |
 *       +--------------------------------------------+-----+-----------------------------+
 *       | (?:public|internal)\s+                     |  1  | public/internal visibility  |
 *       | abstract\s+                                |  2  | abstract specification      |
 *       | (?:\s+open\s+)?(?:class|interface)         |  3  | class/interface description |
 *       | \s+[a-zA-Z][a-zA-Z0-9_<, >]*               |  4  | class/interface name        |
 *       | \s*:[^{]+                                  |  5  | extends/implements          |
 *       | \s*\{                                      |  6  | Curly ending                |
 *       +--------------------------------------------+-----+-----------------------------+
 */
private val CLASS_INTERFACE_PATTERN =
    Pattern.compile("((?:public|internal)\\s+)?(abstract\\s+)?((?:\\s+open\\s+)?(?:class|interface))(\\s+[a-zA-Z][a-zA-Z0-9_<, >]*)(\\s*:[^{]+)?(\\s*\\{)?")
private const val GROUP_CLASS_PUBLIC_INTERNAL = 1
private const val GROUP_ABSTRACT = 2
private const val GROUP_CLASS_INTERFACE_DESCRIPTION = 3
private const val GROUP_CLASS_INTERFACE_NAME = 4
private const val GROUP_CLASS_INTERFACE_EXTENDS = 5
private const val GROUP_CLASS_INTERFACE_CURLY = 6

class ClassInterfaceLineParser : LineParser
{
    override fun parse(trimLine: String): String
    {
        val matcher = CLASS_INTERFACE_PATTERN.matcher(trimLine)

        if (matcher.matches())
        {
            val parsed = StringBuilder()

            matcher.group(GROUP_CLASS_PUBLIC_INTERNAL)?.let { header -> parsed.append(header) }
            ?: if (KotlinToSwiftOptions.automaticPublic)
            {
                parsed.append("public ")
            }
            matcher.group(GROUP_ABSTRACT)?.let { parsed.append(it.replace("abstract", "open")) }
            parsed.append(matcher.group(GROUP_CLASS_INTERFACE_DESCRIPTION).replace("interface", "protocol"))
            parsed.append(matcher.group(GROUP_CLASS_INTERFACE_NAME))
            matcher.group(GROUP_CLASS_INTERFACE_EXTENDS)?.let { parsed.append(it) }
            matcher.group(GROUP_CLASS_INTERFACE_CURLY)?.let { parsed.append(it) }
            return parsed.toString()
        }

        return ""
    }
}