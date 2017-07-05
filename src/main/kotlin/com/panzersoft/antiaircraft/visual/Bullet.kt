package com.panzersoft.antiaircraft.visual

import com.panzersoft.antiaircraft.GameParameters
import com.panzersoft.antiaircraft.visual.GameObject
import java.awt.Color
import java.awt.Graphics

/**
 * Created by mdozturk on 7/4/17.
 */

class Bullet() : GameObject() {
    internal var hidden = true
    internal var location = RealPoint(0.0, 0.0)
    internal var speed = RealPoint(178.0, 0.0)

    fun reset(angle: Double) {
        println("Setting bullet angle= ${angle}")
        val aa = GameParameters.makeAntiAircraft()
        location = aa.location
        speed = RealPoint(GameParameters.bulletSpeed * Math.cos(Math.toRadians(angle)),
        GameParameters.bulletSpeed * Math.sin(Math.toRadians(angle)))
        hidden =false
    }

    override fun paintComponent(g: Graphics) {
        if (!hidden) {
            g.color = Color.BLACK
            val p = location.toPoint()
            g.fillRect(p.x, p.y, 6, 6)
        }
    }

    /**
     * @param elapsedTime time elapsed in milliseconds
     */
    override fun move(elapsedTime: Long) {
        if (!hidden) {

            location.x += speed.x * elapsedTime.toDouble() / 1000.0
            location.y += speed.y * elapsedTime.toDouble() / 1000.0

            //println("moving bullet ${location.x} ${location.y}")
            if (offscreen(location)) {
                hidden = true
            }
        }
    }
}