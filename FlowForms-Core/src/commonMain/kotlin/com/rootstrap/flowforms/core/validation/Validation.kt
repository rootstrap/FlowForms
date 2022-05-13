package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT

/**
 * Used by [FField] to update their status given the sum of the validationResults
 *
 * @property failFast : if true it short-circuits the validation process of the field with the
 * [ValidationResult] returned by the [validate] call as it's current status **if it's not [CORRECT]**
 */
abstract class Validation(val failFast : Boolean = true) {

    /**
     * Represents a validation that must return a result when invoked.
     */
    abstract fun validate() : ValidationResult
}

/**
 * Result from executing a validation.
 *
 * @property resultId defines the result of the validation, being it [CORRECT], [INCORRECT], or a custom code.
 * @property extras extras allows to send additional information about the executed validation and it's result.
 */
data class ValidationResult(
    val resultId : String,
    val extras : Map<String, Any> = mutableMapOf()
) {
    companion object {

        /**
         * empty ValidationResult with [CORRECT] status code
         */
        val Correct = ValidationResult(CORRECT)
    }
}
