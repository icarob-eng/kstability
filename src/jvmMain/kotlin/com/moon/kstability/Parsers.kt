package com.moon.kstability

import com.moon.kstability.parsing.ModelParsers
import org.yaml.snakeyaml.Yaml

object Parsers {
    fun parseYamlString(yaml: String): Structure = ModelParsers.parseSections(Yaml().load(yaml))
}

