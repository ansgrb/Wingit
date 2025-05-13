package com.github.ansgrb.wingit

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.ansgrb.wingit.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Wingit",
    ) {
        App()
    }
}