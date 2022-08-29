package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.field.FlowField

/**
 * Used by [FlowField] to update their status given the sum of the validationResults
 *
 * @param failFast Determines if this validation should short-circuit the validation process.
 * Defaults to true.
 * @param async Determines if the validation should be triggered asynchronously.
 * Defaults to false.
 */
abstract class Validation(val failFast : Boolean = true, val async : Boolean = false) {

    /**
     * Represents a validation that must return a result when invoked.
     */
    abstract suspend fun validate() : ValidationResult
}

/**
 * Result from executing a validation.
 *
 * @property resultId defines the result of the validation, being it [CORRECT], [INCORRECT], or a custom code.
 * @property extras extras allows to send additional information about the executed validation and it's result.
 */
data class ValidationResult(
    val resultId: String,
    val extras: Map<String, Any> = mutableMapOf()
) {
    companion object {

        /**
         * empty ValidationResult with [CORRECT] status code
         */
        val Correct = ValidationResult(CORRECT)

        /**
         * empty ValidationResult with [INCORRECT] status code
         */
        val Incorrect = ValidationResult(INCORRECT)
    }
}
