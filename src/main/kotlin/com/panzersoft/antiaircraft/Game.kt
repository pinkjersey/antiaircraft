package com.panzersoft.antiaircraft

import com.panzersoft.antiaircraft.GameParameters.tickSize
import com.panzersoft.antiaircraft.visual.Aircraft
import com.panzersoft.antiaircraft.visual.AntiAircraft
import com.panzersoft.antiaircraft.visual.Bullet
import com.panzersoft.antiaircraft.visual.GameObject
import java.awt.BorderLayout
import javax.swing.JFrame
import java.awt.*;
import javax.swing.JPanel



/**
 * Created by mdozturk on 7/4/17.
 */

class Game(val commander: Commander) {
    val aircraft = Aircraft()
    val antiAircraft = AntiAircraft(commander)
    val bullet = Bullet()
    val objects = listOf(aircraft, antiAircraft, bullet)
    internal val frame = JFrame("Anti-Aircraft")
    internal val drawPanel = DrawPanel(objects)

    internal inner class DrawPanel(val objects: List<GameObject>) : JPanel() {
        override fun paintComponent(g: Graphics) {
            g.color = Color.WHITE
            g.fillRect(0, 0, this.width, this.height)
            objects.forEach {
                it.paintComponent(g)
            }

        }
    }

    init {
        drawPanel.preferredSize = Dimension(600, 300)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(BorderLayout.CENTER, drawPanel)
        frame.isVisible = true
        frame.isResizable = false
        //frame.size = Dimension(600, 300)
        frame.location = Point(375, 55)
        frame.pack()
    }

    fun run() {

        while(true){
            objects.forEach {
                it.move(tickSize)
            }
            Thread.sleep(tickSize)
            frame.repaint();

        }
    }
}