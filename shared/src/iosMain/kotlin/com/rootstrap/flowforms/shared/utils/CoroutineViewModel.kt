package com.rootstrap.flowforms.shared.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class CoroutineViewModel {
    actual val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    actual fun dispose() {
        coroutineScope.cancel()
        onCleared()
    }

    protected actual open fun onCleared() {
    }
}
