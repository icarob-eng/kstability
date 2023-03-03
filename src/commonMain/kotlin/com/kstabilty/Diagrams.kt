package com.kstabilty

import kotlin.math.sqrt

/**
 * Represents a quadratic funtion defining the `invoke` operator as f(x).
 *
 * Automatically calculates the expresison's delta, roots and vertex.
 */
data class Quadratic(val a: Double, val b: Double, val c: Double){
    val delta = b * b - 4 * a * c
    val roots = Pair(
        (- b + sqrt(delta))/ (2 * a),
        (- b - sqrt(delta))/ (2 * a)
    )
    val vertex = Vector(-b / (2 * a), delta / (4 * a))

    operator fun invoke(x: Int) = a * x * x + b * x + c
    operator fun invoke(x: Float) = a * x * x + b * x + c
    operator fun invoke(x: Double) = a * x * x + b * x + c
}

/**
 * Defines a section along one bar, where the knot marks the **start** of the section, and the bar, the direction.
 *
 * @see Diagrams.getSections
 */
data class Section (val bar: Bar, val knotInput: Knot) {
    val knot = bar.makeTangentKnot(knotInput)
}

object Diagrams {
    // todo: utilizar interface de plotagem

    /**
     * Derives a list of sections from a strucure. Each section is defined by a knot, where it begins.
     * The knots are devided by a line perpedicular to the given bar.
     *
     * **By now this function considers all loads in the structure, not just loads in the bar**
     *
     * @see Section
     * @param structure The structure that will be devided.
     * @param bar The direction that the sections will be devided.
     *
     * @return List of sections.
     */
    fun getSections(structure: Structure, bar: Bar): List<Section> {
        return structure.knots.map { Section(bar, it) }
    }

    // todo: make recursive
    /**
     * Derives the quadratic expression for the bending moment, for a section.
     *
     * @param allSections A list with all the strucutre's sections, where the sections to the left will be actually
     * used.
     * @param sectionId Specify the section from which will be calculated the quadratic.
     *
     * @return The generated quadratic.
     *
     * @see getSections
     * @see Quadratic
     */
    fun generateMomentQuadratic(allSections: List<Section>, sectionId: Int): Quadratic = TODO()

    /**
     * Derives the quadratic expression for the shear force, for a section.
     *
     * @param allSections A list with all the strucutre's sections, where the sections to the left will be actually
     * used.
     * @param sectionId Specify the section from which will be calculated the quadratic.
     *
     * @return The generated quadratic.
     *
     * @see getSections
     * @see Quadratic
     */
    fun generateShearQuadratic(allSections: List<Section>, sectionId: Int): Quadratic = TODO()

    /**
     * Generates a chart's list of plot point's x values.
     *
     * **x values may repeat depending on the end of a section.**
     *
     * @param sections List of structure's sections.
     * @param step The maximum distance between points.
     *
     * @return A list of x values, as Doubles.
     *
     * @see getYAxis
     * @see getSections
     * @see Section
     */
    fun getXAxis(sections: List<Section>, step: Float): List<Double> = TODO()

    /**
     * Calculates y values from x values and the quadratic funtions and put the results in a list of Doubles.
     *
     * @param sections List of structure's sections.
     * @param xAxis List of x values from which the y values will be calculated.
     * @param quadratics The quadratic functions that will determine the y values.
     *
     * @return A list of y values, as Doubles.
     *
     * @see getXAxis
     * @see Section
     * @see generateMomentQuadratic
     * @see generateShearQuadratic
     * @see Quadratic
     */
    fun getYAxis(sections: List<Section>, xAxis: List<Double>, quadratics: List<Quadratic>): List<Double> = TODO()

    fun getShearForceDiagram(structure: Structure, bar: Bar, step: Float): Pair<List<Double>, List<Double>> {
        val sections = getSections(structure, bar)

        val x = getXAxis(sections, step)

        val quadratics = sections.indices.map { generateShearQuadratic(sections, it) }

        val y = getYAxis(sections, x, quadratics)

        return Pair(x, y)
    }

    fun getBendingMomentDiagram(structure: Structure, bar: Bar, step: Float): Pair<List<Double>, List<Double>> {
        val sections = getSections(structure, bar)

        val x = getXAxis(sections, step)

        val quadratics = sections.indices.map { generateMomentQuadratic(sections, it) }

        val y = getYAxis(sections, x, quadratics)

        return Pair(x, y)
    }
}