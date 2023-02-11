package fr.jhelp.kotlinToSwift.complete

import fr.jhelp.kotlinToSwift.test.tools.assertTransformed
import org.junit.jupiter.api.Test

class AbstractTests
{
    @Test
    fun abstractTest()
    {
        val kotlwiftSource =
            """
                abstract class Test
                {
                    val value = 73
                
                    abstract fun test()
                    
                    abstract fun compute(value : Double) : Double
                }
            """.trimIndent()

        val swiftExpected =
            """
                public open class Test
                {
                    let value = 73
                    
                    open func test()
                    {
                        fatalError("Abstract method, must override test")
                    }
                    
                    @discardableResult
                    open func compute(_ value : Double) -> Double
                    {
                        return fatal("Abstract method, must override compute")
                    }
                }
            """.trimIndent()

        assertTransformed(kotlwiftSource, swiftExpected)
    }
}