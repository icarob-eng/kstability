package com.moon.kstability

import com.moon.kstability.Vector.Consts
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StructureModelTest {
    private val nodeSampleA = Node("A", Vector(0F, 2F))
    private val nodeSampleB = Node("B", Vector(1F, 2F))
    private val nodeSampleC = Node("C", Vector(3F, 2F))
    private val nodeSampleD = Node("D", Vector(4F, 2F))
        .apply { Beam(nodeSampleA, this) }

    private val supportSampleA = Support(nodeSampleA, Support.Gender.SECOND, Vector(0F, 5F))
    private val supportSampleB = Support(nodeSampleD, Support.Gender.FIRST, Consts.VERTICAL)

    private val structureSample = Structure("My structure", mutableListOf(nodeSampleA, nodeSampleB, nodeSampleC, nodeSampleD))

    @Test
    fun supportDirection() {
        assertEquals(Consts.VERTICAL, supportSampleA.direction)
    }

    @Test
    fun distributedLoadEq() {
        /**
         * Although unrecommended, this test covers:
         * - `DistributedLoad.getEqvLoad()`
         * - `Knot.equals()`
         * - `Load.equals()`
         */

        val distributedLoadSample = DistributedLoad(nodeSampleB, nodeSampleC, Vector(0F, 10F))
        val eqvPointLoadSample = PointLoad(Node("", Vector(2F, 2F)), Vector(0F, 20F))
        assertEquals(true, distributedLoadSample.getEqvLoad() == eqvPointLoadSample)
    }

    @Test
    fun structureLoadsHolding() {
        val distributedLoadSampe = DistributedLoad(nodeSampleB, nodeSampleC, Vector(0F, 10F))
        val pointLoadSampleA = PointLoad(nodeSampleB, Vector(0F, 10F))
        val pointLoadSampleB = PointLoad(nodeSampleB, Consts.VERTICAL * 10F)

        assertAll( "LoadsHolding",
            {assertEquals(distributedLoadSampe, structureSample.getDistributedLoads()[0])},
            {assertEquals(pointLoadSampleA, structureSample.getPointLoads()[0])},
            {assertEquals(pointLoadSampleB, structureSample.getPointLoads()[1])}
        )
        // kinda surprise that this passes
    }

    @Test
    fun structure_getEqvLoads() {
        val expected = setOf(  // using sets guarantees that the order will not matter
            DistributedLoad(nodeSampleB, nodeSampleC, Vector(0F, 10F)).getEqvLoad(),
            PointLoad(nodeSampleB, Vector(0F, 10F)),
            PointLoad(nodeSampleB, Consts.VERTICAL * 10F)
        )

        assertEquals(expected, structureSample.getEqvLoads().toSet())
    }

    @Test
    fun rotateAll() {
        val i = Float.POSITIVE_INFINITY
        val rotatedKnotPosSampleC = nodeSampleC.pos.getRotated(i)
        val rotatedSupportDirSampleB = supportSampleB.direction.getRotated(i)

        val samplePointLoad = PointLoad(nodeSampleD, Vector(0f,3f))
        val rotatedPointLoadVector = samplePointLoad.vector.getRotated(i)


        val rotatedStructure = structureSample.getRotatedCopy(i)

        assertAll("rotateAll",
            { assertEquals(rotatedKnotPosSampleC, rotatedStructure.nodes.first{ it.name == "C"}.pos) },
            { assertEquals(rotatedSupportDirSampleB, rotatedStructure.getSupports()[1].direction) },
            { assertEquals(i, rotatedStructure.getBeams().first().beamVector.inclination()) }, // bar inclination == i
            { assertEquals(rotatedPointLoadVector, rotatedStructure.getPointLoads().first().vector) },
        )
    }
}