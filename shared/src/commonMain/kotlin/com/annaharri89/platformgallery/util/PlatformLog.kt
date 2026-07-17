package com.annaharri89.platformgallery.util

expect fun platformLogError(tag: String, message: String, throwable: Throwable? = null)

expect fun platformLogDebug(tag: String, message: String)
