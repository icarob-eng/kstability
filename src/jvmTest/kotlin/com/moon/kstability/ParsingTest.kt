package com.moon.kstability

import com.moon.kstability.Support.Gender
import com.moon.kstability.Vector.Consts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class ParsingTest {
    private val structureSample = Structure("Sample Structure", hashSetOf(
        Node("A", Vector(0, 0)).apply { support = Support(this, Gender.FIRST, Consts.VERTICAL) },
        Node("B", Vector(0, 1)).apply { pointLoads.add(PointLoad(this, Vector(0,-10))) },
        Node("C", Vector(0, 2)).apply { support = Support(this, Gender.SECOND, Consts.VERTICAL) }
    )).also { Beam(it["A"]!!, it["C"]!!) }

    private val pathA = "src/jvmTest/kotlin/com/moon/kstability/sampleA.yaml"
    private val pathB = "src/jvmTest/kotlin/com/moon/kstability/sampleB.yaml"

    @Test
    fun parseFromYamlStructureTest() {
        assertEquals(structureSample, Parsing.parseYamlString(
            File(pathA).readText()
        ))
    }

    @Test
    fun parseAndSerializeTest() {
        assertEquals(structureSample, Parsing.parseYamlString(Parsing.serializeStructureToYaml(structureSample)))
    }

    @Test
    fun readAndWriteFileTest() {
        val f = File(pathB)
        f.writeText(Parsing.serializeStructureToYaml(structureSample))
        val structureResult = Parsing.parseYamlString(f.readText())
        assertEquals(structureSample, structureResult)
    }
}