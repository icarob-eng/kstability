package com.kstabilty


enum class SupportGender (val reactions: Int){
    FIRST(1), // roller
    SECOND(2),  // pinned
    THIRD(3), // fixed
}

data class Knot(val name: String, val pos: Vector, val structure: Structure? = null) {
    // todo: garantir que os nomes serão únicos
    var support: Support? = null  // only one support by knot
    var momentum = 0F
    val bars: MutableList<Bar> = mutableListOf()
    val pointLoads: MutableList<PointLoad> = mutableListOf()
    val distributedLoads: MutableList<DistributedLoad> = mutableListOf()

    init {
        structure?.knots?.add(this)  // null for temporary knots
    }

    override operator fun equals(other: Any?) = if (other is Knot) this.pos == other.pos else false
}

data class Support(val knot: Knot, val gender: SupportGender, val dir: Vector) {
    val direction: Vector = dir.normalize()  // unit vector

    init { knot.support = this }
}

data class Bar(val knot1: Knot, val knot2: Knot) {
    val inclination = if (knot1.pos.x != knot2.pos.x) (knot2.pos.y - knot1.pos.y)/(knot2.pos.x - knot1.pos.x)
    else if (knot2.pos.y >= knot1.pos.y) Float.POSITIVE_INFINITY else Float.NEGATIVE_INFINITY

    init {
        knot1.bars.add(this)
        knot2.bars.add(this)
    }

    fun makeTangentKnot(knot: Knot): Knot = TODO()
}

data class PointLoad(val knot: Knot, val vector: Vector) {
    init { knot.pointLoads.add(this) }

    override operator fun equals(other: Any?): Boolean = when (other) {
        is PointLoad -> this.vector == other.vector && this.knot == other.knot

        is DistributedLoad -> this == other.getEqvLoad()

        else -> false
    }
}

// forces are assumed to be normal to the load length
data class DistributedLoad(val knot1: Knot, val knot2: Knot, val norm: Float) {  // todo: substituir norma por vetor de intensidade
    init {
        knot1.distributedLoads.add(this)
        knot2.distributedLoads.add(this)
    }

    override operator fun equals(other: Any?) = this.getEqvLoad() == other

    fun getEqvLoad() = PointLoad(
        Knot(
            knot1.name + knot2.name,
            (knot1.pos + knot2.pos) / 2,  // midpoint
            null
        ),
        (knot2.pos - knot1.pos).getOrthogonal() * (knot2.pos - knot1.pos).modulus() * norm
        // bisector vector times modulus of the entire distributed force
    )
}

data class Structure(val name: String, val knots: MutableList<Knot> = mutableListOf()) {
    fun getSupports() = knots.mapNotNull { it.support }
    fun getBars() = knots.flatMap { it.bars }
    fun getMomentumLoads() = knots.sumOf { it.momentum.toDouble() }.toFloat()
    fun getPointLoads() = knots.flatMap { it.pointLoads }
    fun getDistributedLoads() = knots.flatMap { it.distributedLoads }
    fun getEqvLoads() = getPointLoads() + getDistributedLoads().map { it.getEqvLoad() }

    fun getRotatedCopy(i: Float): Structure {
        // todo: testar se rotacionar os nós e cargas pontuais já resolveria

        val newStructure = Structure(this.name + "'", mutableListOf())
        this.knots.forEach {
            val knot = Knot(it.name, it.pos.rotate(i), newStructure)
            knot.momentum = it.momentum
        }
        // passa todos os nós e depois altera procurando um a um

        this.getSupports().forEach {
            Support(
                newStructure.knots.first { knot -> knot.name == it.knot.name },
                it.gender,
                it.dir.rotate(i)
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
                it.vector.rotate(i)
            )
        }

        this.getDistributedLoads().forEach {
            DistributedLoad(
                newStructure.knots.first { knot -> knot.name == it.knot1.name },
                newStructure.knots.first { knot -> knot.name == it.knot2.name },
                it.norm
                )
        }

        return newStructure
    }
}