package com.panzersoft.antiaircraft

import com.panzersoft.antiaircraft.models.Aircraft
import com.panzersoft.antiaircraft.models.AntiAircraft
import com.panzersoft.antiaircraft.visual.GameObject

/**
 * Created by mdozturk on 7/4/17.
 */

object GameParameters {
    val tickSize = 10L // in milliseconds
    val panelWidth = 600
    val panelHeight = 300
    val realWidth = 1780.0
    val realHeight = realWidth * panelHeight.toDouble() / panelWidth.toDouble()
    val aircraftSpeed = 178.8
    val bulletSpeed = 400
    val antiAircraftDegreesPerMilli = 0.18
    val antiAircraftDegreesPerTick = antiAircraftDegreesPerMilli * 10.0

    val reloadTimeInMillis = 4000L

    fun makeAircraft() : Aircraft {
        return Aircraft(GameObject.RealPoint(0.0, GameParameters.realHeight * .8),
                GameObject.RealPoint(GameParameters.aircraftSpeed, 0.0))
    }

    fun makeAntiAircraft() : AntiAircraft {
        return AntiAircraft(GameObject.RealPoint(GameParameters.realWidth / 2, 0.0))
    }
}