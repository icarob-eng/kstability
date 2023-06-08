package com.moon.kstability

import kotlin.math.sqrt

/**
 * Represents a polynomial function, defining the [invoke] operator as f(x).
 *
 * Automatically calculates the expression's [delta], [roots] and [vertex], for `a != 0`.
 */
data class Polynomial(val a: Float, val b: Float, val c: Float){
    private val delta = b * b - 4 * a * c
    val roots = if (a != 0F) Pair(
        (- b + sqrt(delta))/ (2 * a),
        (- b - sqrt(delta))/ (2 * a)
    ) else null
    val vertex = if (a != 0F) Vector(-b / (2 * a), delta / (4 * a)) else null

    operator fun invoke(x: Int) = a * x * x + b * x + c
    operator fun invoke(x: Float) = a * x * x + b * x + c
    operator fun invoke(x: Double) = a * x * x + b * x + c

    operator fun plus(other: Polynomial) = Polynomial(this.a + other.a, this.b + other.b, this.c + other.c)

    operator fun minus(other: Polynomial) = Polynomial(this.a - other.a, this.b - other.b, this.c - other.c)

    override fun toString(): String {
        val expression = StringBuilder()
        if (a != 0F) {
            if (a>0F) expression.append("+")
            expression.append(a.toString())
            expression.append("x²")
        }
        if (b != 0F) {
            if (b>0F) expression.append("+")
            expression.append(b.toString())
            expression.append("x")
        }
        if (c != 0F) {
            if (c>0F) expression.append("+")
            expression.append(c.toString())
        }
        return expression.toString()
    }
    object PointLoad {
        /**
         * For all Macaulay's functions in this module, `a` stands for "application point",
         * `f` for applied force, `end` for end of distributed load and `m` for bending moment load
         */
        fun normalStress(f: Vector): Polynomial = Polynomial(a=0F, b=0F, c=f.x)

        fun shearStress(f: Vector): Polynomial = Polynomial(a=0F, b=0F, c=f.y)

        fun bendingMoment(a: Vector, f: Vector): Polynomial = Polynomial(a=0F, b=f.y, c=-f.y*a.x)
    }

    object DistributedLoad {
        fun normalStress(a: Vector, f: Vector): Polynomial = Polynomial(a=0F, b=f.x, c=-f.x*a.x)

        fun normalStressEnd(a: Vector, f: Vector, end: Vector) = normalStress(a, f) + normalStress(end, -f)

        fun shearStress(a: Vector, f: Vector): Polynomial = Polynomial(a=0F, b=f.y, c=-f.y*a.x)

        fun shearStressEnd(a: Vector, f: Vector, end: Vector) = shearStress(a, f) + shearStress(end, -f)

        fun bendingMoment(a: Vector, f: Vector): Polynomial = Polynomial(a=f.y/2, b=-f.y*a.x, c=(f.y*a.x*a.x)/2)

        fun bendingMomentEnd(a: Vector, f: Vector, end: Vector) = bendingMoment(a, f) + bendingMoment(end, -f)
    }

    object MomentumLoad{
        fun normalStress() = Polynomial(0F,0F,0F)
        fun shearStress() = Polynomial(0F,0F,0F)
        fun bendingMoment(m: Float) = Polynomial(a=0F,b=0F,c=m)
    }
}

