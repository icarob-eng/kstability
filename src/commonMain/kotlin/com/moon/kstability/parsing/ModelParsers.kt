package com.moon.kstability.parsing

import com.moon.kstability.*

object ModelParsers {
    private val s = StringsPtBr

    /**
     * Parses the many possible syntax for a `Vector`. See parsing documentation for possible syntax.
     *
     * @param arg Some data structure correspondent to a `Vector`.
     * @return `Vector`.
     * @throws IllegalArgumentException
     * @see Vector
     */
    @Throws(IllegalArgumentException::class)
    fun parseVector(arg: Any?): Vector {
        try {
            return if (arg is List<*> && arg.all { it is Number } && arg.size == 2) {
                // case [0,1]
                Vector(arg[0] as Number, arg[1] as Number)
            } else if (arg is Map<*, *> && arg.keys.map { (it as? String)?.lowercase() }.toSet() == setOf(s.x, s.y)) {
                // case x: 0, y: 1
                Vector(arg[s.x] as Number, arg[s.y] as Number)
            } else if ((arg as? String)?.lowercase() == s.vertical) {
                // case vertical
                Vector.VERTICAL
            } else if ((arg as? String)?.lowercase() == s.horizontal) {
                // case horizontal
                Vector.HORIZONTAL
            } else {
                throw ClassCastException()
            }
        } catch (e: ClassCastException) {
            throw IllegalArgumentException(s.invalidVector.format(arg))
        }
    }

    /**
     * Parses a `Map<String, Any>` into `Node`s and adds those to the `Structure`.
     *
     * @param arg `Map<String, Any>` corresponding to a list of `Node`s. See documentation for specifications.
     * @return Nothing: the nodes are added to the structure.
     * @throws IllegalArgumentException
     * @see Node
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseNodeSection(arg: Map<String, Any>){
        arg.map { (name, vectorMap) ->
            nodes.add(Node(name, parseVector(vectorMap)))
        }
    }

    /**
     * Finds if a node name is a `Node` in the `Structure`, if null safety and correspondent exception.
     *
     * @param name The node name to be found.
     * @return Node
     * @throws IllegalArgumentException thrown if the `Node` is not found.
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.findNotNullNode(name: String): Node = nodes.find { it.name == name }
        ?: throw IllegalArgumentException(s.invalidNodeRef.format(name))

    /**
     * Parses a specific data structure into `Support`s and adds those in their respective `Node`s.
     *
     * @param arg The data structure of the `Support`. See documentation for specifications.
     * @return Nothing: the supports are added to the node.
     * @throws IllegalArgumentException
     * @see parseNodeSection
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseSupportSection(arg: Map<String, Map<String, Any>>) {
        arg.entries.map { (nodeStr, supportMap) ->
            val node = findNotNullNode(nodeStr)

            if (supportMap.keys.map { key -> key.lowercase() }.toSet() != setOf(s.gender, s.direction))
                throw IllegalArgumentException(s.invalidSupportSyntax.format(supportMap))

            node.support = Support(
                node,
                when(supportMap[s.gender]) {
                    1 -> Support.Gender.FIRST
                    2 -> Support.Gender.SECOND
                    3 -> Support.Gender.THIRD
                    else -> throw IllegalArgumentException(s.invalidSupportGender.format(supportMap[s.gender]))
                },
                parseVector(supportMap[s.direction])
                )
        }
    }

    /**
     * Parses a `List<List<String>>` into a list of `Beam`s, where the inner list is a pair of node names where the
     * `Beam`s are connected.
     *
     * @param arg The specific data structure to be parsed as beams. See documentation for specifications.
     * @return Nothing: the beams are added to the nodes.
     * @throws IllegalArgumentException
     * @see parseNodeSection
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseBeamSection(arg: List<List<String>>) {
        arg.map {
            beamLst ->
            if (beamLst.size != 2) throw IllegalArgumentException(s.invalidBeamList.format(beamLst))
            val nodes: List<Node> = beamLst.map { nodeStr -> findNotNullNode(nodeStr) }
            Beam(nodes[0], nodes[1])  // this automatically adds itself to the structure
        }
    }

    /**
     * Parses a specific data structure into `PointLoad`s or `DistributedLoad`s, depending on the number of `Node`s
     * passed.
     *
     * @param arg The specific data structure to be parsed as loads. See documentation for specifications.
     * @return Nothing: the loads are added to the nodes.
     * @throws IllegalArgumentException
     * @see parseNodeSection
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseLoadSection(arg: Map<String, Map<String, Any>>) {
        arg.map { (_, map) ->
            val keys = map.keys.map { key -> key.lowercase() }.toSet()

            val vector = if(s.vector in keys) parseVector(map[s.vector]) else
                if (s.direction in keys && s.module in keys) parseVector(map[s.direction]).normalize() * map[s.module] as Number
                else throw IllegalArgumentException(s.invalidLoadVectorSyntax.format(map))

            val nodeKey = if (s.node in keys) s.node else if (s.nodes in keys) s.node
            else throw IllegalArgumentException(s.invalidLoadNodesSyntax)


            val nodes = map[nodeKey]

            if (nodes is String)
                PointLoad(findNotNullNode(nodes), vector)

            else if (nodes is List<*> && nodes.all { it is String } && nodes.size == 2)
                DistributedLoad(findNotNullNode(nodes[0] as String), findNotNullNode(nodes[1] as String), vector)

            else
                throw IllegalArgumentException(s.invalidLoadSyntax.format(map))
        }
    }

    /**
     * Parses a data structure into a `Structure` and it's properties, using many casts. To convert yaml strings into
     * `Structure`, see `parseYamlString()` when using JVM.
     *
     * @param arg The data structure that will be converted. See documentation for specifications.
     * @return Structure The created `Structure`, with all the `Load`s, `Beam`s, `Support`s and `Node`s.
     * @throws IllegalArgumentException
     * @suppress UNCHECKED_CAST
     *
     * @see parseVector
     * @see parseNodeSection
     * @see parseSupportSection
     * @see parseBeamSection
     * @see parseLoadSection
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalArgumentException::class)
    fun parseSections(arg: Map<String, Any>): Structure {

        val structure = Structure(
            arg[s.structureSection] as? String?: throw IllegalArgumentException(s.invalidSection.format("structure"))
        )

        try { structure.parseNodeSection(arg[s.nodesSection] as Map<String, Any>) }
        catch (e: ClassCastException) { throw IllegalArgumentException(s.invalidSection.format("node")) }

        try { structure.parseSupportSection(arg[s.supportSection] as Map<String, Map<String, Any>>) }
        catch (e: ClassCastException) { throw IllegalArgumentException(s.invalidSection.format("support")) }

        try { structure.parseBeamSection(arg[s.beamsSection] as List<List<String>>)}
        catch (e: ClassCastException) { throw IllegalArgumentException(s.invalidSection.format("beam")) }

        try { structure.parseLoadSection(arg[s.loadsSection] as Map<String, Map<String, Any>>) }
        catch (e: ClassCastException) { throw IllegalArgumentException(s.invalidSection.format("load")) }

        return structure
    }

    private fun String.format(arg: Any?) = this.replace("{}", arg.toString())
}