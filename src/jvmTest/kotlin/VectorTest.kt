import com.kstabilty.Consts
import com.kstabilty.Vector
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
        assertEquals(expected, sample.modulus())
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
        assertEquals(Consts.VERTICAL, sampleC.getOrthogonal())
    }
}