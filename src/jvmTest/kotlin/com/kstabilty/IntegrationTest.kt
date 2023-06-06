package com.kstabilty

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertAll

import kotlin.io.path.*
import kotlin.test.assertEquals

class IntegrationTest {
    companion object {
        private const val outputPath = "src/jvmTest/kotlin/com/kstabilty/IntegrationTestOutput"

        @JvmStatic
        @BeforeAll
        fun shredOldFiles() = Path(outputPath).forEachDirectoryEntry { it.deleteExisting() }

        fun writeCsv(fileName: String, axes: Axes) {
            val writer = Path("$outputPath/$fileName").let {
                it.createFile()
                it.bufferedWriter()
            }

            writer.write("x, y".trimIndent())

            for (i in 0 until axes.first.size) {
                writer.newLine()
                writer.write("${axes.first[i]}, ${axes.second[i]}")
            }

            writer.flush()
        }
    }

    private val basicStructureSampleA = Structure("Basic Sample A", mutableListOf(
        Node("A", Vector(0,0)).apply {
            Support(this, Support.Gender.SECOND, Vector.Consts.VERTICAL)
        },
        Node("B", Vector(1, 0)).apply {
            PointLoad(this, Vector(0,-10))
        },
        Node("C", Vector(2,0)).apply {
            Support(this, Support.Gender.FIRST, Vector.Consts.VERTICAL)
        }
    )).also {
        Beam(it.nodes.first(), it.nodes.last())
    }

    private val basicStructureSampleB = Structure("Basic Sample B", mutableListOf(
        Node("A", Vector(0,0)).apply {
            Support(this, Support.Gender.SECOND, Vector.Consts.VERTICAL)
        },
        Node("B", Vector(2,0)).apply {
            Support(this, Support.Gender.FIRST, Vector.Consts.VERTICAL)
        }
    )).also {
        Beam(it.nodes.first(), it.nodes.last())
        DistributedLoad(it.nodes.first(), it.nodes.last(), Vector(0, -5))
    }

    @Test
    fun writeCsvFromShearBasicSampleA() {
        Stabilization.stabilize(basicStructureSampleA)
        val output = Diagrams.getDiagram(
            basicStructureSampleA,
            basicStructureSampleA.getBeams().first(),
            Diagrams::generateShearFunction,
            0.01F
        )

        writeCsv("basic_sample_a_shear_stress", output.first)

        assertAll("basic sample a - shear stress polynomials test",
            { assertEquals(Polynomial(0F, 0F, 5F), output.second[0]) },
            { assertEquals(Polynomial(0F, 0F, -5F), output.second[1]) }
        )
    }

    @Test
    fun writeCsvFromMomentBasicSampleA() {
        Stabilization.stabilize(basicStructureSampleA)
        val output = Diagrams.getDiagram(
            basicStructureSampleA,
            basicStructureSampleA.getBeams().first(),
            Diagrams::generateMomentFunction,
            0.01F
        )

        writeCsv("basic_sample_a_bending_moment", output.first)

        assertAll("basic sample a - bending moment polynomials test ",
            { assertEquals(Polynomial(0F, 5F, 0F), output.second[0]) },
            { assertEquals(Polynomial(0F, -5F, 10F), output.second[1]) }
        )
    }

    @Test
    fun writeCsvFromShearBasicSampleB() {
        Stabilization.stabilize(basicStructureSampleB)
        val output = Diagrams.getDiagram(
            basicStructureSampleB,
            basicStructureSampleB.getBeams().first(),
            Diagrams::generateShearFunction,
            0.01F
        )

        writeCsv("basic_sample_b_shear_stress", output.first)

        assertEquals(Polynomial(0F, -5F, 5F), output.second[0])
    }

    @Test
    fun writeCsvFromMomentBasicSampleB() {
        Stabilization.stabilize(basicStructureSampleB)
        val output = Diagrams.getDiagram(
            basicStructureSampleB,
            basicStructureSampleB.getBeams().first(),
            Diagrams::generateMomentFunction,
            0.01F
        )

        writeCsv("basic_sample_b_bending_moment", output.first)

        assertEquals(Polynomial(-2.5F, 5F, 0F), output.second[0])
    }

    /*
    PROBLEMA RESOLVIDO 7.4 (p.378) do livro MECÂNICA VETORIAL PARA ENGENHEIROS: ESTÁTICA.
    Ferdinand P. Beer...(et al.) 9ª edição, 2012.
    */
    private val structureSampleA = Structure("Sample A", mutableListOf(
        Node("A", Vector(0, 0)).apply {
            Support(this, Support.Gender.SECOND, Vector.Consts.VERTICAL)
        },
        Node("B", Vector(1.8, 0)).apply {
            PointLoad(this, Vector(0, -90.0))
        },
        Node("C", Vector(2.4 + 1.8, 0)).apply {
            PointLoad(this, Vector(0, 54.0))
        },
        Node("D", Vector(3 + 2.4 + 1.8, 0)).apply {
            Support(this, Support.Gender.FIRST, Vector.Consts.VERTICAL)
        },
        Node("E", Vector(2.4 + 3 + 2.4 + 1.8, 0))
    )).also {
        Beam(it.nodes.first(), it.nodes.last())
        DistributedLoad(it.nodes[3], it.nodes[4], Vector(0, 22.50))
    }

    /*
    PROBLEMA RESOLVIDO 7.6 (p.379) do livro MECÂNICA VETORIAL PARA ENGENHEIROS: ESTÁTICA.
    Ferdinand P. Beer...(et al.) 9ª edição, 2012.
    */
    private val structureSampleB = Structure("Sample B", mutableListOf(
        Node("A", Vector(0,0)).apply {
            Support(this, Support.Gender.SECOND, Vector.Consts.VERTICAL)
        },
        Node("B", Vector(6,0)),
        Node("C", Vector(3 + 6,0)).apply {
            Support(this, Support.Gender.FIRST, Vector.Consts.VERTICAL)
        }
    )).also {
        Beam(it.nodes.first(), it.nodes.last())
        DistributedLoad(it.nodes[0], it.nodes[1], Vector(0, 20))
    }

    @Test
    fun writeCsvFromShearSampleA() {
        Stabilization.stabilize(structureSampleA)
        writeCsv("sample_a_shear_stress", Diagrams.getDiagram(
            structureSampleA,
            structureSampleA.getBeams().first(),
            Diagrams::generateShearFunction,
            0.01F
        ).first)
    }

    @Test
    fun writeCsvFromMomentSampleA() {
        Stabilization.stabilize(structureSampleA)
        writeCsv("sample_a_bending_moment", Diagrams.getDiagram(
            structureSampleA,
            structureSampleA.getBeams().first(),
            Diagrams::generateMomentFunction,
            0.01F
        ).first)
    }

    @Test
    fun writeCsvFromShearSampleB() {
        Stabilization.stabilize(structureSampleB)
        writeCsv("sample_b_shear_stress", Diagrams.getDiagram(
            structureSampleB,
            structureSampleB.getBeams().first(),
            Diagrams::generateShearFunction,
            0.01F
        ).first)
    }

    @Test
    fun writeCsvFromMomentSampleB() {
        Stabilization.stabilize(structureSampleB)
        writeCsv("sample_b_bending_moment", Diagrams.getDiagram(
            structureSampleB,
            structureSampleB.getBeams().first(),
            Diagrams::generateMomentFunction,
            0.01F
        ).first)
    }
}