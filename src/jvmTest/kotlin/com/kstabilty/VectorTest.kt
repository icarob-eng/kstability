package com.kstabilty

import com.kstabilty.Vector.Consts
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class VectorTest {
    private val sample = Vector(3, 4)
    private val sampleB = Vector(1, 2)
    private val sampleC = Vector(2, 0)

    /**
     * Expected values calculated by numpy
     */

    @Test
    fun modulus() {
        val expected = 5F
        assertEquals(expected, sample.length())
    }

    @Test
    fun normalize() {
        // [0.6, 0.8]
        val expected = Vector(0.6, 0.8)
        assertEquals(expected, sample.normalize())
    }

    @Test
    fun notEquals() {
        assertFalse(sample == sampleC)
    }

    @Test
    fun equals() {
        assertTrue(sample == Vector(3F, 4F))
    }

    @Test
    fun moduleEquals() {
        assertTrue(sample.equals(5F))
    }

    @Test
    fun times() {
        val expected = 11F
        assertEquals(expected, sample * sampleB)
    }

    @Test
    fun crossModule() {
        val expected = 2F
        assertEquals(expected, sample.crossModule(sampleB))
    }

    @Test
    fun getOrthogonal() {
        assertEquals(Consts.VERTICAL, sampleC.orthogonal())
    }

    @Test
    fun getRotated() {
        assertAll("getRotated",
            { assertEquals(sampleC.orthogonal() * 2, sampleC.getRotated(Float.POSITIVE_INFINITY))},
            { assertEquals(Vector(1, 3).normalize()*2, sampleC.getRotated(3F/1)) },
            { assertEquals(Vector(7, 5).normalize()*2, sampleC.getRotated(5F/7)) }
        )
    }

    @Test
    fun inclination() {
        assertAll("inclination",
            { assertEquals(4F/3, sample.inclination()) },
            { assertEquals(2F/1, sampleB.inclination()) },
            { assertEquals(0F, sampleC.inclination()) },
            { assertEquals(Float.POSITIVE_INFINITY, Consts.VERTICAL.inclination()) },
            { assertEquals(Float.NEGATIVE_INFINITY, -Consts.VERTICAL.inclination()) },
        )
    }
}