package com.panzersoft.antiaircraft

import com.panzersoft.antiaircraft.GameParameters.antiAircraftDegreesPerMilli
import com.panzersoft.antiaircraft.GameParameters.makeAircraft
import com.panzersoft.antiaircraft.GameParameters.makeAntiAircraft
import com.panzersoft.antiaircraft.GameParameters.realHeight
import com.panzersoft.antiaircraft.GameParameters.realWidth
import com.panzersoft.antiaircraft.GameParameters.reloadTimeInMillis
import com.panzersoft.antiaircraft.OrderMaker.mate
import com.panzersoft.antiaircraft.models.Aircraft
import com.panzersoft.antiaircraft.models.AntiAircraft
import com.panzersoft.antiaircraft.models.Bullet
import com.panzersoft.antiaircraft.orders.AimOrder
import com.panzersoft.antiaircraft.orders.Order
import com.panzersoft.antiaircraft.orders.WaitOrder
import com.panzersoft.antiaircraft.visual.GameObject
import com.panzersoft.antiaircraft.visual.GameObject.Companion.calcDistance
import java.lang.Math.*


/**
 * Implements the AA gun commander.
 *
 * The commander is the individual that controls the AA gun and is trying to shoot down the plane flying overhead.
 *
 * There are a couple of actions the commander can take, he/she can:
 * * wait
 * * aim
 * * fire and
 * * reload
 *
 * @property orders a list of command the AA gun will execute.
 * @property type the creation history of this commander. R is random, M is mutant, (X<->Y) is a child
 * @constructor secondary constructor that constructs a new commander my mating two parents.
 *
 */

class Commander(val orders: List<Order>, val type: String) {
    constructor(p1: Commander, p2: Commander) :
            this(mate(p1.orders, p2.orders), "(" + p1.type + "<->" + p2.type + ")")

    /**
     * Used by [fire] as a return value.
     *
     * @property time elapsed time before either the bullet hits the target or goes outside of range
     * @property dist closest distance the bullet gets to the plane.
     */
    data class TimeAndDistance(val time: Long, val dist: Double)

    /**
     * Used by [fire] when the gun is fired while empty.
     */
    class NotLoadedException : Exception("Anti-Aircraft gun not loaded")

    /**
     * Mutates this commander to make a new one.
     *
     * The [OrderMaker] is used to mutate the order of the current commander to create a new one.
     *
     * @return the mutated commander.
     */
    fun mutate() : Commander {
        return Commander(OrderMaker.mutate(orders), type + "M")
    }

    /**
     * Creates an offspring of this commander using the given commander.
     *
     * @param other the other parent.
     * @return the child commander.
     */
    fun offspring(other: Commander) : Commander {
        return Commander(this, other)
    }

    /**
     * Simulates the firing of the gun.
     *
     * This function steps through time 1 millisecond at a time and calculates the distance between the bullet and
     * the aircraft. If the distance is less than 5 meters, the function stops, returning the distance and the
     * time it took to get there.
     *
     * If the bullet misses, then the function returns the time it took for the bullet to go out of range and the
     * minimum distance between the bullet and the aircraft.
     *
     * @param aircraft the current state of the aircraft.
     * @param antiAircraft the current state of the AA gun.
     * @return results of the firing, see details above.
     */
    fun fire(aircraft: Aircraft, antiAircraft: AntiAircraft) : TimeAndDistance {
        if (antiAircraft.loaded) {
            antiAircraft.loaded = false
            val bullet = Bullet(GameObject.RealPoint(
                    antiAircraft.location.x,
                    antiAircraft.location.y),
                    GameObject.RealPoint(GameParameters.bulletSpeed * Math.cos(Math.toRadians(antiAircraft.angle)),
                            GameParameters.bulletSpeed * Math.sin(Math.toRadians(antiAircraft.angle))))
            val airCopy = aircraft.copy()
            var mindist = calcDistance(Pair(airCopy.location, bullet.location))
            var time = 0L

            while (true) {
                bullet.move(1)
                airCopy.move(1)
                ++time
                val dist = calcDistance(Pair(airCopy.location, bullet.location))
                if (dist < mindist) {
                    mindist = dist
                }
                if (mindist < 5.0) {
                    // hit!
                    return TimeAndDistance(time, mindist)
                }
                if (bullet.location.x > realWidth || bullet.location.x < 0.0 || bullet.location.y > realHeight) {
                    // out of bounds
                    return TimeAndDistance(time, mindist)
                }
            }
        } else {
            throw NotLoadedException()
        }
    }

    /**
     * Utility function that prints the given string only if the boolean is set.
     *
     * A simple logging utility.
     *
     * @param str the string to print.
     * @param doit whether or not the string should be printed.
     */
    fun print(str: String, doit: Boolean) {
        if (doit) {
            println(str)
        }
    }

    /**
     * Calculates the fitness of a given list of orders.
     *
     * This function simulates the given orders and gives points for good behavior and makes deductions for bad ones.
     *
     * Only 10 seconds worth of orders are considered and the remaining are thrown away.
     *
     * @param printInfo whether or not to log information about the fitness function to the console
     * @return the fitness value.
     */
    fun fitness(printInfo: Boolean): Double {
        val aircraft = makeAircraft()
        val antiAir = makeAntiAircraft()
        print("num orders ${orders.size}, type $type", printInfo)
        var time = 0L
        var fitness = 0.0
        var priorAim = false
        orders.forEach {
            if (time < 10000) { // 10,000 milliseconds = 10 seconds
                print("current time $time", printInfo)
                if (priorAim && it.op != Operands.AIM) {
                    priorAim = false // use this flag to not give additional points for multiple aims
                }

                // consider orders only for 10 seconds
                when (it.op) {
                    Operands.FIRE -> {
                        try {
                            val timeAndDistance = fire(aircraft, antiAir)
                            if (timeAndDistance.dist < 5.0) {// hit!
                                print("HIT!", printInfo)
                                // great job, 100%
                                fitness += 1000.0
                            } else {
                                val maxDistance = 500
                                if (timeAndDistance.dist > 500) {
                                    val extra = 5 - ((timeAndDistance.dist - 500.0) / 100.0).toLong()
                                    print("Fired, but missed by a lot (${timeAndDistance.dist}) $extra point", printInfo)
                                    fitness += extra
                                } else {
                                    val points = -499.0 / maxDistance * timeAndDistance.dist + 500.0
                                    print("An attempt was made $timeAndDistance.dist / $maxDistance Points= $points", printInfo)
                                    fitness += points
                                }
                            }
                            aircraft.move(timeAndDistance.time)
                            antiAir.move(timeAndDistance.time)
                            time += timeAndDistance.time
                        }
                        catch (exp: NotLoadedException) {
                            print("deducting 5 points for firing without reloading", printInfo)
                            fitness -= 5.0
                            aircraft.move(1)
                            antiAir.move(1)
                            time += 1
                        }
                    }
                    Operands.WAIT -> {
                        val wait: WaitOrder = it as WaitOrder
                        if (wait.waitTime.value > 0.0) {
                            print("Waiting ${wait.waitTime.value} seconds", printInfo)
                            val waitTime = (wait.waitTime.value * 10000).toLong()
                            aircraft.move(waitTime)
                            antiAir.move(waitTime)
                            time += waitTime
                        }
                        else {
                            print("deducting 5 points for waiting an invalid value", printInfo)
                            fitness -= 5.0
                            aircraft.move(1)
                            antiAir.move(1)
                            time += 1
                        }
                    }
                    Operands.AIM -> {
                        val aim = it as AimOrder
                        if (aim.angle.value < 0.0 || aim.angle.value > 180.0) {
                            print("deducting 5 points for changing angle to an invalid value", printInfo)
                            fitness -= 5.0
                            aircraft.move(1)
                            antiAir.move(1)
                            time += 1
                        } else {
                            print("Current angle ${antiAir.angle}, changing to ${aim.acceptable}", printInfo)
                            val diff = abs(antiAir.angle - aim.acceptable)
                            val millisNeeded = (diff / antiAircraftDegreesPerMilli).toLong()
                            if (!priorAim) {
                                fitness += 1.0
                                priorAim = true
                            }
                            antiAir.targetAngle = aim.acceptable
                            aircraft.move(millisNeeded)
                            antiAir.move(millisNeeded)
                            time += millisNeeded
                        }
                    }
                    Operands.RELOAD -> {
                        if (!antiAir.loaded) {
                            antiAir.loaded = true
                            print("awarding 2 points for reloading", printInfo)
                            fitness += 2.0
                            aircraft.move(reloadTimeInMillis)
                            antiAir.move(reloadTimeInMillis)
                            time += reloadTimeInMillis
                        } else {
                            print("already loaded, deducting 1 points for unncessary reloading", printInfo)
                            fitness -= 1.0
                            aircraft.move(1)
                            antiAir.move(1)
                            time += 1
                        }
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported operand in simulation")
                    }

                }
            }
        }
        return fitness
    }
}