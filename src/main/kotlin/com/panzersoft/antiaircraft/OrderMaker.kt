package com.panzersoft.antiaircraft

import com.panzersoft.antiaircraft.orders.AimOrder
import com.panzersoft.antiaircraft.orders.Order
import com.panzersoft.antiaircraft.orders.WaitOrder
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by mdozturk on 7/4/17.
 */

object OrderMaker {
    val opSize = Operands.values().size

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

    fun mate(p1: List<Order>, p2: List<Order>) : List<Order> {
        if (p1.size > 5 && p2.size > 5) {
            val value = ThreadLocalRandom.current().nextInt(1, 5)
            return p1.subList(0, value) + p2.subList(value, p2.lastIndex)
        } else {
            throw IllegalArgumentException("Cannot mate, not enough genetic material")
        }
    }
}

