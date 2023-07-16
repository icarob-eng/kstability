package com.moon.kstability

import com.moon.kstability.Vector.Consts
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StabilizationTest {
    private val nodeSampleA = Node("A", Vector(0F, 102F))
    private val nodeSampleB = Node("B", Vector(1F, 102F))
        .also{ PointLoad(it, Vector(0f, -20f)) } // applies load sample
    private val nodeSampleC = Node("C", Vector(3F, 102F))
        .also { PointLoad(it, Vector(0f, -10f)) }
    private val nodeSampleD = Node("D", Vector(4F, 102F))
        .also { Beam(nodeSampleA, it) }

    private val supportSampleA = Support(nodeSampleA, Support.Gender.SECOND, Consts.VERTICAL)
    private val supportSampleB = Support(nodeSampleD, Support.Gender.FIRST, Consts.VERTICAL)

    private val structureSample = Structure("My structure", mutableListOf(nodeSampleA, nodeSampleB, nodeSampleC, nodeSampleD))

    /**
     * Structure Sample expectations:
     * - Resultant force from loads: Vector(0, -30)
     * - Resultant bending moment from loads: - 20 * 1 - 10 * 3 = -50,
     * therefore, the positive rotation direction is counter-clockwise
     * - Reaction forces: A + D = -30, D * 4 = -50 => D = -12.5, A = -17.5
     *
     * Also, the resultant force after stabilization should be 0.
      */

    private val expectedResultForce = Vector(0f, -30f)
    private val expectedBendingMoment = -20 * 1 - 10 * 3F
    private val expectedReactionA = Vector(0.0f, 17.5f)
    private val expectedReactionD = Vector(0.0f, 12.5f)

    private val sampleB = Structure("Basic Sample B", mutableListOf(
        Node("A", Vector(0,3)).apply {
            Support(this, Support.Gender.FIRST, Consts.VERTICAL)
        },
        Node("B", Vector(2, 3)).apply {
            PointLoad(this, Vector(0,-10))
        },
        Node("C", Vector(4,3)).apply {
            Support(this, Support.Gender.SECOND, Consts.VERTICAL)
        }
    )).also {
        Beam(it.nodes.first(), it.nodes.last())
    }


    @Test
    fun isostaticTest() {
        assertTrue(Stabilization.isIsostatic(structureSample))
    }
    // todo: check isostaticity in more complex structures

    @Test
    fun getResultMomentumTest() {
        assertEquals(expectedBendingMoment, Stabilization.getResultMomentum(structureSample))
    }

    @Test
    fun getResultForceTest() {
        assertEquals(expectedResultForce, Stabilization.getResultForce(structureSample))
    }

    @Test
    fun getReactionsABTest() {
        /**
         * Checks if the reaction pair is expected
         */
        val expected = Pair(
            expectedReactionA,
            expectedReactionD
        )

        val data = Stabilization.getReactionsAB(
            supportSampleA,
            supportSampleB,
            Stabilization.getResultForce(structureSample),
            Stabilization.getResultMomentum(structureSample)
        )

        assertEquals(expected, data)
    }

    @Test
    fun checkStabilizedForcesTest() {
        /**
         * Checks if the expected forces are put in the structure sample
         */

        val expected = Pair(
            expectedReactionA,
            expectedReactionD
        )

        Stabilization.stabilize(structureSample)
        val data = Pair(
            structureSample.getSupports()[0].node.pointLoads[0].vector,
            structureSample.getSupports()[1].node.pointLoads[0].vector
        )

        assertEquals(expected, data)
    }

    @Test
    fun resultForceManuallyStabilizedTest() {
        /**
         * Checks if the expected reaction forces in the structure makes the result force 0
         */

        val expected = Vector(0f, 0f)

        PointLoad(nodeSampleA, expectedReactionA)
        PointLoad(nodeSampleD, expectedReactionD)

        assertEquals(expected, Stabilization.getResultForce(structureSample))
    }

    @Test
    fun resultMomentumManuallyStabilizedTest() {
        /**
         * Checks if the expected reaction forces in the structure makes the result momentum 0
         */
        val expected = 0F

        PointLoad(nodeSampleA, expectedReactionA)
        PointLoad(nodeSampleD, expectedReactionD)

        assertEquals(expected, Stabilization.getResultMomentum(structureSample))
    }

    @Test
    fun resultForceStabilizedTest() {
        val expected = Vector(0f, 0f)

        Stabilization.stabilize(structureSample)

        assertEquals(expected, Stabilization.getResultForce(structureSample))
    }

    @Test
    fun resultMomentumStabilizedTest() {
        val expected = 0F

        Stabilization.stabilize(structureSample)

        assertEquals(expected, Stabilization.getResultMomentum(structureSample))
    }

    @Test
    fun isNotStableTest() {
        assertFalse(Stabilization.isStable(structureSample))
    }

    @Test
    fun isManuallyStableTest() {
        PointLoad(nodeSampleA, expectedReactionA)
        PointLoad(nodeSampleD, expectedReactionD)

        assertTrue(Stabilization.isStable(structureSample))
    }

    @Test
    fun isStabilizationStableTest() {
        Stabilization.stabilize(structureSample)
        assertTrue(Stabilization.isStable(structureSample))
    }

    @Test
    fun isStableSampleBTest() {
        Stabilization.stabilize(sampleB)
        assertTrue(Stabilization.isStable(sampleB))
    }

    @Test
    fun keepStabilityTest() {
        Stabilization.stabilize(structureSample)
        Stabilization.stabilize(structureSample)
        Stabilization.stabilize(structureSample)
        Stabilization.stabilize(structureSample)
        Stabilization.stabilize(structureSample)
        assertTrue(Stabilization.isStable(structureSample))
    }

    @Test
    fun isNotIsostaticTest() {
        val nodeSampleA1 = Node("A", Vector(0F, 2F))
        val nodeSampleB1 = Node("B", Vector(4F, 2F))

        PointLoad(nodeSampleA1, Vector(0f, -3f))
        Support(nodeSampleB1, Support.Gender.SECOND, Consts.HORIZONTAL)

        val structureB = Structure("Fixed support structure",
            mutableListOf(nodeSampleA1, nodeSampleB1)
        )

        assertThrows(IllegalArgumentException::class.java) { Stabilization.stabilize(structureB) }
    }

    @Test
    fun isStabilizationStableFixedSupportTest() {
        val nodeSampleA1 = Node("A", Vector(0F, 2F))
        val nodeSampleB1 = Node("B", Vector(4F, 2F))

        PointLoad(nodeSampleA1, Vector(0f, -3f))
        Support(nodeSampleB1, Support.Gender.THIRD, Consts.HORIZONTAL)

        val structureB = Structure("Fixed support structure",
            mutableListOf(nodeSampleA1, nodeSampleB1)
        )

        Stabilization.stabilize(structureB)

        val expectedResultForce = Vector(0f, 0f)
        val expectedResultMomenta = 0F

        assertEquals(expectedResultForce, Stabilization.getResultForce(structureB))
        assertEquals(expectedResultMomenta, Stabilization.getResultMomentum(structureB))
    }
}