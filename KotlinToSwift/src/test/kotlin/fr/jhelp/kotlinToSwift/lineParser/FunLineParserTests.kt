package fr.jhelp.kotlinToSwift.lineParser

import fr.jhelp.kotlinToSwift.test.tools.assertEqualsIgnoreWhiteSpaceNumber
import org.junit.jupiter.api.Test

class FunLineParserTests
{
    @Test
    fun parseFunctionDeclaration()
    {
        val funLineParser = FunLineParser()
        assertEqualsIgnoreWhiteSpaceNumber("public func test()",
                                           funLineParser.parse("public fun test()"))
        assertEqualsIgnoreWhiteSpaceNumber("@discardableResult\n    public func toString() -> String",
                                           funLineParser.parse("override public fun toString() : String"))
        assertEqualsIgnoreWhiteSpaceNumber("@discardableResult\n    override public func test(_ age:Int, _ name:String) -> Boolean",
                                           funLineParser.parse("override public fun test(age:Int, name:String) : Boolean"))
        assertEqualsIgnoreWhiteSpaceNumber("private func mayFail() throws ",
                                           funLineParser.parse("@Throws private fun mayFail()"))
        assertEqualsIgnoreWhiteSpaceNumber("open public func test(_ task : ()->Int)",
                                           funLineParser.parse("open public fun test(task : ()->Int)"))
        assertEqualsIgnoreWhiteSpaceNumber("open public func test(_ task : @escaping  ()->Int)",
                                           funLineParser.parse("open public fun test(@Escaping task : ()->Int)"))
        assertEqualsIgnoreWhiteSpaceNumber("@discardableResult\n    public func next() -> Element<T>?",
                                           funLineParser.parse("fun next(): Element<T>?"))
    }

    @Test
    fun parseAbstractFunctionDeclaration()
    {
        val funLineParser = FunLineParser()
        assertEqualsIgnoreWhiteSpaceNumber("public open func test() {\n fatalError(\"Abstract method, must override test\")\n }",
                                           funLineParser.parse("public abstract fun test()"))
        assertEqualsIgnoreWhiteSpaceNumber("@discardableResult\n    public open func toString() -> String {\n return fatal(\"Abstract method, must override toString\")\n }",
                                           funLineParser.parse("override public abstract fun toString() : String"))
        assertEqualsIgnoreWhiteSpaceNumber("@discardableResult\n    override public open func test(_ age:Int, _ name:String) -> Boolean {\n return fatal(\"Abstract method, must override test\")\n }",
                                           funLineParser.parse("override public abstract fun test(age:Int, name:String) : Boolean"))
        assertEqualsIgnoreWhiteSpaceNumber("private open func mayFail() throws {\n fatalError(\"Abstract method, must override mayFail\")\n }",
                                           funLineParser.parse("@Throws private abstract fun mayFail()"))
        assertEqualsIgnoreWhiteSpaceNumber("@discardableResult\n    open func next() -> Element<T>? {\n return fatal(\"Abstract method, must override next\")\n }",
                                           funLineParser.parse("abstract fun next(): Element<T>?"))
    }
}