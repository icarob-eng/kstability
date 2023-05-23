package com.kstabilty


/**
 * The node class holds all load, support and r information. It also could have a reference to the hole structure.
 *
 * @property name An (preferably) unique point name.
 * @property pos The node's position.
 * @property structure A circular reference to the hole structure. Nullable.
 * @property momentum The resultant bending moment applied at the node, as a Float.
 * @property beams List of beams that start or end at the node. The beams also have a circular reference to the node.
 * @property pointLoads List of point loads applied at the node. The loads also have a circular reference to the node.
 * @property distributedLoads List of distributed loads that start or end at the node.
 * The loads also have a circular reference to the node.
 *
 * @see Vector
 * @see PointLoad
 * @see DistributedLoad
 * @see Support
 * @see Beam
 * @see Structure
 */
data class Node(var name: String, val pos: Vector, val structure: Structure? = null) {
    // todo: garantir que os nomes serão únicos
    var support: Support? = null  // only one support by node
    var momentum = 0F  // todo: checar a quê corresponde o sinal
    val beams: MutableList<Beam> = mutableListOf()
    val pointLoads: MutableList<PointLoad> = mutableListOf()
    val distributedLoads: MutableList<DistributedLoad> = mutableListOf()

    init {
        structure?.nodes?.add(this)  // null for temporary nodes
    }

    override operator fun equals(other: Any?) = if (other is Node) this.pos == other.pos else false
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + pos.hashCode()
        result = 31 * result + (structure?.hashCode() ?: 0)
        result = 31 * result + (support?.hashCode() ?: 0)
        result = 31 * result + momentum.hashCode()
        result = 31 * result + beams.hashCode()
        result = 31 * result + pointLoads.hashCode()
        result = 31 * result + distributedLoads.hashCode()
        return result
    }
}

/**
 * A support object represents a place where the system will apply a reaction load, been it a point load or a bending
 * moment. The support also specifies restrictions on the point load direction.
 *
 * @property node A circular reference to the node. When instantiated, it adds itself to the node.
 * @property gender A value from the inner enum class `Gender` Values could be:
 *  - `FIRST`: representing a roller support or a simple contact, with no friction.
 *  Requires to specify the restricted direction, that the reaction force will be parallel to;
 *  - `SECOND`: representing a pinned support or a simple contact, with infinite friction.
 *  Has no need to specify a restricted direction, cause all directions are restricted;
 *  - `THIRD`: representing a fixed support, where all movements are restricted;
 * @property dir Vector used to specify reaction force direction restrictions, when needed. Also **required** for plotting
 * purposes.
 *
 * @see Node
 * @see PointLoad
 */
data class Support(val node: Node, val gender: Gender, private val dir: Vector) {
    val direction: Vector = dir.normalize()  // unit vector

    init { node.support = this }

    enum class Gender (val reactions: Int){
        FIRST(1), // roller
        SECOND(2),  // pinned
        THIRD(3), // fixed
    }
}

/**
 * Represents a physical connexion between 2 nodes. In this library, we mainly use it for charts and plots,
 * at the moment.
 *
 * _This API will change in the future._
 *
 * @property node1 Represents the position where the beam starts. When instantiated, it adds itself to the node.
 * @property node2 Represents the position where the beam ends. When instantiated, it adds itself to the node.
 * @property beamVector A vector from [node1] to [node2].
 *
 * @see Node
 * @see Vector
 * @see Structure
 */
data class Beam(val node1: Node, val node2: Node) {
    val beamVector = node2.pos - node1.pos

    init {
        node1.beams.add(this)
        node2.beams.add(this)
    }

    // proj_a b =  (a * b) * a / |a|² = (a * b) * â / |a|
    fun makeTangentNode(node: Node) = Node(
        node.name + "'",
        (beamVector.normalize() / beamVector.length()) * ((node.pos - node1.pos) * beamVector)
    )

    // C is aligned with AB if |AC| + |CB| == |AB|
    fun isAligned(vector: Vector) =
        ((vector - node1.pos).length() + (node2.pos - vector).length()) == (beamVector).length()
}

/**
 * Represents a force applied at the given node.
 *
 * @property node Where the force is applied. When instantiated, it adds itself to the node.
 * @property vector Represents the actual force vector.
 *
 * @see Node
 * @see Vector
 */
data class PointLoad(val node: Node, val vector: Vector) {
    init { node.pointLoads.add(this) }

    override operator fun equals(other: Any?): Boolean = when (other) {
        is PointLoad -> this.vector == other.vector && this.node == other.node

        is DistributedLoad -> this == other.getEqvLoad()

        else -> false
    }

    override fun hashCode(): Int {
        var result = node.pos.hashCode()
        result = 31 * result + vector.hashCode()
        return result
    }
}

/**
 * Represents a force applied to a line segment.
 *
 * @property node1 Represents the position where the load starts. starts. When instantiated, it adds itself to the node.
 * @property node2 Represents the position where the load ends. When instantiated, it adds itself to the node.
 * @property vector Represents the force vector that is distributed through the line segment.
 *
 * @see Node
 * @see Vector
 *
 * @see getEqvLoad
 */
data class DistributedLoad(val node1: Node, val node2: Node, val vector: Vector) {
    init {
        node1.distributedLoads.add(this)
        node2.distributedLoads.add(this)
    }

    override operator fun equals(other: Any?) = this.getEqvLoad() == other

    /**
     * Returns the integral of the force vector through the line segment, i.e. distributes the load vector
     * through all the beam's length.
     *
     * @return A point load applied between the two nodes.
     */
    fun getEqvLoad() = PointLoad(
        Node(
            node1.name + node2.name,
            (node1.pos + node2.pos) / 2,  // midpoint
            null
        ),
        vector * (node2.pos - node1.pos).length()
    )

    override fun hashCode(): Int {
        var result = node1.pos.hashCode()
        result = 31 * result + node2.pos.hashCode()
        result = 31 * result + vector.hashCode()
        return result
    }
}

/**
 * This is the main class in this library, used to aggregate all other instances.
 * It only _actually_ holds all the nodes.
 *
 * Has many methods to sum up all the data, for example, [getEqvLoads] it's the resultant force in the project.
 * And also has [getRotatedCopy], a method for rotate all of it.
 *
 * @property name It's just a name, use at your will.
 * @property nodes A list of all nodes, that actually holds the data.
 */
data class Structure(val name: String, val nodes: MutableList<Node> = mutableListOf()) {
    fun getSupports() = nodes.mapNotNull { it.support }
    fun getBeam() = nodes.flatMap { it.beams }
    fun getMomentumLoads() = nodes.sumOf { it.momentum.toDouble() }.toFloat()
    fun getPointLoads() = nodes.flatMap { it.pointLoads }
    fun getDistributedLoads() = nodes.flatMap { it.distributedLoads }
    fun getEqvLoads() = getPointLoads() + getDistributedLoads().map { it.getEqvLoad() }

    fun getRotatedCopy(i: Float): Structure {
        // não seria possível rotacionar apenas os nós e direções, pois as direções são valores imutáveis
        val newStructure = Structure(this.name + "'")
        this.nodes.forEach {
            val node = Node(it.name, it.pos.getRotated(i), newStructure)
            node.momentum = it.momentum
        }
        // passa todos os nós e depois altera procurando um a um

        this.getSupports().forEach {
            Support(
                newStructure.nodes.first { node -> node.name == it.node.name },
                it.gender,
                it.direction.getRotated(i)
            )
        }

        this.getBeam().forEach {
            Beam(
                newStructure.nodes.first { node -> node.name == it.node1.name },
                newStructure.nodes.first { node -> node.name == it.node2.name }
            )
        }

        this.getPointLoads().forEach {
            PointLoad(
                newStructure.nodes.first { node -> node.name == it.node.name },
                it.vector.getRotated(i)
            )
        }

        this.getDistributedLoads().forEach {
            DistributedLoad(
                newStructure.nodes.first { node -> node.name == it.node1.name },
                newStructure.nodes.first { node -> node.name == it.node2.name },
                it.vector
                )
        }

        return newStructure
    }
}