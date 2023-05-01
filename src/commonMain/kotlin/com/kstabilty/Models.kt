package com.kstabilty


/**
 * The knot class holds all load, support and bar information. It also could have a reference to the hole structure.
 *
 * @property name An (preferably) unique point name.
 * @property pos The knot's position.
 * @property structure A circular reference to the hole structure. Nullable.
 * @property momentum The resultant bending moment applied at the knot, as a Float.
 * @property bars List of bars that start or end at the knot. The bars also have a circular reference to the knot.
 * @property pointLoads List of point loads applied at the knot. The loads also have a circular reference to the knot.
 * @property distributedLoads List of distributed loads that start or end at the knot.
 * The loads also have a circular reference to the knot.
 *
 * @see Vector
 * @see PointLoad
 * @see DistributedLoad
 * @see Support
 * @see Bar
 * @see Structure
 */
data class Knot(val name: String, val pos: Vector, val structure: Structure? = null) {
    // todo: garantir que os nomes serão únicos
    var support: Support? = null  // only one support by knot
    var momentum = 0F  // todo: checar a quê corresponde o sinal
    val bars: MutableList<Bar> = mutableListOf()
    val pointLoads: MutableList<PointLoad> = mutableListOf()
    val distributedLoads: MutableList<DistributedLoad> = mutableListOf()

    init {
        structure?.knots?.add(this)  // null for temporary knots
    }

    override operator fun equals(other: Any?) = if (other is Knot) this.pos == other.pos else false
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + pos.hashCode()
        result = 31 * result + (structure?.hashCode() ?: 0)
        result = 31 * result + (support?.hashCode() ?: 0)
        result = 31 * result + momentum.hashCode()
        result = 31 * result + bars.hashCode()
        result = 31 * result + pointLoads.hashCode()
        result = 31 * result + distributedLoads.hashCode()
        return result
    }
}

/**
 * A support object represents a place where the system will apply a reaction load, been it a point load or a bending
 * moment. The support also specifies restrictions on the point load direction.
 *
 * @property knot A circular reference to the knot. When instantiated, it adds itself to the knot.
 * @property gender A value from the inner enum class `Gender` Values could be:
 *  - `FIRST`: representing a roller support or a simple contact, with no friction.
 *  Requires to specify the restricted direction, that the reaction force will be parallel to;
 *  - `SECOND`: representing a pinned support or a simple contact, with infinite friction.
 *  Has no need to specify a restricted direction, cause all directions are restricted;
 *  - `THIRD`: representing a fixed support, where all movements are restricted;
 * @property dir Vector used to specify reaction force direction restrictions, when needed. Also **required** for plotting
 * purposes.
 *
 * @see Knot
 * @see PointLoad
 */
data class Support(val knot: Knot, val gender: Gender, private val dir: Vector) {
    val direction: Vector = dir.normalize()  // unit vector

    init { knot.support = this }

    enum class Gender (val reactions: Int){
        FIRST(1), // roller
        SECOND(2),  // pinned
        THIRD(3), // fixed
    }
}

/**
 * Represents a physical connexion between 2 knots. In this library, we mainly use it for charts and plots,
 * at the moment.
 *
 * _This API will change in the future._
 *
 * @property knot1 Represents the position where the bar starts. When instantiated, it adds itself to the knot.
 * @property knot2 Represents the position where the bar ends. When instantiated, it adds itself to the knot.
 * @property barVector A vector from [knot1] to [knot2].
 *
 * @see Knot
 * @see Vector
 * @see Structure
 */
data class Bar(val knot1: Knot, val knot2: Knot) {
    val barVector = knot2.pos - knot1.pos

    init {
        knot1.bars.add(this)
        knot2.bars.add(this)
    }

    fun makeTangentKnot(knot: Knot) = Knot(
        knot.name + "'",
        (barVector.normalize() / barVector.length()) * (-knot1.pos * (knot.pos + knot2.pos))
    )

    // C is aligned with AB if |AC| + |CB| == |AB|
    fun isAligned(vector: Vector) =
        ((vector - knot1.pos).length() + (knot2.pos - vector).length()) == (barVector).length()
}

/**
 * Represents a force applied at the given knot.
 *
 * @property knot Where the force is applied. When instantiated, it adds itself to the knot.
 * @property vector Represents the actual force vector.
 *
 * @see Knot
 * @see Vector
 */
data class PointLoad(val knot: Knot, val vector: Vector) {
    init { knot.pointLoads.add(this) }

    override operator fun equals(other: Any?): Boolean = when (other) {
        is PointLoad -> this.vector == other.vector && this.knot == other.knot

        is DistributedLoad -> this == other.getEqvLoad()

        else -> false
    }

    override fun hashCode(): Int {
        var result = knot.pos.hashCode()
        result = 31 * result + vector.hashCode()
        return result
    }
}

/**
 * Represents a force applied to a line segment.
 *
 * @property knot1 Represents the position where the load starts. starts. When instantiated, it adds itself to the knot.
 * @property knot2 Represents the position where the load ends. When instantiated, it adds itself to the knot.
 * @property vector Represents the force vector that is distributed through the line segment.
 *
 * @see Knot
 * @see Vector
 *
 * @see getEqvLoad
 */
data class DistributedLoad(val knot1: Knot, val knot2: Knot, val vector: Vector) {
    init {
        knot1.distributedLoads.add(this)
        knot2.distributedLoads.add(this)
    }

    override operator fun equals(other: Any?) = this.getEqvLoad() == other

    /**
     * Returns the integral of the force vector through the line segment, i.e. distributes the load vector
     * through all the bar's length.
     *
     * @return A point load applied between the two knots.
     */
    fun getEqvLoad() = PointLoad(
        Knot(
            knot1.name + knot2.name,
            (knot1.pos + knot2.pos) / 2,  // midpoint
            null
        ),
        vector * (knot2.pos - knot1.pos).length()
    )

    override fun hashCode(): Int {
        var result = knot1.pos.hashCode()
        result = 31 * result + knot2.pos.hashCode()
        result = 31 * result + vector.hashCode()
        return result
    }
}

/**
 * This is the main class in this library, used to aggregate all other instances.
 * It only _actually_ holds all the knots.
 *
 * Has many methods to sum up all the data, for example, [getEqvLoads] it's the resultant force in the project.
 * And also has [getRotatedCopy], a method for rotate all of it.
 *
 * @property name It's just a name, use at your will.
 * @property knots A list of all knots, that actually holds the data.
 */
data class Structure(val name: String, val knots: MutableList<Knot> = mutableListOf()) {
    fun getSupports() = knots.mapNotNull { it.support }
    fun getBars() = knots.flatMap { it.bars }
    fun getMomentumLoads() = knots.sumOf { it.momentum.toDouble() }.toFloat()
    fun getPointLoads() = knots.flatMap { it.pointLoads }
    fun getDistributedLoads() = knots.flatMap { it.distributedLoads }
    fun getEqvLoads() = getPointLoads() + getDistributedLoads().map { it.getEqvLoad() }

    fun getRotatedCopy(i: Float): Structure {
        // não seria possível rotacionar apenas os nós e direções, pois as direções são valores imutáveis
        val newStructure = Structure(this.name + "'")
        this.knots.forEach {
            val knot = Knot(it.name, it.pos.getRotated(i), newStructure)
            knot.momentum = it.momentum
        }
        // passa todos os nós e depois altera procurando um a um

        this.getSupports().forEach {
            Support(
                newStructure.knots.first { knot -> knot.name == it.knot.name },
                it.gender,
                it.direction.getRotated(i)
            )
        }

        this.getBars().forEach {
            Bar(
                newStructure.knots.first { knot -> knot.name == it.knot1.name },
                newStructure.knots.first { knot -> knot.name == it.knot2.name }
            )
        }

        this.getPointLoads().forEach {
            PointLoad(
                newStructure.knots.first { knot -> knot.name == it.knot.name },
                it.vector.getRotated(i)
            )
        }

        this.getDistributedLoads().forEach {
            DistributedLoad(
                newStructure.knots.first { knot -> knot.name == it.knot1.name },
                newStructure.knots.first { knot -> knot.name == it.knot2.name },
                it.vector
                )
        }

        return newStructure
    }
}