package com.annaharri89.platformgallery.util

actual fun platformLogError(tag: String, message: String, throwable: Throwable?) {
    if (throwable != null) {
        println("$tag: $message — ${throwable.message}")
        throwable.printStackTrace()
    } else {
        println("$tag: $message")
    }
}

actual fun platformLogDebug(tag: String, message: String) {
    println("$tag: $message")
}
