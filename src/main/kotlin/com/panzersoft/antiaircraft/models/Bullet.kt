package com.panzersoft.antiaircraft.models

import com.panzersoft.antiaircraft.GameParameters
import com.panzersoft.antiaircraft.visual.GameObject
import java.lang.Math.toRadians

/**
 * Created by mdozturk on 7/4/17.
 */
data class Bullet(var location: GameObject.RealPoint, var speed: GameObject.RealPoint) : GameModel() {

    /**
     * @param elapsedTime time elapsed in milliseconds
     */
    override fun move(elapsedTime: Long) {
        location.x += speed.x * elapsedTime.toDouble() / 1000.0
        location.y += speed.y * elapsedTime.toDouble() / 1000.0
    }
}