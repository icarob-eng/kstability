package com.moon.kstability.parsing

import com.moon.kstability.*

object ModelParsers {
    /**
     * Parses the many possible syntax for a `Vector`. See parsing documentation for possible syntax.
     *
     * @param arg Some data structure correspondent to a `Vector`.
     * @param lang Some language class object implementing YamlStrings
     * @return `Vector`.
     * @throws IllegalArgumentException
     * @see Vector
     */
    @Throws(IllegalArgumentException::class)
    fun parseVector(arg: Any?, lang: YamlStrings): Vector {
        try {
            return if (arg is List<*> && arg.all { it is Number } && arg.size == 2) {
                // case [0,1]
                Vector(arg[0] as Number, arg[1] as Number)
            } else if (arg is Map<*, *> && arg.keys.map { (it as? String)?.lowercase() }.toSet() == setOf(lang.x, lang.y)) {
                // case x: 0, y: 1
                Vector(arg[lang.x] as Number, arg[lang.y] as Number)
            } else if ((arg as? String)?.lowercase() == lang.vertical) {
                // case vertical
                Vector.VERTICAL
            } else if ((arg as? String)?.lowercase() == lang.horizontal) {
                // case horizontal
                Vector.HORIZONTAL
            } else {
                throw ClassCastException()
            }
        } catch (e: ClassCastException) {
            throw IllegalArgumentException(lang.invalidVector.format(arg))
        }
    }

    /**
     * Parses a `Map<String, Any>` into `Node`s and adds those to the `Structure`.
     *
     * @param arg `Map<String, Any>` corresponding to a list of `Node`s. See documentation for specifications.
     * @param lang Some language class object implementing YamlStrings
     * @return Nothing: the nodes are added to the structure.
     * @throws IllegalArgumentException
     * @see Node
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseNodeSection(arg: Map<String, Any>, lang: YamlStrings){
        arg.map { (name, vectorMap) ->
            nodes.add(Node(name, parseVector(vectorMap, lang)))
        }
    }

    /**
     * Finds if a node name is a `Node` in the `Structure`, if null safety and correspondent exception.
     *
     * @param name The node name to be found.
     * @param lang Some language class object implementing YamlStrings
     * @return Node
     * @throws IllegalArgumentException thrown if the `Node` is not found.
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.findNotNullNode(name: String, lang: YamlStrings): Node = nodes.find { it.name == name }
        ?: throw IllegalArgumentException(lang.invalidNodeRef.format(name))

    /**
     * Parses a specific data structure into `Support`s and adds those in their respective `Node`s.
     *
     * @param arg The data structure of the `Support`. See documentation for specifications.
     * @param lang Some language class object implementing YamlStrings
     * @return Nothing: the supports are added to the node.
     * @throws IllegalArgumentException
     * @see parseNodeSection
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseSupportSection(arg: Map<String, Map<String, Any>>, lang: YamlStrings) {
        arg.entries.map { (nodeStr, supportMap) ->
            val node = findNotNullNode(nodeStr, lang)

            if (supportMap.keys.map { key -> key.lowercase() }.toSet() != setOf(lang.gender, lang.direction))
                throw IllegalArgumentException(lang.invalidSupportSyntax.format(supportMap))

            node.support = Support(
                node,
                when(supportMap[lang.gender]) {
                    1 -> Support.Gender.FIRST
                    2 -> Support.Gender.SECOND
                    3 -> Support.Gender.THIRD
                    else -> throw IllegalArgumentException(lang.invalidSupportGender.format(supportMap[lang.gender]))
                },
                parseVector(supportMap[lang.direction], lang)
                )
        }
    }

    /**
     * Parses a `List<List<String>>` into a list of `Beam`s, where the inner list is a pair of node names where the
     * `Beam`s are connected.
     *
     * @param arg The specific data structure to be parsed as beams. See documentation for specifications.
     * @param lang Some language class object implementing YamlStrings
     * @return Nothing: the beams are added to the nodes.
     * @throws IllegalArgumentException
     * @see parseNodeSection
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseBeamSection(arg: List<List<String>>, lang: YamlStrings) {
        arg.map {
            beamLst ->
            if (beamLst.size != 2) throw IllegalArgumentException(lang.invalidBeamList.format(beamLst))
            val nodes: List<Node> = beamLst.map { nodeStr -> findNotNullNode(nodeStr, lang) }
            Beam(nodes[0], nodes[1])  // this automatically adds itself to the structure
        }
    }

    /**
     * Parses a specific data structure into `PointLoad`s or `DistributedLoad`s, depending on the number of `Node`s
     * passed.
     *
     * @param arg The specific data structure to be parsed as loads. See documentation for specifications.
     * @param lang Some language class object implementing YamlStrings
     * @return Nothing: the loads are added to the nodes.
     * @throws IllegalArgumentException
     * @see parseNodeSection
     * @see parseSections
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.parseLoadSection(arg: Map<String, Map<String, Any>>, lang: YamlStrings) {
        arg.map { (_, map) ->
            val keys = map.keys.map { key -> key.lowercase() }.toSet()

            val vector = if(lang.vector in keys) parseVector(map[lang.vector], lang) else
                if (lang.direction in keys && lang.module in keys) parseVector(map[lang.direction], lang).normalize() * map[lang.module] as Number
                else throw IllegalArgumentException(lang.invalidLoadVectorSyntax.format(map))

            val nodeKey = if (lang.node in keys) lang.node else if (lang.nodes in keys) lang.node
            else throw IllegalArgumentException(lang.invalidLoadNodesSyntax)


            val nodes = map[nodeKey]

            if (nodes is String)
                PointLoad(findNotNullNode(nodes, lang), vector)

            else if (nodes is List<*> && nodes.all { it is String } && nodes.size == 2)
                DistributedLoad(findNotNullNode(nodes[0] as String, lang), findNotNullNode(nodes[1] as String, lang), vector)

            else
                throw IllegalArgumentException(lang.invalidLoadSyntax.format(map))
        }
    }

    /**
     * Parses a data structure into a `Structure` and it's properties, using many casts. To convert yaml strings into
     * `Structure`, see `parseYamlString()` when using JVM.
     *
     * @param arg The data structure that will be converted. See documentation for specifications.
     * @param lang Some language class object implementing YamlStrings
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
    fun parseSections(arg: Map<String, Any>, lang: YamlStrings): Structure {

        val structure = Structure(
            arg[lang.structureSection] as? String?: throw IllegalArgumentException(lang.invalidSection.format("structure"))
        )

        try { structure.parseNodeSection(arg[lang.nodesSection] as Map<String, Any>, lang) }
        catch (e: ClassCastException) { throw IllegalArgumentException(lang.invalidSection.format(lang.nodesSection)) }

        try { structure.parseSupportSection(arg[lang.supportSection] as Map<String, Map<String, Any>>, lang) }
        catch (e: ClassCastException) { throw IllegalArgumentException(lang.invalidSection.format(lang.supportSection)) }

        try { structure.parseBeamSection(arg[lang.beamsSection] as List<List<String>>, lang)}
        catch (e: ClassCastException) { throw IllegalArgumentException(lang.invalidSection.format(lang.beamsSection)) }

        try { structure.parseLoadSection(arg[lang.loadsSection] as Map<String, Map<String, Any>>, lang) }
        catch (e: ClassCastException) { throw IllegalArgumentException(lang.invalidSection.format(lang.loadsSection)) }

        return structure
    }

    private fun String.format(arg: Any?) = this.replace("{}", arg.toString())
}