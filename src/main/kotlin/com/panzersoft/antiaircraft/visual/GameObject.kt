package com.panzersoft.antiaircraft.visual

import com.panzersoft.antiaircraft.GameParameters
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
    companion object {
        fun calcDistance(objects: Pair<GameObject.RealPoint, GameObject.RealPoint>) : Double {
            val diffx = objects.first.x - objects.second.x
            val diffy = objects.first.y - objects.second.y
            return Math.sqrt(Math.pow(diffx, 2.0) + Math.pow(diffy, 2.0))
        }

        fun offscreen(location: RealPoint) : Boolean {
            return (location.x > GameParameters.realWidth || location.x < 0.0 ||
                    location.y > GameParameters.realHeight || location.y < 0.0)
        }
    }

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