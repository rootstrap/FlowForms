package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.ValidationResult

/**
 * Represents the current state of a field.
 *
 * By default every fieldStatus starts at [UNMODIFIED]
 *
 * @param code The status of the field. Being it [UNMODIFIED], [INCOMPLETE], [CORRECT],
 * [INCORRECT], [IN_PROGRESS] or a custom error code
 * @param validationResults The list of validations with their results, triggered to reach this status.
 */
data class FieldStatus(
    val code : String = UNMODIFIED,
    val validationResults : List<ValidationResult> = emptyList()
)
