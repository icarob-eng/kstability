package com.moon.kstability

object Stabilization {
    /**
     * Check if given structure is isostatic.
     * Current algorithm checks if structure have 3 support genders.
     *
     * @param structure Structure to be checked. Needs to have supports
     * @return Boolean: true if structure have 3 degrees of support
     * @see isStable
     */
    fun isIsostatic(structure: Structure): Boolean {
        // todo: advanced method for checking isostaticity
        var genders = 0
        for (support in structure.getSupports())
            genders += support.gender.reactions
        return genders == 3
    }

    /**
     * Checks if given structure is stabilized i.e. resultant force and banding moment are both zero
     *
     * @see isIsostatic
     * @see getReactionsAB
     */
    fun isStable(structure: Structure) = getResultMomentum(structure) == 0F &&
            getResultForce(structure) == Vector(0F,0F)

    /**
     * Sums bending moment in given structure, from the given rotation axis.
     * Read "momentum" as "bending moment".
     *
     * @param structure Structure from witch the resultant bending moment is calculated
     * @param axis Axis of rotation to calculate the bending momentum
     * @return Given structure's resultant bending moment
     */
    fun getResultMomentum(structure: Structure, axis: Vector = Vector(0F, 0F)): Float {
        var momentum = structure.getMomentumLoads()
        for (load in structure.getEqvLoads())
            momentum += (load.node.pos - axis).crossModule(load.vector)
        // T = d x F; d = d1 - d_origin

        return momentum
    }

    /**
     * Sums all loads and equivalent loads in given structure.
     * Note that the result type is a vector, not a load, since the bending moment from
     * the resultant force is calculated in `getResultMomentum`.
     *
     * @param structure Structure from witch the resultant force is calculated
     * @return Given structure's resultant force, as a vector, without point of application
     */
    fun getResultForce(structure: Structure): Vector {
        var result = Vector(0F, 0F)
        for (load in structure.getEqvLoads())
            result += load.vector

        return result
    }

    /**
     * Return reactions for a support of second gender A and other of the first, B.
     * The formula is derived in the `algebraic_formulations.ipynb` file.
     *
     * @param supportA Second gender support
     * @param supportB First gender support
     * @param resultForce Structure's resultant force, calculated by `getResultForce()`
     * @param resultMomentum Structure's resultant force, calculated by `getResultMomentum()`
     *
     * @suppress UnnecessaryVariable For clarity in the equations.
     *
     * @see getResultForce
     * @see getResultMomentum
     */
    @Suppress("UnnecessaryVariable")
    fun getReactionsAB(supportA: Support, supportB: Support,
                       resultForce: Vector, resultMomentum: Float): Pair<Vector, Vector> {
        val ra = supportA.node.pos
        val rb = supportB.node.pos
        val c = resultForce
        val m = resultMomentum
        val i = supportB.direction

        val phi = rb.crossModule(i)
        val psi = ra.crossModule(i)

        val ax = (c.x * ra.x * i.y - c.x * phi - c.y * ra.x * i.x + m * i.x)/(-phi + psi)
        val ay = (c.x * ra.y * i.y - c.y * ra.y * i.x - c.y * phi + m * i.y)/(-phi + psi)

//        return if (phi != 0f) {
//            val k = (ax * ra.y - ay * ra.x + m) / phi
            
//            Pair(- Vector(ax, ay), - i * k)
        val fa = Vector(ax, ay)
        val fb = resultForce - fa

        return Pair(-fa, -fb)
    }

    @Deprecated("This function will be substituted by v2.x", ReplaceWith("Structure.stabilizeStructure()"))
    fun stabilizeStructure(structure: Structure) = structure.stabilize()

    /**
     * Makes the structure stable by adding loads or momenta to the adequate supports.
     *
     * @throws IllegalArgumentException thrown if it doesn't have an algorithm to solve de structure.
     *
     * @see isIsostatic
     * @see getResultForce
     * @see getResultMomentum
     * @see getReactionsAB
     */
    @Throws(IllegalArgumentException::class)
    fun Structure.stabilize() {
        val exception = IllegalArgumentException("A estrutura não é isostática")

        if (isStable(this)) return
        if (! isIsostatic(this)) throw exception

        val resultMomentum = getResultMomentum(this)
        val resultForce = getResultForce(this)

        val supports = getSupports()

        if (supports.size == 1 && supports[0].gender == Support.Gender.THIRD) {
            val momentumAtSupport = getResultMomentum(this,
                getSupports()[0].node.pos)

            getSupports()[0].node.momentum -= momentumAtSupport
            getSupports()[0].node.reactionMomentum = - momentumAtSupport
            PointLoad(getSupports()[0].node, -resultForce, true)

        } else if (supports.size == 2) {
            when (Support.Gender.SECOND){
                supports[0].gender -> {  // supports[0] = a
                    val (reactionA, reactionB) = getReactionsAB(
                        supports[0], supports[1],
                        resultForce, resultMomentum
                    )

                    PointLoad(getSupports()[0].node, reactionA, true)
                    PointLoad(getSupports()[1].node, reactionB, true)
                }
                supports[1].gender -> {  // supports[1] = a
                    val (reactionA, reactionB) = getReactionsAB(
                        supports[1], supports[0],
                        resultForce, resultMomentum
                    )

                    PointLoad(getSupports()[0].node, reactionB, true)
                    PointLoad(getSupports()[1].node, reactionA, true)
                }
                else -> throw exception
            }
        } else throw exception
    }
}