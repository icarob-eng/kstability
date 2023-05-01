package com.kstabilty

import kotlin.jvm.JvmField
import kotlin.math.sqrt


/**
 * All relevant methods are documented in `memoria_de_calculo.md`. The class also have many overloads to convert
 * Ints and Doubles to Floats.
 */
data class Vector(val x: Float, val y: Float) {
    object Consts {
        @JvmField
        val HORIZONTAL = Vector(1, 0)
        @JvmField
        val VERTICAL = Vector(0, 1)
    }

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
        is Number -> this.length() == other

        is Vector -> this.x == other.x && this.y == other.y

        else -> false
    }

    fun length() = sqrt(this*this)  // = sqrt(x**2 + y**2)

    fun normalize() = this/length()

    operator fun times(other: Vector) = this.x * other.x + this.y * other.y  // dot product

    fun crossModule(other: Vector) = this.x * other.y - this.y * other.x

    fun getOrthogonal() = Vector(-y, x).normalize()

    fun getRotated(i: Float): Vector {
        return if (i == 0F) this
        else if (i.isFinite()) {
            // sin and cos arctg formulae from wolfram alpha
            val cosarctg = 1 / sqrt(i*i + 1)
            val sinarctg = i / sqrt(i*i + 1)
            Vector(
                this.x * cosarctg - this.y * sinarctg,
                this.x * sinarctg + this.y * cosarctg
            )
        } else if (i == Float.POSITIVE_INFINITY) this.getOrthogonal() * this.length()
        else -this.getOrthogonal() * this.length()
    }

    fun inclination() = if (this.x != 0F) (this.y)/(this.x) else
        if (this.y >= 0) Float.POSITIVE_INFINITY else Float.NEGATIVE_INFINITY

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}