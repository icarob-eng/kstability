package com.kstabilty

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class DiagramsTest {
    private val knotSampleA = Knot("A", Vector(0F, 0F))
    private val knotSampleB = Knot("B", Vector(1F, 0F))
    private val knotSampleC = Knot("C", Vector(3F, 0F))
    private val knotSampleD = Knot("D", Vector(4F, 0F))
    private val barSample = Bar(knotSampleA, knotSampleD)

    private val samplePolynomials = listOf(
        Polynomial(0F, 0F, 1F),
        Polynomial(0F, 0F, 2F),
        Polynomial(0F, 0F, 3F),
        Polynomial(0F, 0F, 4F)
    )

    private val structureSample = Structure("My structure", mutableListOf(knotSampleA, knotSampleB, knotSampleC, knotSampleD))
    private val sectionSamples = Diagrams.getSections(structureSample, barSample)


    @Test
    fun getSectionsTest() {
        assertEquals(structureSample.knots.size, sectionSamples.size)
    }

    @Test
    fun getXAxisSizeTest() {
        val randomResolution = 0.17F

        val xAxisSample = Diagrams.getXAxis(sectionSamples, randomResolution)

        val expectedSize = barSample.barVector.length()/randomResolution + structureSample.knots.size * 2 + 1

        assertEquals(expectedSize.toInt(), xAxisSample.size)
    }

    @Test
    fun getYAxisTest() {
        val randomResolution = 0.19F

        val xAxisSample = Diagrams.getXAxis(sectionSamples, randomResolution)
        val yAxisSample = Diagrams.getYAxis(
            sectionSamples,
            xAxisSample,
            samplePolynomials
        )

        val expectedSize = barSample.barVector.length()/randomResolution + structureSample.knots.size * 2 + 1

        assertEquals(expectedSize.toInt(), yAxisSample.size)


        val axes = xAxisSample zip yAxisSample
        assertAll("y axis values test",
            // remember: first == x, second == y
            { assertEquals(1F, axes.first { it.first >= 0.5F }.second) },
            { assertEquals(2F, axes.first { it.first >= 2F }.second) },
            { assertEquals(3F, axes.first { it.first >= 3.5F }.second) },
//            { assertEquals(4F, axes.first { it.first >= 0.5F }.first) } // index out of bounds
            )
    }

    @Test
    fun rotatePlotTest() {
        val randomResolution = 0.017F
        val i = 2F

        val x = Diagrams.getXAxis(sectionSamples, randomResolution)
        val y = Diagrams.getYAxis(
            sectionSamples,
            x,
            samplePolynomials
        )

        val rotatedAxes = Diagrams.rotatePlot(Pair(x, y), i)

        assertAll("rotated plot points test",
            { assertEquals(Vector(x[5], y[5]).getRotated(i), Vector(rotatedAxes.first[5], rotatedAxes.second[5])) },
            { assertEquals(Vector(x[11], y[11]).getRotated(i), Vector(rotatedAxes.first[11], rotatedAxes.second[11])) },
            { assertEquals(Vector(x[13], y[13]).getRotated(i), Vector(rotatedAxes.first[13], rotatedAxes.second[13])) },
            { assertEquals(Vector(x[17], y[17]).getRotated(i), Vector(rotatedAxes.first[17], rotatedAxes.second[17])) },
            { assertEquals(Vector(x[19], y[19]).getRotated(i), Vector(rotatedAxes.first[19], rotatedAxes.second[19])) }
            )

    }

    @Test
    fun scaleAxisTest() {
        val sampleAxis = listOf(0.0F, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F)

        val expectedAxis = floatArrayOf(0.0F, 0.2F, 0.4F, 0.6F, 0.8F, 1.0F)

        assertArrayEquals(expectedAxis, Diagrams.scaleAxis(sampleAxis, 2F).toFloatArray())
    }
}