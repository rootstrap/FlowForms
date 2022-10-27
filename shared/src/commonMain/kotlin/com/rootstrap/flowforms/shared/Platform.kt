package com.rootstrap.flowforms.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform