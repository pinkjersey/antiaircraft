package com.panzersoft.antiaircraft.models

import com.panzersoft.antiaircraft.GameParameters
import com.panzersoft.antiaircraft.GameParameters.antiAircraftDegreesPerMilli
import com.panzersoft.antiaircraft.visual.GameObject
import java.awt.Color
import java.awt.Graphics

/**
 * Created by mdozturk on 7/4/17.
 */

data class AntiAircraft(var location: GameObject.RealPoint) : GameModel() {
    internal var angle = 0.0
    var targetAngle = angle
    var loaded = true
    internal val length = 100

    /**
     * @param elapsedTime time elapsed in milliseconds
     */
    override fun move(elapsedTime: Long) {
        if (targetAngle > angle) {
            angle += elapsedTime * antiAircraftDegreesPerMilli
            if (angle > targetAngle) {
                angle = targetAngle
            }
        } else if (targetAngle < angle) {
            angle -= elapsedTime * antiAircraftDegreesPerMilli
            if (angle < targetAngle) {
                angle = targetAngle
            }
        }
        if (angle >= 360 || angle < 0.0) { angle = angle % 360 }
    }
}