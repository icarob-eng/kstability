package com.kstabilty


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

    // todo: rotate structure

    // todo: make recursive
    /**
     * Derives the polynomial expression for the bending moment, for a section.
     *
     * @param allSections A list with all the strucutre's sections, where the sections to the left will be actually
     * used.
     * @param sectionId Specify the section from which will be calculated the polynomial.
     *
     * @return The generated polynomial.
     *
     * @see getSections
     * @see Polynomial
     */
    fun generateMomentFunction(allSections: List<Section>, sectionId: Int): Polynomial = TODO()

    /**
     * Derives the polynomial expression for the shear force, for a section.
     *
     * @param allSections A list with all the strucutre's sections, where the sections to the left will be actually
     * used.
     * @param sectionId Specify the section from which will be calculated the polynomial.
     *
     * @return The generated polynomial.
     *
     * @see getSections
     * @see Polynomial
     */
    fun generateShearFunction(allSections: List<Section>, sectionId: Int): Polynomial = TODO()

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
    fun getXAxis(sections: List<Section>, resolution: Float): List<Float> {
        val referenceBar = sections.first().bar
        val axis = mutableListOf<Float>()
        var x = referenceBar.knot1.pos.x
        while (x < referenceBar.knot2.pos.x) {
            axis.add(x)
            x += resolution
        }
        return axis
    }


    /**
     * Calculates y values from x values and the quadratic functions and put the results in a list of Doubles.
     *
     * @param sections List of structure's sections.
     * @param xAxis List of x values from which the y values will be calculated.
     * @param polynomials The quadratic functions that will determine the y values.
     *
     * @return A list of y values, as Doubles.
     *
     * @see getXAxis
     * @see Section
     * @see generateMomentFunction
     * @see generateShearPolynomial
     * @see Polynomial
     */
    fun getYAxis(sections: List<Section>, xAxis: List<Float>, polynomials: List<Polynomial>): List<Float> = TODO()

    fun getShearForceDiagram(structure: Structure, bar: Bar, step: Float): Pair<List<Float>, List<Float>> {
        val sections = getSections(structure, bar)

        val x = getXAxis(sections, step)

        val polynomial = sections.indices.map { generateShearFunction(sections, it) }

        val y = getYAxis(sections, x, polynomial)

        return Pair(x, y)
    }

    fun getBendingMomentDiagram(structure: Structure, bar: Bar, step: Float): Pair<List<Float>, List<Float>> {
        val sections = getSections(structure, bar)

        val x = getXAxis(sections, step)

        val polynomial = sections.indices.map { generateMomentFunction(sections, it) }

        val y = getYAxis(sections, x, polynomial)

        return Pair(x, y)
    }

    fun scaleAxis(axis: List<Float>, factor: Float) = axis.map { it * factor }
}