package com.kstabilty

class Stabilization {
    companion object {
        fun isIsostatic(structure: Structure): Boolean {
            // todo: advanced method for checking isostacity
            var genders = 0
            for (support in structure.getSupports())
                genders += support.gender.reactions
            return genders == 3
        }

        fun isStable(structure: Structure) = getResultMomentum(structure) == 0F &&
                getResultForce(structure) == Vector(0F,0F)

        /**
         * Read "momentum" as "bending moment".
         */
        fun getResultMomentum(structure: Structure, axis: Vector = Vector(0F, 0F)): Float {
            var momentum = 0F
            for (load in structure.getEqvLoads())
                momentum += (load.knot.pos - axis).crossModule(load.vector)
            // T = d x F; d = d1 - d_origin

            return momentum
        }

        /**
         * Note that the result type is a vector, not a laod, since the bending moment from
         * the resultant force is calculated in `getResultMomentum`.
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
         * supports. Raises an assertion error if it doesn't have a way to solve de structure.
         */
        fun stabilize(structure: Structure) {
//            assert(isStable(structure))  // todo: handle else case

            val resultMomentum = getResultMomentum(structure)
            val resultForce = getResultForce(structure)

            val supports = structure.getSupports()

            if (supports.size == 1) {
//                assert(supports[0].gender == SupportGender.THIRD)  // todo: handle else case
                structure.getSupports()[0].knot.momentum -= resultMomentum
                Load(structure.getSupports()[0].knot, -resultForce)
            } else {
//                assert(supports.size == 2)  // todo: handle else case

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
                        throw AssertionError()  // todo: handle else case
                    }
                }
            }
        }

    }
}