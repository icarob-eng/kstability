package com.moon.kstability

import com.moon.kstability.parsing.ModelParsers
import com.moon.kstability.parsing.ModelSerializers.serialize
import org.yaml.snakeyaml.Yaml

object Parsing {
    fun parseYamlString(yaml: String): Structure = ModelParsers.parseSections(Yaml().load(yaml))

    fun serializeStructureToYaml(structure: Structure): String = Yaml().dumpAsMap(structure.serialize())
}

