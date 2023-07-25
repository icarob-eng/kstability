package com.moon.kstability.parsing

object StringsPtBr {
    // exceptions
    @Deprecated("Used only on the old parsers")
    const val invalidSyntax = "Notação inválida."
    @Deprecated("Used only on the old parsers")
    const val undeclaredNode = "Nó não declarado."
    @Deprecated("Used only on the old parsers")
    const val invalidBeam = "Barra declarada é inválida."
    @Deprecated("Used only on the old parsers")
    const val invalidLoadSyntax1 = "Número de nós inválidos"
    @Deprecated("Used only on the old parsers")
    const val invalidBeamList1 = "A lista de barras é inválida."
    @Deprecated("Used only on the old parsers")
    const val invalidSupportList = "A lista de suportes é inválida."

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
    const val invalidNodeRef = "Referenced node {} not in the structure."
    const val invalidSupportSyntax = "Invalid support syntax. Value = {}"
    const val invalidSupportGender = "Invalid support gender. Value = {}"
    const val invalidBeamList = "Invalid node quantity for a beam. Value = {}"
    const val invalidLoadVectorSyntax = "Invalid load vector syntax. Value = {}"
    const val invalidLoadNodesSyntax = "Invalid load syntax, $node or $nodes expected."
    const val invalidLoadSyntax = "Invalid load syntax. Value = {}"
    const val invalidSection = "Invalid {} section."
}