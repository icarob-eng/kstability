package com.moon.kstability.parsing

import com.moon.kstability.*

object ModelParsers {
    private val s = StringsPtBr

    @Throws(IllegalArgumentException::class)
    fun parseVector(arg: Any?): Vector {
        try {
            return if (arg is List<*> && arg.all { it is Number } && arg.size == 2) {
                // case [0,1]
                Vector(arg[0] as Number, arg[1] as Number)
            } else if (arg is Map<*, *> && arg.keys.toSet().map { (it as? String)?.lowercase() } == setOf(s.x, s.y)) {
                // case x: 0, y: 1
                Vector(arg[s.x] as Number, arg[s.y] as Number)
            } else if ((arg as? String)?.lowercase() == s.vertical) {
                // case vertical
                Vector.Consts.VERTICAL
            } else if ((arg as? String)?.lowercase() == s.horizontal) {
                // case horizontal
                Vector.Consts.HORIZONTAL
            } else {
                throw ClassCastException()
            }
        } catch (e: ClassCastException) {
            throw IllegalArgumentException("Invalid vector. Value = $arg")
        }
    }

    @Throws(IllegalArgumentException::class)
    fun parseNodeSection(arg: Map<String, Any>, structure: Structure){
        arg.map { (name, vectorMap) ->
            Node(name, parseVector(vectorMap), structure)
        }
    }

    @Throws(IllegalArgumentException::class)
    fun parseSupportSection(arg: Map<String, Map<String, Any>>, structure: Structure) {
        arg.entries.map { (nodeStr, supportMap) ->
            val node = structure.nodes.find { node -> node.name == nodeStr }
                ?: throw IllegalArgumentException("Referenced node $nodeStr not in the structure.")

            if (supportMap.keys.toSet().map { key -> key.lowercase() } != setOf(s.gender, s.direction))
                throw IllegalArgumentException("Invalid support syntax. Value = $supportMap")

            node.support = Support(
                node,
                when(supportMap[s.gender]) {
                    1 -> Support.Gender.FIRST
                    2 -> Support.Gender.SECOND
                    3 -> Support.Gender.THIRD
                    else -> throw IllegalArgumentException("Invalid support gender. Value = ${supportMap[s.gender]}")
                },
                parseVector(supportMap[s.direction])
                )
        }
    }

    @Throws(IllegalArgumentException::class)
    fun parseBeamSection(arg: List<List<String>>, structure: Structure) {
        arg.map {
            beamLst ->
            if (beamLst.size != 2) throw IllegalArgumentException("Invalid node quantity for a beam. Value = $beamLst")
            val nodes: List<Node> = beamLst.map { nodeStr ->
                structure.nodes.find { it.name == nodeStr }
                    ?: throw IllegalArgumentException("Referenced node $nodeStr not in the structure.")
            }
            nodes.sortedBy { it.pos.x }
            Beam(nodes[0], nodes[1])  // this automatically adds itself to the structure
        }
    }
}