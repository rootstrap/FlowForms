package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.ValidationResult

/**
 * Represents the current state of a field.
 *
 * By default every fieldStatus starts at [UNMODIFIED]
 *
 * @property code The status of the field. Being it [UNMODIFIED], [CORRECT], [INCORRECT] or a custom error code
 * @property validationResults : the list of validations with their results, triggered to reach this status.
 */
data class FieldStatus(
    val code : String = UNMODIFIED,
    val validationResults : List<ValidationResult> = emptyList()
)
