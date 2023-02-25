import com.kstabilty.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StructureModelTest {
    private val knotSampleA = Knot("A", Vector(0F, 2F))
    private val knotSampleB = Knot("B", Vector(1F, 2F))
    private val knotSampleC = Knot("C", Vector(3F, 2F))
    private val knotSampleD = Knot("D", Vector(4F, 2F))

    private val supportSampleA = Support(knotSampleA, SupportGender.SECOND, Vector(0F, 5F))
    private val supportSampleB = Support(knotSampleD, SupportGender.FIRST, Consts.VERTICAL)
    private val barSample = Bar(knotSampleA, knotSampleD)

    private val structureSample = Structure("My structure", mutableListOf(knotSampleA, knotSampleB, knotSampleC, knotSampleD))

    @Test
    fun supportDirection() {
        assertEquals(Consts.VERTICAL, supportSampleA.direction)
    }

    @Test
    fun distrubutedLoadEq() {
        /**
         * Although unrecomended, this test covers:
         * - `DistributedLoad.getEqvLoad()`
         * - `Knot.equals()`
         * - `Load.equals()`
         */

        val distributedLoadSample = DistributedLoad(knotSampleB, knotSampleC, 10F)
        val eqvPointLoadSample = PointLoad(Knot("", Vector(2F, 2F)), Vector(0F, 20F))
        assertEquals(true, distributedLoadSample.getEqvLoad() == eqvPointLoadSample)
    }

    @Test
    fun structureLoadsHolding() {
        val distributedLoadSampe = DistributedLoad(knotSampleB, knotSampleC, 10F)
        val pointLoadSampleA = PointLoad(knotSampleB, Vector(0F, 10F))
        val pointLoadSampleB = PointLoad(knotSampleB, Consts.VERTICAL * 10F)

        assertEquals(distributedLoadSampe, structureSample.getDistributedLoads()[0])
        assertEquals(pointLoadSampleA, structureSample.getLoads()[0])
        assertEquals(pointLoadSampleB, structureSample.getLoads()[1])
        // kinda surprise that this passes
    }

    @Test
    fun structure_getEqvLoads() {
        val expected = setOf(  // using sets gurantees that the order will not matter
            DistributedLoad(knotSampleB, knotSampleC, 10F).getEqvLoad(),
            PointLoad(knotSampleB, Vector(0F, 10F)),
            PointLoad(knotSampleB, Consts.VERTICAL * 10F)
        )

        assertEquals(expected, structureSample.getEqvLoads().toSet())
    }
}