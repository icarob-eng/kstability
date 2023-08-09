package com.moon.kstability.parsing

import com.moon.kstability.*

object ModelSerializers {
    fun Vector.serialize(lang: YamlStrings): Map<String, Float> = mapOf(lang.x to x, lang.y to y)

    fun Beam.serialize(): List<String> = listOf(node1.name, node2.name)

    fun Support.serialize(lang: YamlStrings): Pair<String, Map<String, Any>> =
        Pair(node.name, mapOf(
            lang.gender to gender.reactions,
            lang.direction to when (direction) {
                Vector.VERTICAL -> lang.vertical
                Vector.HORIZONTAL -> lang.horizontal
                else -> direction.serialize(lang)
            }
        ))

    fun Node.serialize(lang: YamlStrings): Pair<String, Map<String, Float>> = Pair(name, pos.serialize(lang))

    fun PointLoad.serialize(lang: YamlStrings): Map<String, Any> = mapOf(
        lang.node to node.name,
        lang.vector to vector.serialize(lang)
    )

    fun DistributedLoad.serialize(lang: YamlStrings): Map<String, Any> = mapOf(
        lang.nodes to listOf(node1.name, node2.name),
        lang.vector to vector.serialize(lang)
    )

    fun Structure.serialize(lang: YamlStrings): Map<String, Any> = mapOf(
        lang.structureSection to name,
        lang.nodesSection to nodes.associate { it.serialize(lang) },
        lang.supportSection to getSupports().associate { it.serialize(lang) },
        lang.beamsSection to getBeams().map { it.serialize() },
        lang.loadsSection to (getPointLoads().map { it.serialize(lang) } + getDistributedLoads().map { it.serialize(lang) })
            .mapIndexed { i, load -> Pair("F$i", load) }.toMap()
    )
}