package com.kstabilty

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll

import kotlin.io.path.*

class IntegrationTest {
    companion object {
        private const val outputPath = "src/jvmTest/kotlin/com/kstabilty/IntegrationTestOutput"

        @JvmStatic
        @BeforeAll
        fun shredOldFiles() = Path(outputPath).forEachDirectoryEntry { it.deleteExisting() }

        fun writeCsv(fileName: String, axes: Axes) {
            val writer = Path("$outputPath/$fileName").let {
                it.createFile()
                it.bufferedWriter()
            }

            writer.write("x, y".trimIndent())

            for (i in 0 until axes.first.size) {
                writer.newLine()
                writer.write("${axes.first[i]}, ${axes.second[i]}")
            }

            writer.flush()
        }
    }


}