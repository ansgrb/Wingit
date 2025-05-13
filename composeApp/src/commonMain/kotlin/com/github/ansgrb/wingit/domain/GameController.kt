/*
 * Product Made by Anas Ghareib
 * Copyright (C) 2025 Anas Ghareib
 *
 * All rights reserved. This software and associated documentation files
 * (the "Software") are proprietary and confidential. Unauthorized copying,
 * distribution, modification, or use of this Software, via any medium,
 * is strictly prohibited without prior written permission from Anas Ghareib.
 *
 * This Software is provided "as is", without warranty of any kind, express
 * or implied, including but not limited to the warranties of merchantability,
 * fitness for a particular purpose, and non-infringement. In no event shall
 * the author be liable for any claim, damages, or other liability,
 * whether in an action of contract, tort, or otherwise, arising from,
 * out of, or in connection with the Software or the use of it.
 */
package com.github.ansgrb.wingit.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ObservableSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

data class GameController(
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val theWingedRadius: Float = 30f,
    val gravity: Float = 0.6f,
    val theWingedJumpImpulse: Float = -13f,
    val theWingedMaxVelocity: Float = 25f,
    val pipeWidth: Float = 150f,
    val pipeVelocity: Float = 5f,
    val pipeGapSize: Float = 250f,
): KoinComponent {
    //______mutable props_________
    private val setting: ObservableSettings by inject()
    var status by mutableStateOf(GameStatus.IDLE) // default status is IDLE
        private set
    var theWingedVelocity by mutableStateOf(0f)
        private set
    var winged by mutableStateOf(
        TheWinged(
            x =  screenWidth / 3.5f, // little bit left from the center of the screen
            y = screenHeight / 2f, // center of the screen
            radius = theWingedRadius // the radius of the winged
        )
    )
        private set
    var pipePairs = mutableStateListOf<PipePair>()
    //______status functions_________
    fun start() {
        status = GameStatus.STARTED
    }
    fun pause() {
        status = if (status == GameStatus.STARTED) GameStatus.PAUSED else GameStatus.STARTED
    }
    fun over() {
        status = GameStatus.OVER
    }
    //______game loop functions_________
    fun jump() {
        if (status == GameStatus.STARTED) {
            theWingedVelocity = theWingedJumpImpulse
        }
    }
    fun update() {
        pipePairs.forEach { pipePair ->
            if (checkCollision(pipePair = pipePair)) {
                over()
                return
            }
        }
        if (winged.y < 0f) {
            stopGame()
            return // we can return from here to avoid the game to go over the screen
        } else if (winged.y > screenHeight) {
            over()
            return
        }
        if (status == GameStatus.STARTED) {
            theWingedVelocity += gravity
            theWingedVelocity = theWingedVelocity.coerceAtMost(theWingedMaxVelocity)
            winged = winged.copy(y = winged.y + theWingedVelocity)
            if (winged.y > screenHeight) {
                over()
            }
        }
        spawnPipes()
    }

    private fun spawnPipes() {
        pipePairs.forEach { pipe ->
           pipe.x -= pipeVelocity
//           if (pipe.x < -pipeWidth) {
//               pipe.scored = true
//               pipePairs.remove(pipe)
//           }
        }
        pipePairs.removeAll { pipe -> pipe.x + pipeWidth < 0 }
        if (pipePairs.isEmpty() || pipePairs.last().x < screenWidth / 2) {
            val initialPipeX = screenWidth.toFloat() + pipeWidth
            val topHeight = Random.nextFloat() * (screenHeight /2)
            val bottomHeight = screenHeight - topHeight - pipeGapSize
            val newPipePair = PipePair(
                x = initialPipeX,
                y = topHeight + pipeGapSize / 2,
                topHeight = topHeight,
                bottomHeight = bottomHeight
            )
            pipePairs.add(newPipePair)
        }
    }
    private fun checkCollision(pipePair: PipePair): Boolean {
        // check ro a horizontal collision
        val theWingedRightEdge = winged.x + winged.radius
        val theWingedLeftEdge = winged.x - winged.radius
        val pipeRightEdge = pipePair.x + pipeWidth / 2
        val pipeLeftEdge = pipePair.x - pipeWidth / 2
        val horizontalCollection = theWingedRightEdge > pipeLeftEdge
                && theWingedLeftEdge < pipeRightEdge
        // check for the winged to be inside the pipe
        val theWingedTopEdge = winged.y - winged.radius
        val theWingedBottomEdge = winged.y + winged.radius
        val gapTopEdge = pipePair.y - pipeGapSize / 2
        val gapBottomEdge = pipePair.y + pipeGapSize / 2
        val wingInsideGap = theWingedTopEdge > gapTopEdge
                && theWingedBottomEdge < gapBottomEdge
        // return the result
        return horizontalCollection && !wingInsideGap
    }

    fun stopGame() {
//        status = GameStatus.OVER
        winged = winged.copy(y = 0f)
    }
    private fun reset() {
        status = GameStatus.IDLE
        theWingedVelocity = 0f
        winged = winged.copy(y = screenHeight / 2f)
    }
    fun restart() {
        reset()
        removePipes()
        start()
    }
    private fun removePipes() {
        pipePairs.clear()
    }
}
