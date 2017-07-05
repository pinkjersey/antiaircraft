package com.panzersoft.antiaircraft.orders

import com.panzersoft.antiaircraft.Operands

/**
 * Created by mdozturk on 7/4/17.
 */

class WaitOrder(val waitTime: NumOrder) : Order(Operands.WAIT) {

    /*override fun timeNeeded(): Int {
        if (waitTime.value < 0) { return 0 }
        return waitTime.value.toInt()
    }*/
}