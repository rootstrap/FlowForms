package com.rootstrap.flowforms.shared.utils

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class CoroutineViewModel {

    private val readableExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println(throwable.stackTraceToString())
    }

    actual val coroutineScope = CoroutineScope(
        context = Dispatchers.Main + SupervisorJob() + readableExceptionHandler
    )

    actual fun dispose() {
        coroutineScope.cancel()
        onCleared()
    }

    protected actual open fun onCleared() {
    }
}
