package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MAX_LENGTH_UNSATISFIED

/**
 * Validation to use when the given string's length should be **lower** than an specific value.
 * It uses the valueProvider to receive the string at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] if the string is not null and contains at least the given
 * minLength amount of characters. Otherwise returns [MAX_LENGTH_UNSATISFIED].
 *
 * Examples with maxLength = 3 :
 *
 * - "   " (3 white spaces) = true
 * - "  " (2 white spaces) = true
 * - "    " (4 white spaces) = false
 * - " a " = true
 * - "four" = false
 *
 * If you don't want to include left and right whitespaces please call .trim() on the value provided.
 *
 * @param maxLength Indicates the maximum length the provided string can have, inclusive.
 * @param valueProvider function that returns the value used by the [validate] implementation.
 */
class MaxLength(private val maxLength: Int, val valueProvider: () -> String?) : Validation() {

    override suspend fun validate() : ValidationResult {
        val value = valueProvider() ?: return ValidationResult(MAX_LENGTH_UNSATISFIED)
        return ValidationResult(
            if (value.length <= maxLength)
                CORRECT
            else
                MAX_LENGTH_UNSATISFIED
        )
    }

}
