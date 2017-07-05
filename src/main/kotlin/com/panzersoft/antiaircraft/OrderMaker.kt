package com.panzersoft.antiaircraft

import com.panzersoft.antiaircraft.orders.AimOrder
import com.panzersoft.antiaircraft.orders.Order
import com.panzersoft.antiaircraft.orders.WaitOrder
import java.util.concurrent.ThreadLocalRandom

/**
 * Creates orders when requested.
 */

object OrderMaker {
    /**
     * Makes a random list of operands.
     *
     * Currently two general types of operands exist, nullaries, which take no parameters like FIRE and RELOAD,
     * or unary operands which take one number as a parameter like AIM and WAIT.
     *
     * This function creates a random list of operands which may or may not make sense when grouped together.
     *
     * For example, NUM followed by WAIT makes sense, because WAIT will use the NUM as a parameter, however NUM
     * followed by FIRE makes no sense as FIRE doesn't take a parameter as an input.
     *
     * This function makes no attempt to make sensible choices, the random list gets cleaned up by [make].
     *
     * @param size size of the list to generate.
     * @return list of operands of given size.
     */
    fun makeRandomList(size: Int) : List<Operands> {
        return 0.rangeTo(size).map {
            val value = ThreadLocalRandom.current().nextDouble(0.0, 1.0)
            if (value < .3) {
                Operands.NUM
            } else if (value < .4) {
                Operands.FIRE
            } else if (value < .5) {
                Operands.RELOAD
            } else if (value < .75) {
                Operands.WAIT
            } else {
                Operands.AIM
            }
        }
    }

    /**
     * Creates a random list of orders to be used with a Commander.
     *
     * The function starts off with creating a random list of operands, something like:
     *
     *      NFWWWANW...
     *
     * Then the function creates a list of indices which locate all non NUM operands. NUM operands are give '-1' as
     * their locations:
     *
     *      {-1, 1, 2, 3, 4, 5, -1, 7, ... }
     *
     * Then all -1's are removed:
     *
     *      {1, 2, 3, 4, 5, 7, ... }
     *
     * Using the indices, the list if broken up into smaller pieces:
     *
     *      {{NF}, {W}, {W}, {W}, {A}, {NW}, ...}
     *
     * Finally, invalid entries are removed:
     *
     *      {{NW}, ... }
     *
     * ... and converted into orders by using expression trees.
     *
     * Note: NF is removed because F doesn't need a N. Ws and the A are removed because they do need a corresponding N.
     *
     * @return list of valid orders.
     */

    fun make() : List<Order> {
        val opList = makeRandomList(1000)
        val indices = opList.withIndex().map {
            if (! isNumberOp(it.value)) {
                it.index
            } else {
                -1
            }
        }.filter { it != -1 }

        val validExpressions = indices.withIndex().map {
            val start = if (it.index > 0) { indices[it.index-1] + 1 } else { 0 }
            val end = it.value + 1
            opList.subList(start, end)
        }.filter { (it.size == 1 && isNullaryOp(it[0])) || (it.size == 2 && it[0] == Operands.NUM) }

        val expressionTrees = validExpressions.map {
            val et = ExpressionTree()
            et.buildTree(it)
            et
        }

        return expressionTrees.map {
            it.generateOrder()
        }
    }

    /**
     * Creates a new list of orders by mutating an existing list of orders.
     *
     * The WAIT and AIM orders have associated NUM orders. This functions finds these types of orders and modifies
     * the value using a random double.
     *
     * @param orders existing list of orders used by another [Commander]
     * @return mutated order list
     */
    fun mutate(orders: List<Order>) : List<Order> {
        val newOrders = orders.map {
            if (isMutable(it.op)) {
                val value = ThreadLocalRandom.current().nextDouble(0.0, 1.0)
                if (value >= .05) { // most mutate
                    when (it.op) {
                        Operands.WAIT -> {
                            val existing = it as WaitOrder
                            WaitOrder(existing.waitTime.mutate())
                        }
                        Operands.AIM -> {
                            val existing = it as AimOrder
                            AimOrder(existing.angle.mutate())
                        }
                        else -> {
                            throw IllegalArgumentException("Unsupported mutable op")
                        }
                    }
                } else {
                    it
                }
            } else {
                it
            }
        }
        return newOrders
    }

    /**
     * Creates a new list of orders by combining two given lists.
     *
     * One of the ideas in genetic programming is to take two well performing individuals out of the pool and
     * creating a new individual using their characteristics. This function does this by copying some orders
     * from the first parent, and the remaining from the second parent:
     *
     * ```
     *      p1:         XXXXXXXX
     *      p2:         YYYYYYYY
     *                      ^ random point
     *      offspring:  XXXXYYYY
     * ```
     *
     * @param p1 the first parent.
     * @param p2 the second second.
     * @return *offspring* order list.
     */
    fun mate(p1: List<Order>, p2: List<Order>) : List<Order> {
        if (p1.size > 5 && p2.size > 5) {
            val value = ThreadLocalRandom.current().nextInt(1, 5)
            return p1.subList(0, value) + p2.subList(value, p2.lastIndex)
        } else {
            throw IllegalArgumentException("Cannot mate, not enough genetic material")
        }
    }
}

