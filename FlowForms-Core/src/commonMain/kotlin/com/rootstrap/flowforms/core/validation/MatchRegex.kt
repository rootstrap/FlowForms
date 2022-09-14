package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_REGEX_UNSATISFIED

/**
 * Validation to use when the value should match the given regular expression. It uses the valueProvider to
 * receive the value at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] when the provided value matches the given regular
 * expression. Otherwise returns [MATCH_REGEX_UNSATISFIED].
 *
 * @param regex Regular expression to match the string given by the valueProvider.
 * @param failFast **Optional**, refer to failFast property on [Validation] class.
 * @param async **Optional**, refer to async property on [Validation] class.
 * @param valueProvider function that returns the values used by the [validate] implementation.
 */
open class MatchRegex(
    private val regex: Regex,
    failFast : Boolean = true,
    async : Boolean = false,
    private val valueProvider: () -> String?
) : Validation(failFast = failFast, async = async) {

    override suspend fun validate() : ValidationResult {
        val stringToMatch = valueProvider() ?: return ValidationResult(MATCH_REGEX_UNSATISFIED)
        return ValidationResult(
            if (regex.matches(stringToMatch))
                CORRECT
            else
                MATCH_REGEX_UNSATISFIED
        )
    }

}
