package com.github.ansgrb.wingit

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
    WEB
}

expect fun getPlatform(): Platform