package com.moon.kstability

import com.moon.kstability.Support.Gender
import com.moon.kstability.Vector.Consts
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class ParseIntegrationTest {
    private val structureSample = Structure("Sample Structure", mutableListOf(
        Node("A", Vector(0, 0)).apply { support = Support(this, Gender.FIRST, Consts.VERTICAL) },
        Node("B", Vector(0, 0)).apply { pointLoads.add(PointLoad(this, Vector(0,-10))) },
        Node("C", Vector(0, 0)).apply { support = Support(this, Gender.SECOND, Consts.VERTICAL) }
    )).also { Beam(it.nodes.first(), it.nodes.last()) }

    @Test
    fun parseFromYamlStructureTest() {
//        assertEquals(structureSample, Parser("com/moon/kstability/sample.yaml"))
    }
}