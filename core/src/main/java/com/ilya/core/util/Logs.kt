package com.ilya.core.util

import android.util.Log

fun logThrowable(exception: Throwable) {
    Log.d("throwable", "Exception: $exception")
    Log.d("throwable", "Message: ${exception.message}")
    Log.d("throwable", "Cause: ${exception.cause}")
}