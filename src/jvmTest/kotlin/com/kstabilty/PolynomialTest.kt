package com.kstabilty

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.sqrt

class PolynomialTest {
    private val sample1 = Polynomial(1F,1F,1F)
    private val sample2 = Polynomial(1F,0F,0F)
    private val sample3 = Polynomial(1F,0F,-1F)
    private val sample4 = Polynomial(0F,1F,0F)

    @Test
    fun testIfRootAndVertexIsNull() {
        assertAll ("testIfRootAndVertexIsNull",
            { assertNull(sample4.vertex) },
            { assertNull(sample4.roots) }
        )
    }

    @Test
    fun rootsTest() {
        assertAll ("rootsTest",
            { assertEquals(-1F/2 + sqrt(-3F/4), sample1.roots?.first)},
            { assertEquals(-1F/2 + sqrt(-3F/4), sample1.roots?.second)},
            { assertEquals(0F, sample2.roots?.first)},
            { assertEquals(-0F, sample2.roots?.second)},
            { assertEquals(1F, sample3.roots?.first)},
            { assertEquals(-1F, sample3.roots?.second)}
            )
    }

    @Test
    fun vertexTest() {
        assertAll ( "vertexTest",
        { assertEquals(-0.5F, sample1.vertex?.x) },
        { assertEquals(-0F, sample2.vertex?.x) },
        { assertEquals(-0F, sample3.vertex?.x) }
        )
    }

    @Test
    operator fun invoke() {
        assertAll ("invokeTest",
            { assertEquals(3F, sample1(1)) },
            { assertEquals(7F, sample1(2)) },
            { assertEquals(13F, sample1(3)) },
            { assertEquals(1F, sample2(1)) },
            { assertEquals(4F, sample2(2)) },
            { assertEquals(9F, sample2(3)) },
            { assertEquals(0F, sample3(1)) },
            { assertEquals(3F, sample3(2)) },
            { assertEquals(8F, sample3(3)) },
            { assertEquals(1F, sample4(1)) },
            { assertEquals(2F, sample4(2)) },
            { assertEquals(3F, sample4(3)) }
            )
    }

    @Test
    fun testToString() {
        assertAll( "toStringTest",
            { assertEquals("+1.0x²+1.0x+1.0", sample1.toString()) },
            { assertEquals("+1.0x²", sample2.toString()) },
            { assertEquals("+1.0x²-1.0", sample3.toString()) },
            { assertEquals("+1.0x", sample4.toString()) }
            )
    }
}