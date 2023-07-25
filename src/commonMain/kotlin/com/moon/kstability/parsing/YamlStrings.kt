package com.moon.kstability.parsing

object StringsPtBr {
    // exceptions
    const val invalidSyntax = "Notação inválida."
    const val undeclaredNode = "Nó não declarado."
    const val invalidBeam = "Barra declarada é inválida."
    const val invalidLoadSyntax = "Número de nós inválidos"
    const val invalidBeamList = "A lista de barras é inválida."
    const val invalidSupportList = "A lista de suportes é inválida."

    // consts
    const val vertical = "vertical"
    const val horizontal = "horizontal"

    // sections
    const val structureSection = "estrutura"
    const val loadsSection = "cargas"
    const val nodesSection = "nós"
    const val beamsSection = "barras"

    // attributes
    const val x = "x"
    const val y = "y"
    const val direction = "direção"
    const val gender = "gênero"
    const val node = "nó"
    const val module = "módulo"
    const val vector ="vetor"
}