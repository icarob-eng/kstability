package com.kstabilty


typealias Axis = List<Float>
typealias Axes = Pair<Axis, Axis>
object Diagrams {
    /**
     * Defines a section along one [bar], where the [knot] marks the **start** of the section, and the bar, the direction.
     *
     * @see Diagrams.getSections
     */
    data class Section (val bar: Bar, val knot: Knot)

    /**
     * Derives a list of sections from a structure. Each section is defined by a knot, where it begins.
     * The knots are divided by a line perpendicular to the given bar.
     *
     * **By now this function considers all loads in the structure, not just loads in the bar**
     *
     * @see Section
     * @param structure The structure that will be divided.
     * @param bar The direction that the sections will be divided.
     *
     * @return List of sections.
     */
    fun getSections(structure: Structure, bar: Bar): List<Section> {
        val sections = structure.knots.sortedBy { it.pos.x }.map { Section(bar, bar.makeTangentKnot(it)) }
        return mutableListOf(sections).removeLast()
    }

    /**
     * Derives the polynomial expression for the bending moment, for a section, been a piece in a Macaulay's function.
     *
     * @param allSections A list with all the structure's sections, where the sections to the left will be actually
     * used.
     * @param sectionId Specify the section from which will be calculated the polynomial.
     *
     * @return The generated polynomial.
     *
     * @see getSections
     * @see Polynomial
     */
    fun generateMomentFunction(allSections: List<Section>, sectionId: Int): Polynomial {
        val relevantSections = allSections.filterIndexed { index, _ -> index <= sectionId }
        val pointLoads = relevantSections.flatMap { it.knot.pointLoads }
        val distributedLoads = relevantSections.flatMap { it.knot.distributedLoads }
        val momentum = relevantSections.map { it.knot.momentum }.sum()

        var resultPolynomial = Polynomial.MomentumLoad.bendingMoment(momentum)
        resultPolynomial = pointLoads.map { Polynomial.PointLoad.bendingMoment(it.knot.pos, it.vector)}.fold(resultPolynomial) { i, j -> i+j}
        // equivalent of: result += map.sum()

        distributedLoads.forEach { dL ->
            val limits = listOf(dL.knot1.pos, dL.knot2.pos).sortedBy { it.x }

            resultPolynomial += if (limits.last().x >= relevantSections.last().knot.pos.x)
                Polynomial.DistributedLoad.bendingMomentEnd(limits.first(), dL.vector, limits.last())
            else Polynomial.DistributedLoad.bendingMoment(limits.first(), dL.vector)
        }

        return resultPolynomial
    }

    /**
     * Derives the polynomial expression for the shear stress, for a section, been a piece in a Macaulay's function.
     *
     * @param allSections A list with all the structure's sections, where the sections to the left will be actually
     * used.
     * @param sectionId Specify the section from which will be calculated the polynomial.
     *
     * @return The generated polynomial.
     *
     * @see getSections
     * @see Polynomial
     */
    fun generateShearFunction(allSections: List<Section>, sectionId: Int): Polynomial {
        val relevantSections = allSections.filterIndexed { index, _ -> index <= sectionId }
        val pointLoads = relevantSections.flatMap { it.knot.pointLoads }
        val distributedLoads = relevantSections.flatMap { it.knot.distributedLoads }
//        val momentum = relevantSections.map { it.knot.momentum }.sum()

        var resultPolynomial = Polynomial.MomentumLoad.shearStress()
        resultPolynomial = pointLoads.map { Polynomial.PointLoad.shearStress(it.vector)}.fold(resultPolynomial) { i, j -> i+j}
        // equivalent of: result += map.sum()

        distributedLoads.forEach { dL ->
            val limits = listOf(dL.knot1.pos, dL.knot2.pos).sortedBy { it.x }

            resultPolynomial += if (limits.last().x >= relevantSections.last().knot.pos.x)
                Polynomial.DistributedLoad.shearStressEnd(limits.first(), dL.vector, limits.last())
            else Polynomial.DistributedLoad.shearStress(limits.first(), dL.vector)
        }

        return resultPolynomial
    }

    /**
     * Derives the polynomial expression for the normal stress, for a section, been a piece in a Macaulay's function.
     *
     * @param allSections A list with all the structure's sections, where the sections to the left will be actually
     * used.
     * @param sectionId Specify the section from which will be calculated the polynomial.
     *
     * @return The generated polynomial.
     *
     * @see getSections
     * @see Polynomial
     */
    fun generateNormalFunction(allSections: List<Section>, sectionId: Int): Polynomial {
        val relevantSections = allSections.filterIndexed { index, _ -> index <= sectionId }
        val pointLoads = relevantSections.flatMap { it.knot.pointLoads }
        val distributedLoads = relevantSections.flatMap { it.knot.distributedLoads }
//        val momentum = relevantSections.map { it.knot.momentum }.sum()

        var resultPolynomial = Polynomial.MomentumLoad.normalStress()
        resultPolynomial = pointLoads.map { Polynomial.PointLoad.normalStress(it.vector)}.fold(resultPolynomial) { i, j -> i+j}
        // equivalent of: result += map.sum()

        distributedLoads.forEach { dL ->
            val limits = listOf(dL.knot1.pos, dL.knot2.pos).sortedBy { it.x }

            resultPolynomial += if (limits.last().x >= relevantSections.last().knot.pos.x)
                Polynomial.DistributedLoad.normalStressEnd(limits.first(), dL.vector, limits.last())
            else Polynomial.DistributedLoad.normalStress(limits.first(), dL.vector)
        }

        return resultPolynomial
    }

    /**
     * Generates a chart's list of plot point's x values.
     *
     * **x values may repeat depending on the end of a section.**
     *
     * @param sections List of structure's sections.
     * @param resolution The maximum distance between points.
     *
     * @return A list of x values, as Floats.
     *
     * @see getYAxis
     * @see getSections
     * @see Section
     */
    fun getXAxis(sections: List<Section>, resolution: Float): Axis{
        val referenceBar = sections.first().bar
        val axis = mutableListOf<Float>()
        var x = referenceBar.knot1.pos.x
        while (x < referenceBar.knot2.pos.x) {
            axis.add(x)
            x += resolution
        }

        for (section in sections) {
            val i = section.knot.pos.x
            if (i !in axis) axis.add(i)
            axis.add(i)  // guarantees that there's 2 points at intersection
        }
        return axis
    }


    /**
     * Calculates y values from x values and the quadratic functions and put the results in a list of Float.
     *
     * @param sections List of structure's sections.
     * @param xAxis List of x values from which the y values will be calculated.
     * @param polynomials The quadratic functions that will determine the y values.
     *
     * @return A list of y values, as Float.
     *
     * @see getXAxis
     * @see Section
     * @see generateNormalFunction
     * @see generateMomentFunction
     * @see generateShearFunction
     * @see Polynomial
     */
    fun getYAxis(sections: List<Section>, xAxis: Axis, polynomials: List<Polynomial>): Axis {
        val yAxis = mutableListOf<Float>()
        for (x in xAxis) {
            var i = sections.indexOfLast { x >= it.knot.pos.x }  // this is responsible to actually divide the sections
            // if the next x is equal to current, make the section be the last one, else, use the new one
            i = if (x == xAxis[xAxis.indexOf(x) + 1]) i - 1 else i
            yAxis.add(polynomials[i](x))
        }
        return yAxis
    }


    /**
     * Generates a pair of values representing the x and y axes of a plot of a function generated by the given `method`.
     *
     * The methods could be `generateShearFunction`, `generateMomentFunction` or (unimplemented)
     * `generateNormalFunction`.
     *
     * @param inputStructure A structure with all data that will be used. It needs to be stabilized.
     * @param bar The bar to analyze the stresses.
     * @param method A Macaulay's function that could be used to plot the diagrams.
     * @param step This defines the horizontal distance between points, which defines how many points there will be.
     *
     * @return A pair of values representing the x and y axes of a plot
     *
     * @see Stabilization
     * @see Structure
     * @see generateNormalFunction
     * @see generateShearFunction
     * @see generateMomentFunction
     *
     * @see IStructureDrawer
     */
    fun getDiagram(inputStructure: Structure, bar: Bar,
                   method: (List<Section>, Int) -> Polynomial,
                   step: Float): Pair<Axes, List<Polynomial>> {

        val structure = inputStructure.getRotatedCopy(bar.barVector.inclination())

        val sections = getSections(structure, bar)

        val x = getXAxis(sections, step)
        val polynomials = sections.indices.map { method(sections, it) }
        val y = getYAxis(sections, x, polynomials)

        return Pair(rotatePlot(Pair(x, y), -bar.barVector.inclination()), polynomials)
    }


    /**
     * Rotates a pair of axes, by the given inclination (rise/run).
     */
    fun rotatePlot(axes: Axes, i: Float): Axes{
        val vectorList: List<Vector> = axes.first.mapIndexed { index, it -> Vector(it, axes.second[index])}
        vectorList.forEach { it.getRotated(i) }

        return Pair(vectorList.map { it.x }, vectorList.map { it.y })
    }

//    fun scaleAxis(axis: Axis, factor: Float) = axis.map { it * factor }
}