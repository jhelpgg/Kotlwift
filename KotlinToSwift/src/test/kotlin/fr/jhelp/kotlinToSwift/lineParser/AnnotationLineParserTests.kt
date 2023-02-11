package fr.jhelp.kotlinToSwift.lineParser

import fr.jhelp.kotlinToSwift.test.tools.assertEqualsIgnoreWhiteSpaceNumber
import org.junit.jupiter.api.Test

class AnnotationLineParserTests
{
    @Test
    fun parseOverride()
    {
        val annotationLineParser = AnnotationLineParser()
        assertEqualsIgnoreWhiteSpaceNumber("override constructor()",
                                           annotationLineParser.parse("@Override constructor()"))
    }

    @Test
    fun parseTry()
    {
        val annotationLineParser = AnnotationLineParser()
        assertEqualsIgnoreWhiteSpaceNumber("try someThingMayFail()",
                                           annotationLineParser.parse("@Try someThingMayFail()"))

        assertEqualsIgnoreWhiteSpaceNumber("var x = try someThingMayFail()",
                                           annotationLineParser.parse("@Try var x = someThingMayFail()"))

        assertEqualsIgnoreWhiteSpaceNumber("x = try someThingMayFail()",
                                           annotationLineParser.parse("@Try x = someThingMayFail()"))
    }

    @Test
    fun parseWeak()
    {
        val annotationLineParser = AnnotationLineParser()

        assertEqualsIgnoreWhiteSpaceNumber("weak var listener : Listener? = null",
                                           annotationLineParser.parse("@Weak var listener : Listener? = null"))

        assertEqualsIgnoreWhiteSpaceNumber("private weak var listener : Listener? = null",
                                           annotationLineParser.parse("private @Weak var listener : Listener? = null"))

        assertEqualsIgnoreWhiteSpaceNumber("public weak var listener : Listener? = null",
                                           annotationLineParser.parse("public @Weak var listener : Listener? = null"))

        assertEqualsIgnoreWhiteSpaceNumber("internal weak var listener : Listener? = null",
                                           annotationLineParser.parse("internal @Weak var listener : Listener? = null"))

        assertEqualsIgnoreWhiteSpaceNumber("private weak var listener : Listener? = null",
                                           annotationLineParser.parse("@Weak private var listener : Listener? = null"))

        assertEqualsIgnoreWhiteSpaceNumber("public weak var listener : Listener? = null",
                                           annotationLineParser.parse("@Weak public var listener : Listener? = null"))

        assertEqualsIgnoreWhiteSpaceNumber("internal weak var listener : Listener? = null",
                                           annotationLineParser.parse("@Weak internal var listener : Listener? = null"))
    }
}