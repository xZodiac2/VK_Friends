package com.ilya.core.appCommon.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember


class Switch<T>(
    private val first: T,
    private val second: T
) : Toggleable<T> {

    private var prevValue = first

    override fun toggle(): T {
        prevValue = if (prevValue == first) second else first
        return prevValue
    }

    override fun last(): T {
        return prevValue
    }

}

@Composable
@NonRestartableComposable
fun <T> rememberSwitch(first: T, second: T): Switch<T> {
    val switch = remember { Switch(first, second) }
    return switch
}
