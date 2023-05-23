package com.kstabilty

import kotlin.math.abs
import kotlin.math.max

/**
 * A class with basic routines of the library, including stabilize the structure by default, [drawStructure], and
 * plotting many structure's charts: [drawBendingMoment], [drawShearStress], [drawNormalStress].
 *
 * @see IStructureDrawer
 * @see Structure
 */
class ChartingRoutines (val structure: Structure,
                        private val drawer: IStructureDrawer,
                        private val forceUnit: String = "N",
                        private val lengthUnit: String = "m",
                        private val labelDistancePercentage: Double = 0.05
) {
    private val majorAxisLength: Float

    init {
        Stabilization.stabilize(structure)

        val xAxis = structure.nodes.map { it.pos.x }.sorted()
        val yAxis = structure.nodes.map { it.pos.y }.sorted()

        // necessary to obtain plot scales
        majorAxisLength = max (
            abs(xAxis.first() - xAxis.last()),
            abs(yAxis.first() - yAxis.last()))
    }


    fun drawStructure(labels: Boolean = true) {
        structure.nodes.forEach {
            drawer.writeLabel(findLabelPos(it.pos), it.name)

            if (it.momentum != 0F && labels) drawer.writeLabel(
                findLabelPos(it.pos).orthogonal(),
                it.momentum.toString() + forceUnit  + lengthUnit
            )
            if (it.momentum > 0F) {
                drawer.drawBendingMomentLoad(it.pos, false)
            } else if (it.momentum < 0F) {
                drawer.drawBendingMomentLoad(it.pos, true)
            }
        }

        structure.getBeam().forEach { drawer.drawBeam(it) }

        structure.getSupports().forEach { when(it.gender){
                Support.Gender.FIRST -> drawer.drawFirstGenderSupport(it)
                Support.Gender.SECOND -> drawer.drawSecondGenderSupport(it)
                Support.Gender.THIRD -> drawer.drawThirdGenderSupport(it)
            }
        }

        structure.getPointLoads().forEach {
            if (labels) {
                drawer.writeLabel(
                    findLabelPos(it.node.pos) +
                        Vector.Consts.VERTICAL * majorAxisLength * labelDistancePercentage,
                    it.vector.length().toString() + forceUnit
                )
            }
            drawer.drawArrow(it)
        }

        structure.getDistributedLoads().forEach {
            if (labels) {
                drawer.writeLabel(
                    (findLabelPos(it.node1.pos) + findLabelPos(it.node2.pos))/2,
                    it.vector.toString() + forceUnit + '/' + lengthUnit
                )
            }
            drawer.drawDistributedLoad(it)
        }
    }


    fun drawBendingMoment(step: Float = 0.05F, labels: Boolean = true) {
        structure.getBeam().map { plot(it, Diagrams::generateMomentFunction, step, labels) }
    }

    fun drawShearStress(step: Float = 0.05F, labels: Boolean = true) {
        structure.getBeam().map { plot(it, Diagrams::generateShearFunction, step, labels) }
    }

    fun drawNormalStress(step: Float = 0.05F, labels: Boolean = true) {
        structure.getBeam().map { plot(it, Diagrams::generateNormalFunction, step, labels) }
    }

    private fun plot(beam: Beam, method: (List<Diagrams.Section>, Int) -> Polynomial, step: Float, labels: Boolean) {
        val pair = Diagrams.getDiagram(structure, beam, method, step)
        val axes = pair.first
        // todo: axis scaling
        drawer.plotChart(axes.first, axes.second)

        if (labels) {
            val expressions = pair.second.map {it.toString()}
            expressions.indices.forEach { i ->
                val sectionMiddle = (structure.nodes[i].pos + structure.nodes[i + 1].pos)/2
                drawer.writeLabel(findLabelPos(sectionMiddle), expressions[i])
            }
        }
    }

    private fun findLabelPos(targetPos: Vector): Vector = targetPos + Vector(
        majorAxisLength * labelDistancePercentage,
        - majorAxisLength * labelDistancePercentage
        )  // todo: better method of defining label positions
}