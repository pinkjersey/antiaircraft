package com.panzersoft.antiaircraft.visual

import com.panzersoft.antiaircraft.visual.GameObject
import java.awt.Color
import java.awt.Graphics

/**
 * Created by mdozturk on 7/4/17.
 */

class Bullet() : GameObject() {
    internal var location = RealPoint(0.0, 712.0)
    internal var speed = RealPoint(178.0, 0.0)

    override fun paintComponent(g: Graphics) {
        g.color = Color.BLACK
        val p = location.toPoint()
        g.fillRect(p.x, p.y, 6, 6)
    }

    /**
     * @param elapsedTime time elapsed in milliseconds
     */
    override fun move(elapsedTime: Long) {
        location.x += speed.x * elapsedTime.toDouble() / 1000.0
        location.y += speed.y * elapsedTime.toDouble() / 1000.0
    }
}