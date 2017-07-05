package com.panzersoft.antiaircraft.visual

import com.panzersoft.antiaircraft.GameParameters.panelHeight
import com.panzersoft.antiaircraft.GameParameters.panelWidth
import com.panzersoft.antiaircraft.GameParameters.realHeight
import com.panzersoft.antiaircraft.GameParameters.realWidth
import java.awt.Graphics
import java.awt.Point

/**
 * Created by mdozturk on 7/4/17.
 */

abstract class GameObject {
    data class RealPoint(var x: Double, var y: Double) {
        /**
         * Converts real location to panel location. Top left corner of the panel is 0,0 which corresponds to
         * real world coordinates 0, realHeight. Bottom right corner of the panel is 600, 300 which corresponds tp
         * real world coordinates realWidth, 0
         */
        fun toPoint() : Point {
            val xp: Int = (x * panelWidth.toDouble() / realWidth).toInt()
            val yp: Int =
                    (y * -1 * panelHeight.toDouble() / realHeight + panelHeight.toDouble()).toInt()
            return Point(xp, yp)
        }
    }

    abstract fun paintComponent(g: Graphics)
    abstract fun move(elapsedTime: Long)
}