package com.moon.kstability.parsing

object StringsPtBrObject {
    // consts
    const val vertical = "vertical"
    const val horizontal = "horizontal"

    // sections
    const val structureSection = "estrutura"
    const val supportSection = "apoios"
    const val loadsSection = "cargas"
    const val nodesSection = "nós"
    const val beamsSection = "barras"

    // attributes
    const val x = "x"
    const val y = "y"
    const val direction = "direção"
    const val gender = "gênero"
    const val node = "nó"
    const val nodes = "nós"
    const val module = "módulo"
    const val vector ="vetor"

    // exceptions
    const val invalidVector = "Invalid vector. Value = {}"
    const val invalidNodeRef = "Referenced node \"{}\" not in the structure."
    const val invalidSupportSyntax = "Invalid support syntax. Value = {}"
    const val invalidSupportGender = "Invalid support gender. Value = {}"
    const val invalidBeamList = "Invalid node quantity for a beam. Value = {}"
    const val invalidLoadVectorSyntax = "Invalid load vector syntax. Value = {}"
    const val invalidLoadNodesSyntax = "Invalid load syntax, $node or $nodes expected."
    const val invalidLoadSyntax = "Invalid load syntax. Value = {}"
    const val invalidSection = "Invalid {} section."
}

interface YamlStrings {
    // consts
    val vertical: String
    val horizontal: String

    // sections
    val structureSection: String
    val supportSection: String
    val loadsSection: String
    val nodesSection: String
    val beamsSection: String

    // attributes
    val x: String
    val y: String
    val direction: String
    val gender: String
    val node: String
    val nodes: String
    val module: String
    val vector: String

    // exceptions
    val invalidVector: String
    val invalidNodeRef: String
    val invalidSupportSyntax: String
    val invalidSupportGender: String
    val invalidBeamList: String
    val invalidLoadVectorSyntax: String
    val invalidLoadNodesSyntax: String
    val invalidLoadSyntax: String
    val invalidSection: String
}

class StringsPtBr: YamlStrings {
    // consts
    override val vertical = "vertical"
    override val horizontal = "horizontal"

    // sections
    override val structureSection = "estrutura"
    override val supportSection = "apoios"
    override val loadsSection = "cargas"
    override val nodesSection = "nós"
    override val beamsSection = "barras"

    // attributes
    override val x = "x"
    override val y = "y"
    override val direction = "direção"
    override val gender = "gênero"
    override val node = "nó"
    override val nodes = "nós"
    override val module = "módulo"
    override val vector ="vetor"

    // exceptions
    override val invalidVector = "Invalid vector. Value = {}"
    override val invalidNodeRef = "Referenced node \"{}\" not in the structure."
    override val invalidSupportSyntax = "Invalid support syntax. Value = {}"
    override val invalidSupportGender = "Invalid support gender. Value = {}"
    override val invalidBeamList = "Invalid node quantity for a beam. Value = {}"
    override val invalidLoadVectorSyntax = "Invalid load vector syntax. Value = {}"
    override val invalidLoadNodesSyntax = "Invalid load syntax, $node or $nodes expected."
    override val invalidLoadSyntax = "Invalid load syntax. Value = {}"
    override val invalidSection = "Invalid {} section."
}