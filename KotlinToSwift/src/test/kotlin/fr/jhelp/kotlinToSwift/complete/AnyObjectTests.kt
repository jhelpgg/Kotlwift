package fr.jhelp.kotlinToSwift.complete

import fr.jhelp.kotlinToSwift.test.tools.assertTransformed
import org.junit.jupiter.api.Test

class AnyObjectTests {
    @Test
    fun anyObject() {
        @Test
        fun abstractTest() {
            val kotlwiftSource =
                """
                interface Test : AnyObject
                {
                }
            """.trimIndent()

            val swiftExpected =
                """
                protocol Test : AnyObject
                {
                }
            """.trimIndent()

            assertTransformed(kotlwiftSource, swiftExpected)
        }
    }
}