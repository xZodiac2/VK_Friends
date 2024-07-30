package com.ilya.core.appCommon.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import com.ilya.core.appCommon.base.Switch

fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean {
    return this.itemCount == 0
}

@Composable
@NonRestartableComposable
fun <T> rememberSwitch(first: T, second: T): Switch<T> {
    val switch = remember { Switch(first, second) }
    return switch
}

