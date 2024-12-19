package com.ilya.core.util

import android.util.Log

fun logThrowable(exception: Throwable) {
    Log.e("throwable", "Exception: $exception")
    Log.e("throwable", "Cause: ${exception.cause}")
}

fun myLog(data: Any?) {
    Log.d("mytag", data.toString())
}
