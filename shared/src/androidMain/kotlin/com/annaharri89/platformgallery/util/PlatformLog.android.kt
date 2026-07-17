package com.annaharri89.platformgallery.util

import android.util.Log

actual fun platformLogError(tag: String, message: String, throwable: Throwable?) {
    if (throwable != null) {
        Log.e(tag, message, throwable)
    } else {
        Log.e(tag, message)
    }
}

actual fun platformLogDebug(tag: String, message: String) {
    Log.d(tag, message)
}
