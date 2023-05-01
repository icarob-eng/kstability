package com.kstabilty

import com.kstabilty.Vector.Consts
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StructureModelTest {
    private val knotSampleA = Knot("A", Vector(0F, 2F))
    private val knotSampleB = Knot("B", Vector(1F, 2F))
    private val knotSampleC = Knot("C", Vector(3F, 2F))
    private val knotSampleD = Knot("D", Vector(4F, 2F))

    private val supportSampleA = Support(knotSampleA, Support.Gender.SECOND, Vector(0F, 5F))
    private val supportSampleB = Support(knotSampleD, Support.Gender.FIRST, Consts.VERTICAL)
//    private val barSample = Bar(knotSampleA, knotSampleD)

    private val structureSample = Structure("My structure", mutableListOf(knotSampleA, knotSampleB, knotSampleC, knotSampleD))

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

        val distributedLoadSample = DistributedLoad(knotSampleB, knotSampleC, Vector(0F, 10F))
        val eqvPointLoadSample = PointLoad(Knot("", Vector(2F, 2F)), Vector(0F, 20F))
        assertEquals(true, distributedLoadSample.getEqvLoad() == eqvPointLoadSample)
    }

    @Test
    fun structureLoadsHolding() {
        val distributedLoadSampe = DistributedLoad(knotSampleB, knotSampleC, Vector(0F, 10F))
        val pointLoadSampleA = PointLoad(knotSampleB, Vector(0F, 10F))
        val pointLoadSampleB = PointLoad(knotSampleB, Consts.VERTICAL * 10F)

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
            DistributedLoad(knotSampleB, knotSampleC, Vector(0F, 10F)).getEqvLoad(),
            PointLoad(knotSampleB, Vector(0F, 10F)),
            PointLoad(knotSampleB, Consts.VERTICAL * 10F)
        )

        assertEquals(expected, structureSample.getEqvLoads().toSet())
    }

    @Test
    fun rotateAll() {
        val i = Float.POSITIVE_INFINITY
        val rotatedKnotPosSampleC = knotSampleC.pos.getRotated(i)
        val rotatedSupportDirSampleB = supportSampleB.direction.getRotated(i)

        val samplePointLoad = PointLoad(knotSampleD, Vector(0,3))
        val rotatedPointLoadVector = samplePointLoad.vector.getRotated(i)


        val rotatedStructure = structureSample.getRotatedCopy(i)

        assertAll("rotateAll",
            { assertEquals(rotatedKnotPosSampleC, rotatedStructure.knots.first{ it.name == "C"}.pos) },
            { assertEquals(rotatedSupportDirSampleB, rotatedStructure.getSupports()[1].direction) },
            { assertEquals(i, rotatedStructure.getBars().first().inclination) }, // bar inclination == i
            { assertEquals(rotatedPointLoadVector, rotatedStructure.getPointLoads().first().vector) },
        )
    }
}