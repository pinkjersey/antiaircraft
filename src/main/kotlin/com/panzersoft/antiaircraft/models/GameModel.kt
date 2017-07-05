package com.panzersoft.antiaircraft.models

import com.panzersoft.antiaircraft.GameParameters
import com.panzersoft.antiaircraft.visual.GameObject

/**
 * Created by mdozturk on 7/4/17.
 */

abstract class GameModel() {
    abstract fun move(elapsedTime: Long)
}