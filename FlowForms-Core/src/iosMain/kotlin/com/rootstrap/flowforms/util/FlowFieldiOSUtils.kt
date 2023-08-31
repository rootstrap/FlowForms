package com.rootstrap.flowforms.util

import com.rootstrap.flowforms.core.field.FieldDefinition
import com.rootstrap.flowforms.core.field.FieldStatus
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun FieldDefinition.onStatusChange(
    onStatusChange: (FieldStatus) -> Unit,
) {
    MainScope().launch {
        status.collect { fieldStatus ->
            onStatusChange(fieldStatus)
        }
    }
}
