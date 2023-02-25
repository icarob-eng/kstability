package com.kstabilty


/**
 * This interface defines methods to plot and draw structural components and their arguments.
 *
 * Coulors, line thicness, scale and radius should be defined as constants in the implementing class.
 * Each method represents a different object's type, therefore, should have those parameters different.
 */
interface IStructureDrawer {

    /**
     * Draws a simple line representing a bar.
     *
     * @param bar the bar that will be drawned
     * @return void
     */
    fun drawBar(bar: Bar): Nothing

    /**
     * Draws a **circle** tangent to the specified point, representing a roller support,
     * with the diameter direciton been opposite to the support's direction.
     *
     * @param support A first gender support
     * @return void
     * @see drawHinge
     */
    fun drawFirstGenderSupport(support: Support): Nothing

    /**
     * Draws an equilateral triangle with a vertex in the given support's knot and the triangle's bisector in the
     * opposite direction of the support's direction, representing a pinned support.
     *
     * @param support A second gender support
     * @return void
     */
    fun drawSecondGenderSupport(support: Support): Nothing

    /**
     * Draws a line perpendicular to the given support's direction, with the midpoint in the support's knot,
     * representing a fixed support.
     *
     * @param support A third gender suppoert
     * @return void
     */
    fun drawThirdGenderSupport(support: Support): Nothing

    /**
     * Draws an arrow representing a given Load. The arrow doesn't need to scale in proportion to the load.
     * The arrow will be drawn with its tip in the load's knot, istead of having its tail been drawn there,
     * therefore, this method should draw an arrow in the opposite direction of the load's direction, poiting
     * to the knot.
     *
     * @param pointLoad The load that will be drawn
     * @return void
     */
    fun drawArrow(pointLoad: PointLoad): Nothing

    /**
     * Draws several arrows perpendicular to the distributed load's length, putting many points along it and making the
     * procces documented in `drawArrow()` and also draws a single retangle arround those arrows.
     *
     * @param distributedLoad
     * @return void
     * @see drawArrow
     */
    fun drawDistributedLoad(distributedLoad: DistributedLoad): Nothing

    /**
     * Plots a normal cartesian function, with `listX` representing the marks in the abscissa axis and `listY`,
     * the ordinate axis.
     *
     * @param listX A list with all the x components of the points in the graph. The abscissa.
     * @param listY A list with all the y components of the points in the graph. The ordinate.
     * @return void
     */
    fun plotHorizontalChart(listX: List<Float>, listY: List<Float>): Nothing

    /**
     * Plots a cartesian graph vertically, with the `listX` still representing the abscissa, but drawn from top down
     * and the `listY`, representing the ordinate axis, but been drawn horizontaly.
     *
     * @param listX A list with the abscissa components of the points in the graph, but that will be drawn from
     * top down.
     * @param listY A list with the ordinate components of the points in the graph, but that will be drawn from left
     * to right.
     * @return void
     * @see plotHorizontalChart
     */
    fun plotVerticalChart(listX: List<Float>, listY: List<Float>): Nothing

    /**
     * Draws a cicle with a knot in the middle, representing a pinned connection, or hinge.
     *
     * @param knot The center of the circle.
     * @return void
     * @see drawFirstGenderSupport
     */
    fun drawHinge(knot: Knot): Nothing

    /**
     * Writes a label from the side of the given positon. This could be associate to a knot to identify its name, or a
     * load to identify its intensity.
     *
     * @param position Position from wich the label will be drawn near.
     * @param text The actual label's text. Could be a number with units or some characteres.
     * @return void
     */
    fun writeLabel(position: Vector, text: String): Nothing
}