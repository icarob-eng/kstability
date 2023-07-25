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
    const val invalidLoadSyntax = "Número de nós inválidos"
    @Deprecated("Used only on the old parsers")
    const val invalidBeamList = "A lista de barras é inválida."
    @Deprecated("Used only on the old parsers")
    const val invalidSupportList = "A lista de suportes é inválida."

    // exceptions


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
}