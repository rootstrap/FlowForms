package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FlowField : a reactive field of a form, identified by it's ID.
 *
 * @property id field's ID.
 * @property validations list of validations to trigger when needed, like when the field's value
 * changes in the form, or the user takes off the focus of the field.
 */
open class FField(
    val id : String,
    val validations : List<Validation> = mutableListOf()
) {

    private val _status = MutableStateFlow(FieldStatus())

    /**
     * Flow with the field status. Initially it will be in an [UNMODIFIED] state.
     * As long as the validations are triggered, this flow will be updated based on the validations
     * results and parameters.
     *
     * For more information about the possible statuses check [FieldStatus]
     *
     */
    val status : Flow<FieldStatus> = _status.asStateFlow()

    /**
     * Trigger the validations associated on this Field in the order they were added.
     */
    fun triggerValidations() : Boolean {
        val validationResults = mutableListOf<ValidationResult>()
        val failedValResults = mutableListOf<ValidationResult>()

        var res : ValidationResult?
        for (validation in validations) {
            res = validation.validate()
            validationResults.add(res)
            if (res.resultId != CORRECT) {
                failedValResults.add(res)
                if (validation.failFast) {
                    break
                }
            }
        }

        val fieldStatus = when {
            failedValResults.isEmpty() -> FieldStatus(CORRECT)
            failedValResults.size == 1 -> FieldStatus(failedValResults.first().resultId, validationResults)
            else -> FieldStatus(INCORRECT, validationResults)
        }

        _status.value = fieldStatus
        return fieldStatus.code == CORRECT
    }

}
