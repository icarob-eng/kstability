package com.kstabilty


/**
 * This interface defines methods to plot and draw structural components and their arguments.
 *
 * Colors, line thickness, scale and radius should be defined as constants in the implementing class.
 * Each method represents a different object's type, therefore, should have those parameters different.
 */
interface IStructureDrawer {

    /**
     * Draws a simple line representing a bar.
     *
     * @param bar the bar that will be drawn
     * @return void
     */
    fun drawBar(bar: Bar)

    /**
     * Draws a **circle** tangent to the specified point, representing a roller support,
     * with the diameter direction been opposite to the support's direction.
     *
     * @param support A first gender support
     * @return void
     * @see drawHinge
     */
    fun drawFirstGenderSupport(support: Support)

    /**
     * Draws an equilateral triangle with a vertex in the given support's knot and the triangle's bisector in the
     * opposite direction of the support's direction, representing a pinned support.
     *
     * @param support A second gender support
     * @return void
     */
    fun drawSecondGenderSupport(support: Support)

    /**
     * Draws a line perpendicular to the given support's direction, with the midpoint in the support's knot,
     * representing a fixed support.
     *
     * @param support A third gender support
     * @return void
     */
    fun drawThirdGenderSupport(support: Support)

    /**
     * Draws an arrow representing a given Load. The arrow doesn't need to scale in proportion to the load.
     * The arrow will be drawn with its tip in the load's knot, instead of having its tail been drawn there,
     * therefore, this method should draw an arrow in the opposite direction of the load's direction, pointing
     * to the knot.
     *
     * @param pointLoad The load that will be drawn
     * @return void
     */
    fun drawArrow(pointLoad: PointLoad)

    /**
     * Draws several arrows perpendicular to the distributed load's length, putting many points along it and making the
     * process documented in `drawArrow()` and also draws a single rectangle around those arrows.
     *
     * @param distributedLoad
     * @return void
     * @see drawArrow
     */
    fun drawDistributedLoad(distributedLoad: DistributedLoad)

    /**
     * Drawn a representation of a bending moment around the given position. Direction determines if it's draw
     * clockwise or counterclockwise.
     *
     * @param position Where it will be drawn.
     * @param clockwise True with the moment is clockwise
     * @return void
     */
    fun drawBendingMomentLoad(position: Vector, clockwise: Boolean)

    /**
     * Plots a normal cartesian function, with `listX` representing the marks in the abscissa axis and `listY`,
     * the ordinate axis.
     *
     * @param listX A list with all the x components of the points in the graph. The abscissa.
     * @param listY A list with all the y components of the points in the graph. The ordinate.
     * @return void
     */
    fun plotChart(listX: Axis, listY: Axis)

    /**
     * Draws a circle with a knot in the middle, representing a pinned connection, or hinge.
     *
     * @param knot The center of the circle.
     * @return void
     * @see drawFirstGenderSupport
     */
    fun drawHinge(knot: Knot)

    /**
     * Writes a label from the side of the given position. This could be associate to a knot to identify its name, or a
     * load to identify its intensity.
     *
     * @param position Position from which the label will be drawn near.
     * @param text The actual label's text. Could be a number with units or some characters.
     * @return void
     */
    fun writeLabel(position: Vector, text: String)
}