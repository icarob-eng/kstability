package com.kstabilty

import kotlin.math.sqrt

/**
 * Represents a polynomial function defining the `invoke` operator as f(x). Calculates vertex and roots for a != 0.
 *
 * Automatically calculates the expression's delta, roots and vertex.
 */
data class Polynomial(val a: Double, val b: Double, val c: Double){
    constructor(a: Float, b: Float, c: Float) : this (a.toDouble(), b.toDouble(), c.toDouble())
    private val delta = b * b - 4 * a * c
    val roots = if (a != 0.toDouble()) Pair(
        (- b + sqrt(delta))/ (2 * a),
        (- b - sqrt(delta))/ (2 * a)
    ) else null
    val vertex = if (a != 0.toDouble()) Vector(-b / (2 * a), delta / (4 * a)) else null

    operator fun invoke(x: Int) = a * x * x + b * x + c
    operator fun invoke(x: Float) = a * x * x + b * x + c
    operator fun invoke(x: Double) = a * x * x + b * x + c

    operator fun plus(other: Polynomial) = Polynomial(this.a + other.a, this.b + other.b, this.c + other.c)

    operator fun minus(other: Polynomial) = Polynomial(this.a - other.a, this.b - other.b, this.c - other.c)

    override fun toString(): String {
        val expression = StringBuilder()
        if (a != 0.0) {
            if (a>0.0) expression.append("+")
            expression.append(a.toString())
            expression.append("x²")
        }
        if (b != 0.0) {
            if (b>0.0) expression.append("+")
            expression.append(b.toString())
            expression.append("x")
        }
        if (c != 0.0) {
            if (c>0.0) expression.append("+")
            expression.append(c.toString())
        }
        return expression.toString()
    }
}

object PointLoadPolynomials {
    fun normalStress(f: Vector): Polynomial = Polynomial(a=0F, b=0F, c=f.x)

    fun shearStress(f: Vector): Polynomial = Polynomial(a=0F, b=0F, c=f.y)

    fun bendingMoment(a: Vector, f: Vector): Polynomial = Polynomial(a=0F, b=f.y, c=-f.y*a.x)
}

object DistributedLoadPolynomials {
    fun normalStress(a: Vector, f: Vector): Polynomial = Polynomial(a=0F, b=f.x, c=-f.x*a.x)

    fun normalStressEnd(a: Vector, f: Vector, end: Vector) = shearStress(a, f) + bendingMoment(end, -f)

    fun shearStress(a: Vector, f: Vector): Polynomial = Polynomial(a=0F, b=f.y, c=-f.y*a.x)

    fun shearStressEnd(a: Vector, f: Vector, end: Vector) = shearStress(a, f) + bendingMoment(end, -f)

    fun bendingMoment(a: Vector, f: Vector): Polynomial = Polynomial(a=f.y/2, b=-f.y*a.x, c=(f.y*a.x*a.x)/2)

    fun bendingMomentEnd(a: Vector, f: Vector, end: Vector) = bendingMoment(a, f) + bendingMoment(end, -f)
}

object MomentumLoadPolynomials{
    fun normalStress() = Polynomial(0F,0F,0F)
    fun shearStress() = Polynomial(0F,0F,0F)
    fun bendingMoment(m: Double) = Polynomial(a=0.0,b=0.0,c=m)
}