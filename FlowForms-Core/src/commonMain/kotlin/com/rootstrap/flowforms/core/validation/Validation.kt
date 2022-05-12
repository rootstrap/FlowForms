package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT

abstract class Validation(val failFast : Boolean = true) {
    abstract fun validate() : ValidationResult
}

data class ValidationResult(
    val resultId : String,
    val extras : Map<String, Any> = mutableMapOf()
) {
    companion object {
        val Correct = ValidationResult(CORRECT)
    }
}
