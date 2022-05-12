package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FField(
    val id : String,
    val onValueChange : List<Validation> = mutableListOf()
) {
    private val _status = MutableStateFlow(FieldStatus())
    val status : Flow<FieldStatus>
        get() = _status

    fun triggerOnValueChangeValidations() : Boolean {
        val validationResults = mutableListOf<ValidationResult>()
        val failedValResults = mutableListOf<ValidationResult>()

        var res : ValidationResult?
        for (validation in onValueChange) {
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
        return fieldStatus.status == CORRECT
    }

}
