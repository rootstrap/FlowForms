package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.ValidationResult

data class FieldStatus(
    val status : String = UNMODIFIED,
    val validationResults : List<ValidationResult> = emptyList()
)
