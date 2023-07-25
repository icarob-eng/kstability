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
    fun Structure.parseNodeSection(arg: Map<String, Any>){
        arg.map { (name, vectorMap) ->
            Node(name, parseVector(vectorMap), this)
        }
    }

    fun Structure.findNode(name: String): Node = nodes.find { it.name == name }
        ?: throw IllegalArgumentException("Referenced node $name not in the structure.")

    @Throws(IllegalArgumentException::class)
    fun Structure.parseSupportSection(arg: Map<String, Map<String, Any>>) {
        arg.entries.map { (nodeStr, supportMap) ->
            val node = findNode(nodeStr)

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
    fun Structure.parseBeamSection(arg: List<List<String>>) {
        arg.map {
            beamLst ->
            if (beamLst.size != 2) throw IllegalArgumentException("Invalid node quantity for a beam. Value = $beamLst")
            val nodes: List<Node> = beamLst.map { nodeStr -> findNode(nodeStr) }
            Beam(nodes[0], nodes[1])  // this automatically adds itself to the structure
        }
    }

    @Throws(IllegalArgumentException::class)
    fun Structure.parseLoadSection(arg: Map<String, Map<String, Any>>) {
        arg.map { (_, map) ->
            val keys = map.keys.toSet().map { key -> key.lowercase() }

            val vector = if(s.vector in keys) parseVector(map[s.vector]) else
                if (s.direction in keys && s.module in keys) parseVector(map[s.direction]) * map[s.module] as Number
                else throw IllegalArgumentException("Invalid load vector syntax. Value = $map")

            val nodeKey = if (s.node in keys) s.node else if (s.nodes in keys) s.node
            else throw IllegalArgumentException("Invalid load syntax, ${s.node} or ${s.nodes} expected.")


            val nodes = map[nodeKey]

            if (nodes is String)
                PointLoad(findNode(nodes), vector)

            else if (nodes is List<*> && nodes.all { it is String } && nodes.size == 2)
                DistributedLoad(findNode(nodes[0] as String), findNode(nodes[1] as String), vector)

            else
                throw IllegalArgumentException("Invalid load syntax. Value = $map")
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalArgumentException::class)
    fun parseSections(arg: Map<String, Any>): Structure {

        val structure = Structure(
            arg[s.structureSection] as? String?: throw IllegalArgumentException("Invalid structure name.")
        )

        try { structure.parseNodeSection(arg[s.nodesSection] as Map<String, Any>) }
        catch (e: ClassCastException) { throw IllegalArgumentException("Invalid node section.") }

        try { structure.parseSupportSection(arg[s.supportSection] as Map<String, Map<String, Any>>) }
        catch (e: ClassCastException) { throw IllegalArgumentException("Invalid support section.") }

        try { structure.parseBeamSection(arg[s.nodesSection] as List<List<String>>)}
        catch (e: ClassCastException) { throw IllegalArgumentException("Invalid beam section.") }

        try { structure.parseLoadSection(arg[s.nodesSection] as Map<String, Map<String, Any>>) }
        catch (e: ClassCastException) { throw IllegalArgumentException("Invalid load section.") }

        return structure
    }
}