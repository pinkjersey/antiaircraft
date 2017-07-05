package com.panzersoft.antiaircraft.orders

import com.panzersoft.antiaircraft.visual.AntiAircraft
import com.panzersoft.antiaircraft.GameParameters.antiAircraftDegreesPerTick
import com.panzersoft.antiaircraft.GameParameters.tickSize
import com.panzersoft.antiaircraft.Operands
import java.lang.Math.abs

/**
 * Created by mdozturk on 7/4/17.
 */

class AimOrder(val angle: NumOrder) : Order(Operands.AIM) {
    val acceptable = if (angle.value > 180.0) { 180.0 } else if (angle.value < 0.0) { 0.0 } else { angle.value }
    /*override fun timeNeeded(): Int {
        val acceptable = if (angle.value > 180.0) { 180.0 } else if (angle.value < 0) { 0.0 } else { angle.value }
        val diff = abs(antiAircraft.angle - acceptable)
        val ticksNeeded = diff / antiAircraftDegreesPerTick
        return (ticksNeeded * tickSize.toDouble()).toInt()
    }*/
}