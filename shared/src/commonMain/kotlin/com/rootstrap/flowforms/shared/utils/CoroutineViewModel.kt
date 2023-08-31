package com.rootstrap.flowforms.shared.utils

import kotlinx.coroutines.CoroutineScope

expect abstract class CoroutineViewModel() {
    val coroutineScope: CoroutineScope
    fun dispose()
    protected open fun onCleared()
}
