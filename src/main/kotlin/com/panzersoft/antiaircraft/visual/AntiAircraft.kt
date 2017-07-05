package com.panzersoft.antiaircraft.visual
import com.panzersoft.antiaircraft.Commander
import com.panzersoft.antiaircraft.GameParameters
import com.panzersoft.antiaircraft.GameParameters.realWidth
import com.panzersoft.antiaircraft.visual.GameObject
import java.awt.Color
import java.awt.Graphics
import java.lang.Math.*

/**
 * Created by mdozturk on 7/4/17.
 */

class AntiAircraft(val commander: Commander) : GameObject() {
    internal var model = GameParameters.makeAntiAircraft()
    internal var location = RealPoint(realWidth / 2, 0.0)
    internal var angle = 45.0
    var targetAngle = 60
    internal val length = 100

    fun endPoint() : RealPoint {
        val rad = toRadians(angle);
        val ret = RealPoint(location.x + length * cos(rad),
                location.y + length * sin(rad))
        return ret
    }

    override fun paintComponent(g: Graphics) {
        g.color = Color.RED
        val p = location.toPoint()
        val p2 = endPoint().toPoint()
        g.drawLine(p.x, p.y, p2.x, p2.y)
    }

    /**
     * @param elapsedTime time elapsed in milliseconds
     */
    override fun move(elapsedTime: Long) {
        if (targetAngle > angle) {
            angle += 1
        } else if (targetAngle < angle) {
            angle -= 1
        }
        if (angle >= 360) { angle -= 360 }
    }
}