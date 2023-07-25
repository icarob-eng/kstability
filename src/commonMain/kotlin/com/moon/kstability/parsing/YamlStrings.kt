package com.moon.kstability.parsing

object StringsPtBr {
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