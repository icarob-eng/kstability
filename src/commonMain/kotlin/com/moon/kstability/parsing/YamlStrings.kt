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
    override val invalidVector = "Vetor inválido. Texto inserido = {}"
    override val invalidNodeRef = "Nó referenciado \"{}\" não foi inserido na estrutura."
    override val invalidSupportSyntax = "Sintaxe de Suporte inválida. Texto inserido = {}"
    override val invalidSupportGender = "Gênero de suporte invalido. Esperado: 1, 2 ou 3. Inserido = {}"
    override val invalidBeamList = "Quantidade inválida de nós em uma barra. Texto inserido = {}"
    override val invalidLoadVectorSyntax = "Sintaxe de vetor de carga inválida. Texto inserido = {}"
    override val invalidLoadNodesSyntax = "Sintaxe para carga inválida, \"$node\" ou \"$nodes\" esperados."
    override val invalidLoadSyntax = "Sintaxe para carga inválida. Texto inserido = {}"
    override val invalidSection = "Seção de {} inválida."
}

class StringEn: YamlStrings {
    // consts
    override val vertical = "vertical"
    override val horizontal = "horizontal"

    // sections
    override val structureSection = "structure"
    override val supportSection = "supports"
    override val loadsSection = "loads"
    override val nodesSection = "nodes"
    override val beamsSection = "beams"

    // attributes
    override val x = "x"
    override val y = "y"
    override val direction = "direction"
    override val gender = "gender"
    override val node = "node"
    override val nodes = "nodes"
    override val module = "module"
    override val vector ="vector"

    // exceptions
    override val invalidVector = "Invalid vector. Inserted value = {}"
    override val invalidNodeRef = "Referenced node \"{}\" not in the structure."
    override val invalidSupportSyntax = "Invalid support syntax. Inserted value = {}"
    override val invalidSupportGender = "Invalid support gender. Expected: 1, 2 or 3. Inserted = {}"
    override val invalidBeamList = "Invalid node quantity for a beam. Inserted value = {}"
    override val invalidLoadVectorSyntax = "Invalid load vector syntax. Inserted value = {}"
    override val invalidLoadNodesSyntax = "Invalid load syntax, $node or $nodes expected."
    override val invalidLoadSyntax = "Invalid load syntax. Inserted value = {}"
    override val invalidSection = "Invalid {} section."
}