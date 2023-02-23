package com.kstabilty

object Stabilization {
    /**
     * Check if given structure is isostatic.
     * Courrent algorithm checks if structure have 3 support genders.
     *
     * @param structure Structure to be chaked. Needs to have supports
     * @return Boolean: true if structure have 3 degrees of support
     * @see isStable
     */
    fun isIsostatic(structure: Structure): Boolean {
        // todo: advanced method for checking isostacity
        var genders = 0
        for (support in structure.getSupports())
            genders += support.gender.reactions
        return genders == 3
    }

    /**
     * Checks if given structure is stabilazed i.e. resultant force and banding moment are both zero
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
     * @return Given structure's resultant beindig moment
     */
    fun getResultMomentum(structure: Structure, axis: Vector = Vector(0F, 0F)): Float {
        var momentum = structure.getMomentumLoads()
        for (load in structure.getEqvLoads())
            momentum += (load.knot.pos - axis).crossModule(load.vector)
        // T = d x F; d = d1 - d_origin

        return momentum
    }

    /**
     * Sums all loads and equivalent loads in given structure.
     * Note that the result type is a vector, not a laod, since the bending moment from
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
     * @see getResultForce
     * @see getResultMomentum
     */
    fun getReactionsAB(supportA: Support, supportB: Support,
                       resultForce: Vector, resultMomentum: Float): Pair<Vector, Vector> {
        val ra = supportA.knot.pos
        val rb = supportB.knot.pos
        val c = resultForce
        val m = resultMomentum
        val i = supportB.dir

        val phi = rb.crossModule(i)
        val psi = ra.crossModule(i)

        val ax = - (c.x * ra.x * i.y - c.x * phi - c.y * ra.x * i.x + m * i.x)/(-phi + psi)
        val ay = - (c.x * ra.y * i.y - c.y * ra.y * i.x - c.y * phi + m * i.y)/(-phi + psi)

        val k = - (ax * ra.y - ay * ra.x + m)/phi
        // I really don't know why it hat to be negative, but is how it works

        return Pair(Vector(ax, ay), i * k)
    }

    /**
     * Makes the passed structure stable by adding loads or momenta to the adequate
     * supports.
     *
     * @throws AssertionError throwed if it doesn't have an algorithm to solve de structure.
     * @param structure Structure to be stabilized. It will be altered
     * @return Nothing
     * @see isIsostatic
     * @see getResultForce
     * @see getResultMomentum
     * @see getReactionsAB
     */
    fun stabilize(structure: Structure) {
        if (! isIsostatic(structure))
            throw IllegalArgumentException("A estrutura não é isostática")
        val resultMomentum = getResultMomentum(structure)
        val resultForce = getResultForce(structure)

        val supports = structure.getSupports()

        if (supports.size == 1 && supports[0].gender == SupportGender.THIRD) {
            structure.getSupports()[0].knot.momentum -= getResultMomentum(structure,
                structure.getSupports()[0].knot.pos)
            Load(structure.getSupports()[0].knot, -resultForce)
        } else {
            if (supports.size != 2)
                throw IllegalArgumentException("A estrutura não é isostática")

            when (SupportGender.SECOND){
                supports[0].gender -> {  // supports[0] = a
                    val reactionPair = getReactionsAB(
                        supports[0], supports[1],
                        resultForce, resultMomentum
                    )

                    Load(structure.getSupports()[0].knot, reactionPair.first)
                    Load(structure.getSupports()[1].knot, reactionPair.second)
                }
                supports[1].gender -> {  // supports[1] = a
                    val reactionPair = getReactionsAB(
                        supports[1], supports[0],
                        resultForce, resultMomentum
                    )

                    Load(structure.getSupports()[1].knot, reactionPair.first)
                    Load(structure.getSupports()[0].knot, reactionPair.second)
                }
                else -> {
                    throw IllegalArgumentException("A estrutura não é isostática")
                }
            }
        }
    }
}