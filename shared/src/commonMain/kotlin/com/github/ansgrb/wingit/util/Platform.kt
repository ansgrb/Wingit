package com.github.ansgrb.wingit.util

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
    WEB
}

expect fun getPlatform(): Platform