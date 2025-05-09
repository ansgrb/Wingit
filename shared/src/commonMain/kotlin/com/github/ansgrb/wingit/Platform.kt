package com.github.ansgrb.wingit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform