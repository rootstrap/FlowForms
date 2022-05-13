package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FField(
    val id : String,
    val onValueChange : List<Validation> = mutableListOf()
) {
    private val _status = MutableStateFlow(FieldStatus())
    val status = _status.asStateFlow()

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
        return fieldStatus.code == CORRECT
    }

}
