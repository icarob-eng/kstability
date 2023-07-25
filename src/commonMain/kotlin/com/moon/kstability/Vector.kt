package com.moon.kstability

import kotlin.math.sqrt


/**
 * All relevant methods are documented in `memoria_de_calculo.md`. The class also have many overloads to convert
 * Ints and Doubles to Floats.
 */
data class Vector(val x: Float, val y: Float, val name:String="") {
    companion object Consts {
        val HORIZONTAL = Vector(1, 0)
        val VERTICAL = Vector(0, 1)
    }

    constructor(x: Number, y: Number,name: String="") : this(x.toFloat(), y.toFloat(), name)

    constructor(array: Array<Number>, name:String="") : this(array[0].toFloat(),array[1].toFloat(), name)

    constructor(array: ArrayList<Number>, name:String="") : this(array[0].toFloat(),array[1].toFloat(), name)

    constructor(direction: String, name:String=""):this(
        when (direction) {
            "vertical" -> 0f
            "horizontal" -> 1f
            else -> throw IllegalArgumentException("Invalid direction")
        },
        when (direction) {
            "vertical" -> 1f
            "horizontal" -> 0f
            else -> throw IllegalArgumentException("Invalid direction")
        },
        name
    )

    operator fun plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vector) = Vector(this.x - other.x, this.y - other.y)

    operator fun times(other: Number) = Vector(x * other.toFloat(), y * other.toFloat())

    operator fun div(other: Number) = Vector(x / other.toFloat(), y / other.toFloat())

    operator fun unaryMinus() = this * -1f

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

    fun orthogonal() = Vector(-y, x).normalize()

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
        } else if (i == Float.POSITIVE_INFINITY) this.orthogonal() * this.length()
        else -this.orthogonal() * this.length()
    }

    fun inclination() = if (this.x != 0F) (this.y)/(this.x) else
        if (this.y >= 0) Float.POSITIVE_INFINITY else Float.NEGATIVE_INFINITY

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "V($x, $y)"
    }
}