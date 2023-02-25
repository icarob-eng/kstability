import com.kstabilty.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class StabilizationTest {
    private val knotSampleA = Knot("A", Vector(0F, 2F))
    private val knotSampleB = Knot("B", Vector(1F, 2F))
    private val knotSampleC = Knot("C", Vector(3F, 2F))
    private val knotSampleD = Knot("D", Vector(4F, 2F))

    private val pointLoadSampleA = PointLoad(knotSampleB, Vector(0, -20))
    private val pointLoadSampleB = PointLoad(knotSampleC, Vector(0, -10))

    private val supportSampleA = Support(knotSampleA, SupportGender.SECOND, Consts.VERTICAL)
    private val supportSampleB = Support(knotSampleD, SupportGender.FIRST, Consts.VERTICAL)
    private val barSample = Bar(knotSampleA, knotSampleD)

    private val structureSample = Structure("My structure", mutableListOf(knotSampleA, knotSampleB, knotSampleC, knotSampleD))

    /**
     * Structure Sample expectetions:
     * - Resultant force from loads: Vector(0, -30)
     * - Resultant bending moment from loads: - 20 * 1 - 10 * 3 = -50,
     * therefore, the positive rotation direction is counter-clockwise
     * - Reaction forces: A + D = -30, D * 4 = -50 => D = -12.5, A = -17.5
     *
     * Also, the resultant force after stabilization should be 0.
      */

    private val expectedResultForce = Vector(0, -30)
    private val expectedBendingMoment = -20 * 1 - 10 * 3F
    private val expectedReactionA = Vector(0.0, 17.5)
    private val expectedReactionD = Vector(0.0, 12.5)


    @Test
    fun isostaticTest() {
        assertTrue(Stabilization.isIsostatic(structureSample))
    }
    // todo: check isostacity in more complex strcutures

    @Test
    fun getResultMomementumTest() {
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
    fun checkSatabilizedForcesTest() {
        /**
         * Checks if the expected forces are put in the strucuture sample
         */

        val expected = Pair(
            expectedReactionA,
            expectedReactionD
        )

        Stabilization.stabilize(structureSample)
        val data = Pair(
            structureSample.getSupports()[0].knot.pointLoads[0].vector,
            structureSample.getSupports()[1].knot.pointLoads[0].vector
        )

        assertEquals(expected, data)
    }

    @Test
    fun resultForceManuallyStabilizedTest() {
        /**
         * Checks if the expected reaction forces in the structure makes the result force 0
         */

        val expected = Vector(0, 0)

        PointLoad(knotSampleA, expectedReactionA)
        PointLoad(knotSampleD, expectedReactionD)

        assertEquals(expected, Stabilization.getResultForce(structureSample))
    }

    @Test
    fun resultMomentumManuallyStabilizedTest() {
        /**
         * Checks if the expected reaction forces in the structure makes the result momentum 0
         */
        val expected = 0F

        PointLoad(knotSampleA, expectedReactionA)
        PointLoad(knotSampleD, expectedReactionD)

        assertEquals(expected, Stabilization.getResultMomentum(structureSample))
    }

    @Test
    fun resultForceStabilizedTest() {
        val expected = Vector(0, 0)

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
        PointLoad(knotSampleA, expectedReactionA)
        PointLoad(knotSampleD, expectedReactionD)

        assertTrue(Stabilization.isStable(structureSample))
    }

    @Test
    fun isStabilizationStableTest() {
        Stabilization.stabilize(structureSample)
        assertTrue(Stabilization.isStable(structureSample))
    }

    @Test
    fun isNotIsostaticTest() {
        val knotSampleA1 = Knot("A", Vector(0F, 2F))
        val knotSampleB1 = Knot("B", Vector(4F, 2F))

        PointLoad(knotSampleA1, Vector(0, -3))
        Support(knotSampleB1, SupportGender.SECOND, Consts.HORIZONTAL)

        val structureB = Structure("Fixed support strcuture",
            mutableListOf(knotSampleA1, knotSampleB1)
        )

        assertThrows(IllegalArgumentException::class.java) {Stabilization.stabilize(structureB)}
    }

    @Test
    fun isStabilizationStableFixedSupportTest() {
        val knotSampleA1 = Knot("A", Vector(0F, 2F))
        val knotSampleB1 = Knot("B", Vector(4F, 2F))

        PointLoad(knotSampleA1, Vector(0, -3))
        Support(knotSampleB1, SupportGender.THIRD, Consts.HORIZONTAL)

        val structureB = Structure("Fixed support strcuture",
            mutableListOf(knotSampleA1, knotSampleB1)
        )

        Stabilization.stabilize(structureB)

        val expectedResultForce = Vector(0, 0)
        val expectedResultMomenta = 0F

        assertEquals(expectedResultForce, Stabilization.getResultForce(structureB))
        assertEquals(expectedResultMomenta, Stabilization.getResultMomentum(structureB))
    }
}