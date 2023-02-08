package com.kstabilty

import kotlin.jvm.JvmField
import kotlin.math.sqrt

object Consts {
    @JvmField
    val HORIZONTAL = Vector(1, 0)
    @JvmField
    val VERTICAL = Vector(0, 1)
}

enum class SupportGender (val reactions: Int){
    FIRST(1), SECOND(2), THIRD(3),
    ROLLER(1), PINNED(2), FIXED(3)
}

data class Vector(val x: Float, val y: Float) {
    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())
    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())

    operator fun plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vector) = Vector(this.x - other.x, this.y - other.y)

    operator fun times(other: Float) = Vector(x * other, y * other)
    operator fun times(other: Int) = this.times(other.toFloat())
    operator fun times(other: Double) = this.times(other.toFloat())

    operator fun div(other: Float) = Vector(x / other, y / other)
    operator fun div(other: Int) = this.div(other.toFloat())
    operator fun div(other: Double) = this.div(other.toFloat())

    operator fun unaryMinus() = this * -1
    operator fun unaryPlus() = this

    override operator fun equals(other: Any?) = when (other) {
        // todo: check if that's the correct way of comparing types
        is Number -> this.modulus() == other

        is Vector -> this.x == other.x && this.y == other.y

        else -> false
    }

    fun modulus() = sqrt(this*this)  // = sqrt(x**2 + y**2)

    fun normalize() = this/modulus()

    operator fun times(other: Vector) = this.x * other.x + this.y * other.y  // dot product

    fun crossModule(other: Vector) = this.x * other.y - this.y * other.x

    fun getNormal() = Vector(-y, x).normalize()
}


data class Knot(val name: String, val pos: Vector, val structure: Structure? = null) {
    var support: Support? = null  // only one support by knot
    var momentum = 0F
    val bars: MutableList<Bar> = mutableListOf()
    val loads: MutableList<Load> = mutableListOf()
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
    init {
        knot1.bars.add(this)
        knot2.bars.add(this)
    }
}

data class Load(val knot: Knot, val vector: Vector) {
    init { knot.loads.add(this) }

    override operator fun equals(other: Any?): Boolean = when (other) {
        is Load -> this.vector == other.vector && this.knot == other.knot

        is DistributedLoad -> this == other.getEqvLoad()

        else -> false
    }
}

// forces are assumed to be normal to the load length
data class DistributedLoad(val knot1: Knot, val knot2: Knot, val norm: Float) {
    init {
        knot1.distributedLoads.add(this)
        knot2.distributedLoads.add(this)
    }

    override operator fun equals(other: Any?) = this.getEqvLoad() == other

    fun getEqvLoad() = Load(
        Knot(
            knot1.name + knot2.name,
            (knot1.pos + knot2.pos) / 2,  // midpoint
            null
        ),
        (knot2.pos - knot1.pos).getNormal() * (knot2.pos - knot1.pos).modulus() * norm
        // bisector vector times modulus of the entire distributed force
    )
}

data class Structure(val name: String, val knots: MutableList<Knot> = mutableListOf()) {
    fun getSupports() = knots.mapNotNull { it.support }
    fun getBars() = knots.flatMap { it.bars }
    fun getLoads() = knots.flatMap { it.loads }
    fun getDistributedLoads() = knots.flatMap { it.distributedLoads }
    fun getEqvLoads() = getLoads() + getDistributedLoads().map { it.getEqvLoad() }
}