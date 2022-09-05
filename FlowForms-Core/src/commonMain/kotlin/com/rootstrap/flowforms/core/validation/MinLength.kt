package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MIN_LENGTH_UNSATISFIED

/**
 * Validation to use when the given string's length should be **greater** than an specific value.
 * It uses the valueProvider to receive the string at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] if the string is not null and contains at least the given
 * minLength amount of characters. Otherwise returns [MIN_LENGTH_UNSATISFIED].
 *
 * Examples with minLength = 3 :
 *
 * - "   " (3 white spaces) = true
 * - "  " (2 white spaces) = false
 * - " a " = true
 * - "four" = true
 *
 * If you don't want to include left and right whitespaces please call .trim() on the value provided.
 *
 * @param minLength Indicates the minimum length the provided string can have, inclusive.
 * @param failFast **Optional**, refer to failFast property on [Validation] class.
 * @param async **Optional**, refer to async property on [Validation] class.
 * @param valueProvider function that returns the value used by the [validate] implementation.
 */
class MinLength(
    private val minLength: Int,
    failFast : Boolean = true,
    async : Boolean = false,
    val valueProvider: () -> String?
) : Validation(failFast = failFast, async = async) {

    override suspend fun validate() : ValidationResult {
        val value = valueProvider() ?: return ValidationResult(MIN_LENGTH_UNSATISFIED)
        return ValidationResult(
            if (value.length >= minLength)
                CORRECT
            else
                MIN_LENGTH_UNSATISFIED
        )
    }

}
