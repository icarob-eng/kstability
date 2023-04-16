package com.kstabilty

import kotlin.jvm.JvmField
import kotlin.math.sqrt


object Consts {
    @JvmField
    val HORIZONTAL = Vector(1, 0)
    @JvmField
    val VERTICAL = Vector(0, 1)
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

    fun getOrhtogonal() = Vector(-y, x).normalize()

    fun rotate(i: Float): Vector {
        return if (i == 0F) this
        else if (i.isFinite()) {
            // sin and cos arctg formulae from wolfram alpha
            val cosarctg = 1 / sqrt(i*i + 1)
            val sinarctg = i / sqrt(i*i + 1)
            Vector(
                this.x * cosarctg - this.y * sinarctg,
                this.x * sinarctg + this.y * cosarctg
            )
        } else if (i == Float.POSITIVE_INFINITY) this.getOrhtogonal() else - this.getOrhtogonal()
    }
}