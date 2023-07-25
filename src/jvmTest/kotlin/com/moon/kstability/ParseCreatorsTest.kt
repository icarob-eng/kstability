package com.moon.kstability

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParseCreatorsTest {

    @Test
    fun isNodeANode() {
        assertEquals(true, NodeCreator(mapOf("A" to arrayOf(0, 0))).isNode, "The isNode attribute works accordingly.")
    }

    @Test
    fun nodeCreatorTransformationTest() {
        assertEquals(
            Node(name = "A", pos = Vector(x = 0, y = 0)), NodeCreator(
                mapOf("A" to arrayOf(0, 0))
            ).createNode(), ""
        )
    }

    @Test
    fun isSupportInFirstNotationASupport() {
        assertEquals(true, SupportCreator(mapOf("A" to mapOf("gênero" to 1, "direção" to "vertical"))).isSupport)
    }

    @Test
    fun isSupportInSecondNotationASupport() {
        assertEquals(true, SupportCreator(mapOf("A" to mapOf("gênero" to 1, "direção" to arrayOf(0, 1)))).isSupport)
    }

    @Test
    fun supportCreatorTransformationTest() {
        assertEquals(
            Support(
                node = Node("A", Vector(x = 1, y = 1)),
                gender = Support.Gender.FIRST,
                dir = Vector(x = 0, y = 1)
            ),
            SupportCreator(mapOf("A" to mapOf("gênero" to 1, "direção" to arrayListOf(0, 1)))).createSupport(
                mutableListOf(
                    Node("A", Vector(x = 1, y = 1))
                )
            )
        )
    }

    @Test
    fun isBeamNotationABeam() {
        assertEquals(true, BeamCreator(arrayListOf("A", "B")).isBeam)
    }

    @Test
    fun beamCreatorTransformationTest() {
        assertEquals(
            Beam(
                node1 = Node(
                    name = "A",
                    pos = Vector(
                        x = 0,
                        y = 0
                    )
                ),
                node2 = Node("B", pos = Vector(x = 1, y = 1))
            ),
            BeamCreator(arrayListOf("A", "B")).createBeam(
                mutableListOf(
                    Node(
                        name = "A",
                        pos = Vector(
                            x = 0,
                            y = 0
                        )
                    ),
                    Node("B", pos = Vector(x = 1, y = 1))
                )
            )
        )
    }

    @Test
    fun isPointLoadNotationAPointLoad() {
        assertEquals(
            true,
            PointLoadCreator(
                mapOf("F1" to mapOf("nó" to "A", "direção" to "horizontal", "módulo" to 200))
            ).isLoad
        )
    }

    @Test
    fun isDistributedLoadNotationAPointLoad() {
        assertEquals(
            true,
            PointLoadCreator(
                mapOf("F1" to mapOf("nó" to arrayOf("A", "B"), "direção" to "horizontal", "módulo" to 200))
            ).isLoad
        )
    }

    @Test
    fun nodeManagerIsANodeSectionTest() {
        assertEquals(
            true,
            NodesManager(mapOf("cargas" to mapOf("A" to arrayOf(0, 0), "A" to arrayOf(0, 0)))).isANodeSection
        )
    }

    @Test
    fun nodeMangerIsAnyLoad() {
        assertEquals(
            true, NodesManager(
                mapOf(
                    "cargas" to mapOf(
                        "A" to arrayOf(0, 0)
                    )
                )
            ).isAnyNode
        )
        assertEquals(
            false, NodesManager(
                mapOf(
                    "cargas" to arrayOf(1, 2, 3)
                )
            ).isAnyNode
        )
    }

    @Test
    fun supportManagerIsASupportSection() {
        assertEquals(
            true,
            SupportsManager(
                mapOf(
                    "apoios" to mapOf(
                        "A" to mapOf(
                            "gênero" to 1,
                            "direção" to "vertical"
                        )
                    )
                )
            ).isASupportSection
        )
        assertEquals(false, SupportsManager(mapOf("apoios" to mapOf("A" to 1))).isASupportSection)
    }

    @Test
    fun supportManagerIsAnySupport() {
        assertEquals(
            true,
            SupportsManager(mapOf("apoios" to mapOf("A" to mapOf("gênero" to 1, "direção" to "vertical")))).isAnySupport
        )
        assertEquals(
            false, SupportsManager(
                mapOf(
                    "apoios" to mapOf("A" to 1)
                )
            ).isAnySupport
        )
    }

    @Test
    fun supportManagerIsAllSupports() {
        assertEquals(
            true, SupportsManager(
                mapOf(
                    "apoios" to mapOf(
                        "A" to mapOf(
                            "gênero" to 1,
                            "direção" to arrayListOf(0, 0)
                        ),
                        "B" to mapOf(
                            "gênero" to 2,
                            "direção" to arrayListOf(0, 1)
                        )
                    )
                )
            ).isAllSupports
        )
        assertEquals(
            false, SupportsManager(
                mapOf(
                    "apoios" to mapOf(
                        "A" to mapOf(
                            "gênero" to 1,
                            "direção" to arrayListOf(0, 0)
                        ),
                        "B" to arrayOf(1, 0)
                    )
                )
            ).isAllSupports
        )
    }

    @Test
    fun isABeamSection() {
        assertEquals(true, BeamsManager(mapOf("barras" to 0)).isABeamSection)
        assertEquals(false, BeamsManager(mapOf("bananas" to 0)).isABeamSection)
    }

    @Test
    fun isALoadSectionPointLoadsManager() {
        assertEquals(
            true, PointLoadsManager(
                mapOf(
                    "cargas" to mapOf(
                        "F1" to mapOf(
                            "nó" to "A",
                            "direção" to "horizontal",
                            "módulo" to 200
                        )
                    )
                )
            ).isALoadSection
        )
        assertEquals(false, PointLoadsManager(mapOf("banana" to 3)).isALoadSection)
    }

    @Test
    fun isAllLoadsPointLoadsManager() {
        assertEquals(
            true, PointLoadsManager(
                mapOf(
                    "cargas" to mapOf(
                        "F1" to mapOf(
                            "nó" to "A",
                            "direção" to "horizontal",
                            "módulo" to 200
                        )
                    )
                )
            ).isAllLoads
        )
        assertEquals(
            false, PointLoadsManager(
                mapOf(
                    "cargas" to mapOf(
                        "F1" to mapOf(
                            "nó" to "A",
                            "direção" to "horizontal",
                            "módulo" to 200
                        ),
                        "F2" to 2
                    )
                )
            ).isAllLoads
        )
    }

    @Test
    fun isAnyLoadPointLoadManager() {
        assertEquals(
            true, PointLoadsManager(
                mapOf(
                    "cargas" to mapOf(
                        "F1" to mapOf(
                            "nó" to "A",
                            "direção" to "horizontal",
                            "módulo" to 200
                        ),
                        "F2" to 2
                    )
                )
            ).isAnyALoad
        )
        assertEquals(
            false, PointLoadsManager(
                mapOf(
                    "cargas" to mapOf(
                        "F1" to 34,
                        "F2" to 2
                    )
                )
            ).isAnyALoad
        )
    }

}