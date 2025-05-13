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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class GameController(
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val gravity: Float = 0.6f,
    val theWingedJumpImpulse: Float = -13f,
    val theWingedMaxVelocity: Float = 25f
) {
    //______mutable props_________
    var status by mutableStateOf(GameStatus.IDLE) // default status is IDLE
        private set
    var theWingedVelocity by mutableStateOf(0f)
        private set
    var winged by mutableStateOf(
        TheWinged(
            x =  screenWidth / 3.5f, // little bit left from the center of the screen
            y = screenHeight / 2f // center of the screen
        )
    )
        private set
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
    }
    fun stopGame() {
//        status = GameStatus.OVER
        winged = winged.copy(y = 0f)
    }
    fun reset() {
        status = GameStatus.IDLE
        theWingedVelocity = 0f
        winged = winged.copy(
            x = screenWidth / 3.5f,
            y = screenHeight / 2f
        )
    }
}
