package com.panzersoft.antiaircraft.visual

import com.panzersoft.antiaircraft.GameParameters.aircraftSpeed
import com.panzersoft.antiaircraft.GameParameters.makeAircraft
import com.panzersoft.antiaircraft.GameParameters.realHeight
import com.panzersoft.antiaircraft.models.Aircraft
import com.panzersoft.antiaircraft.visual.GameObject
import java.awt.Color
import java.awt.Graphics

/**
 * Created by mdozturk on 7/4/17.
 */

class Aircraft() : GameObject() {
    internal var model = makeAircraft()

    override fun paintComponent(g: Graphics) {
        g.color = Color.BLACK
        val p = model.location.toPoint()
        g.fillRect(p.x, p.y, 6, 6)
    }

    /**
     * @param elapsedTime time elapsed in milliseconds
     */
    override fun move(elapsedTime: Long) {
        model.move(elapsedTime)
    }
}