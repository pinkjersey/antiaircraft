package com.panzersoft.antiaircraft

import com.panzersoft.antiaircraft.GameParameters.makeAircraft
import com.panzersoft.antiaircraft.GameParameters.makeAntiAircraft
import com.panzersoft.antiaircraft.models.Bullet
import com.panzersoft.antiaircraft.orders.AimOrder
import com.panzersoft.antiaircraft.orders.Order
import com.panzersoft.antiaircraft.orders.WaitOrder
import com.panzersoft.antiaircraft.visual.GameObject
import com.panzersoft.antiaircraft.visual.GameObject.Companion.calcDistance

/**
 * Creates events from orders.
 *
 * Orders are actions taken by commanders, events are visual changes that occur because of those orders.
 */

object EventMaker {
    enum class EventType {
        AIM,
        FIRE,
        WAIT
    }

    data class Event(val type: EventType, val angle: Double, val timeToComplete: Long, var start: Long, var end: Long) {
        constructor(type: EventType, angle: Double, timeToComplete: Long, start: Long) :
                this(type, angle, timeToComplete, start, start + timeToComplete)
    }

    fun calcBulletTime(lastAngle: Double, time: Long) : Long {
        val aircraft = makeAircraft()
        val antiAircraft = makeAntiAircraft()
        aircraft.move(time)
        var bullet = Bullet(GameObject.RealPoint(
                antiAircraft.location.x,
                antiAircraft.location.y),
                GameObject.RealPoint(GameParameters.bulletSpeed * Math.cos(Math.toRadians(lastAngle)),
                        GameParameters.bulletSpeed * Math.sin(Math.toRadians(lastAngle))))

        var mindist = calcDistance(Pair(aircraft.location, bullet.location))
        var time = 0L

        while (true) {
            bullet.move(1)
            aircraft.move(1)
            ++time
            val dist = calcDistance(Pair(aircraft.location, bullet.location))
            if (dist < mindist) {
                mindist = dist
            }
            if (mindist < 5.0) {
                // hit!
                return time
            }
            if (bullet.location.x > GameParameters.realWidth || bullet.location.x < 0.0 || bullet.location.y > GameParameters.realHeight) {
                // out of bounds
                return time
            }
        }
    }

    fun make(orders: List<Order>) : List<Event> {
        var time = 0L
        var lastAngle = 0.0
        var loaded = true

        val intermediate: List<Event?> = orders.map {
            if (time < 10000) { // 10,000 milliseconds = 10 seconds
                val ut = time
                // consider orders only for 10 seconds
                val ev: Event? = when (it.op) {
                    Operands.FIRE -> {
                        if (loaded) {
                            val bulletOutOfRangeTime = calcBulletTime(lastAngle, time)
                            time += bulletOutOfRangeTime
                             loaded = false
                            Event(EventType.FIRE, lastAngle, bulletOutOfRangeTime, ut)

                        } else {
                            time += 1
                            Event(EventType.WAIT, 0.0, 1, ut)
                        }
                    }
                    Operands.WAIT -> {
                        val wait: WaitOrder = it as WaitOrder
                        if (wait.waitTime.value > 0.0) {
                            val waitTime = (wait.waitTime.value * 10000).toLong()
                            time += waitTime
                            Event(EventType.WAIT, 0.0, waitTime, ut)
                        }
                        else {
                            time += 1
                            Event(EventType.WAIT, 0.0, 1, ut)

                        }
                    }
                    Operands.AIM -> {
                        var aim = it as AimOrder
                        if (aim.angle.value < 0.0 || aim.angle.value > 180.0) {
                            time += 1
                            Event(EventType.AIM, 0.0, 1, ut)
                        } else {
                            val diff = Math.abs(lastAngle - aim.acceptable)
                            val millisNeeded = (diff / GameParameters.antiAircraftDegreesPerMilli).toLong()
                            time += millisNeeded
                            lastAngle = aim.acceptable
                            Event(EventType.AIM, lastAngle, millisNeeded, ut)
                        }
                    }
                    Operands.RELOAD -> {
                        if (loaded == false) {
                            loaded = true
                            time += GameParameters.reloadTimeInMillis
                            Event(EventType.WAIT, 0.0, GameParameters.reloadTimeInMillis, ut)

                        } else {
                            val ut = time
                            time += 1
                            Event(EventType.WAIT, 0.0, 1, ut)
                        }
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported operand in simulation")
                    }
                }
                ev
            } else {
                null
            }
        }
        val ret = intermediate.filterNotNull()
        return ret
    }
}