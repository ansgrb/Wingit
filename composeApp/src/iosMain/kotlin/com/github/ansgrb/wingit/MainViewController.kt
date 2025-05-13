package com.github.ansgrb.wingit

import androidx.compose.ui.window.ComposeUIViewController
import com.github.ansgrb.wingit.di.initKoin

fun MainViewController() = ComposeUIViewController(configure = { initKoin() }) { App() }