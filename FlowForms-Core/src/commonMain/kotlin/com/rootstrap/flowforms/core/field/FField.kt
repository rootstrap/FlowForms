package com.rootstrap.flowforms.core.field

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
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
        var res : ValidationResult?
        var fieldValid = false

        onValueChange.forEach { validation ->
            res = validation.validate()
            res?.let {
                validationResults.add(it)
                if (it.resultId != CORRECT) {
                    fieldValid = false
                    if (validation.failFast) {
                        return@forEach
                    }
                }
            }
        }

        _status.value = FieldStatus(CORRECT)
        return fieldValid
    }

}
