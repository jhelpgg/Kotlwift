package fr.jhelp.kotlinToSwift.postTreatment

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DetectUnusedVariableInForTests
{
    @Test
    fun detectUnusedVariableInForPure()
    {
        val source =
            """
                for i in 0 ..< limit  {
                    inputValues.append(0.0)
                }
            """.trimIndent()
        val expected =
            """
                for _ in 0 ..< limit  {
                    inputValues.append(0.0)
                }
            """.trimIndent()
        Assertions.assertEquals(expected,
                                detectUnusedVariableInFor(source))
    }

    @Test
    fun detectUnusedVariableInForMiddleSomething()
    {
        val source =
            """
                        var frame = Array<Double>()
                        var index = 0

                        for coordinate2d in outputCoordinates  {
                            let data = preprocessDatas[index]

                            if !data.isInTriangle  {
                                resultFind =
                                    SharedGeomHelper.checkCoordinateIsInTriangle(delaunay!.coordList, coordinate, 0.0)
                                index += 1
                            }

                            if resultFind.first  {
                                res.isInTriangle = resultFind.first
                                res.idx = delaunay!.indexList
                                res.triangle = delaunay!.coordList
                                res.lambdas = resultFind.second
                            }
                            preprocessDatas.append(res)
                        }
                    
                    doesSomething()
            """.trimIndent()
        val expected =
            """
                        var frame = Array<Double>()
                        var index = 0

                        for _ in outputCoordinates  {
                            let data = preprocessDatas[index]

                            if !data.isInTriangle  {
                                resultFind =
                                    SharedGeomHelper.checkCoordinateIsInTriangle(delaunay!.coordList, coordinate, 0.0)
                                index += 1
                            }

                            if resultFind.first  {
                                res.isInTriangle = resultFind.first
                                res.idx = delaunay!.indexList
                                res.triangle = delaunay!.coordList
                                res.lambdas = resultFind.second
                            }
                            preprocessDatas.append(res)
                        }
                    
                    doesSomething()
            """.trimIndent()
        Assertions.assertEquals(expected,
                                detectUnusedVariableInFor(source))
    }

    @Test
    fun detectUnusedVariableInForMiddleComplex()
    {
        val source =
            """
                for time in 0 ..< count {
                   for ignored in 0 ..< size {
                      print("something")
                   }

                   for index in 0 ..< size {
                      val t = index
                   }

                   for ignored in 0 ..< size {
                      print("something")
                   }
                }
            """.trimIndent()
        val expected =
            """
                for _ in 0 ..< count {
                   for _ in 0 ..< size {
                      print("something")
                   }

                   for index in 0 ..< size {
                      val t = index
                   }

                   for _ in 0 ..< size {
                      print("something")
                   }
                }
            """.trimIndent()
        Assertions.assertEquals(expected,
                                detectUnusedVariableInFor(source))
    }
}