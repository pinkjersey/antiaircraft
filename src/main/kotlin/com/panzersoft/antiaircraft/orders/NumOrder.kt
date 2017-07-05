package com.panzersoft.antiaircraft.orders

import com.panzersoft.antiaircraft.Operands
import java.util.concurrent.ThreadLocalRandom



/**
 * Created by mdozturk on 7/4/17.
 */

class NumOrder(val value: Double) : Order(Operands.NUM) {
    //val value = ThreadLocalRandom.current().nextDouble(-10.0, 10.0)

    fun mutate() : NumOrder {
        val bump = ThreadLocalRandom.current().nextDouble(-5.0, 5.0)
        return NumOrder(value + bump)
    }
}