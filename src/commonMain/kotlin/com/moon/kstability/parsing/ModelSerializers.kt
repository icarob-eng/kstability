package com.moon.kstability.parsing

import com.moon.kstability.*

object ModelSerializers {
    private val s = StringsPtBrObject

    fun Vector.serialize(): Map<String, Float> = mapOf(s.x to x, s.y to y)

    fun Beam.serialize(): List<String> = listOf(node1.name, node2.name)

    fun Support.serialize(): Pair<String, Map<String, Any>> =
        Pair(node.name, mapOf(
            s.gender to gender.reactions,
            s.direction to when (direction) {
                Vector.VERTICAL -> s.vertical
                Vector.HORIZONTAL -> s.horizontal
                else -> direction.serialize()
            }
        ))

    fun Node.serialize(): Pair<String, Map<String, Float>> = Pair(name, pos.serialize())

    fun PointLoad.serialize(): Map<String, Any> = mapOf(
        s.node to node.name,
        s.vector to vector.serialize()
    )

    fun DistributedLoad.serialize(): Map<String, Any> = mapOf(
        s.nodes to listOf(node1.name, node2.name),
        s.vector to vector.serialize()
    )

    fun Structure.serialize(): Map<String, Any> = mapOf(
        s.structureSection to name,
        s.nodesSection to nodes.associate { it.serialize() },
        s.supportSection to getSupports().associate { it.serialize() },
        s.beamsSection to getBeams().map { it.serialize() },
        s.loadsSection to (getPointLoads().map { it.serialize() } + getDistributedLoads().map { it.serialize() })
            .mapIndexed { i, load -> Pair("F$i", load) }.toMap()
    )
}