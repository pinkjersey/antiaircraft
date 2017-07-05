package com.panzersoft.antiaircraft

import java.util.concurrent.ThreadLocalRandom

/**
 * Created by mdozturk on 7/4/17.
 */

fun newGeneration(prior: List<Commander>, order: List<Pair<Int, Double>>) : List<Commander> {
    val best = order.subList(0, 10).map {
        prior[it.first]
    }
    val children = 0.rangeTo(39).map {
        val p1 = ThreadLocalRandom.current().nextInt(0, 10)
        val p2 = ThreadLocalRandom.current().nextInt(0, 10)
        val parent1 = prior[p1]
        val offspring: Commander =
                if (p1 != p2) { parent1.offspring(prior[p2]) } else { parent1.offspring(prior[p2+1]) }
        offspring
    }
    val mutants = order.subList(0, 10).map {
        prior[it.first].mutate()
    }
    val random = 0.rangeTo(39).map {
        Commander(OrderMaker.make(), "R")
    }
    return best + children + mutants + random
}

fun main(args: Array<String>) {
    var obest = 0.0
    var obestSet = false
    var generation = 0.rangeTo(99).map {
        Commander(OrderMaker.make(), "R")
    }
    val numGenerations = 3
    0.rangeTo(numGenerations).map {

        if (obestSet) {
            println("*** Generation $it Number to beat $obest ***")
        } else {
            println("*** Generation $it ***")
        }

        val fitness = generation.withIndex().map {
            Pair(it.index, it.value.fitness(false))
        }
        val sorted = fitness.sortedWith(compareByDescending {it.second})
        val best = generation[sorted.first().first]
        val bfit = best.fitness(true)
        println("Best Fitness ${bfit}")
        if (bfit > obest) {
            obestSet = true
            obest = bfit
        }

        generation = newGeneration(generation, sorted)
    }
    val fitness = generation.withIndex().map {
        Pair(it.index, it.value.fitness(false))
    }
    val sorted = fitness.sortedWith(compareByDescending {it.second})
    val best = generation[sorted.first().first]
    val g = Game(best)
    g.run()
}