package com.moon.kstability

import com.moon.kstability.parsing.ModelParsers
import com.moon.kstability.parsing.ModelSerializers.serialize
import com.moon.kstability.parsing.StringsPtBr
import com.moon.kstability.parsing.YamlStrings
import org.yaml.snakeyaml.Yaml

object Parsing {
    fun parseYamlString(yaml: String, s: YamlStrings = StringsPtBr()): Structure =
        ModelParsers.parseSections(Yaml().load(yaml), s)

    fun serializeStructureToYaml(structure: Structure, s: YamlStrings = StringsPtBr()): String =
        Yaml().dumpAsMap(structure.serialize(s))
}

