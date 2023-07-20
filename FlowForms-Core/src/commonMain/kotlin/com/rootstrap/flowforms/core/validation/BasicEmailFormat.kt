package com.rootstrap.flowforms.core.validation

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rootstrap.flowforms.core.common.StatusCodes.BASIC_EMAIL_FORMAT_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_REGEX_UNSATISFIED
import com.rootstrap.flowforms.core.validation.BasicEmailFormat.Companion.BASIC_EMAIL_REGEX

/**
 * Validation to use when the value's format should match a [basic email format][BASIC_EMAIL_REGEX].
 * It uses the valueProvider to receive the value at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] when the provided value's format matches a [basic email
 * format][BASIC_EMAIL_REGEX]. Otherwise returns [BASIC_EMAIL_FORMAT_UNSATISFIED].
 *
 * @param failFast **Optional**, refer to failFast property on [Validation] class.
 * @param async **Optional**, refer to async property on [Validation] class.
 * @param valueProvider function that returns the values used by the [validate] implementation.
 */
class BasicEmailFormat(
    failFast : Boolean = true,
    async : Boolean = false,
    private val valueProvider: () -> String?
) : MatchRegex(BASIC_EMAIL_REGEX, failFast = failFast, async = async, valueProvider = valueProvider) {

    companion object {

        /**
         * Regular expression that defines a character sequence with upper or lower case characters,
         * numbers, or some symbols (+._%-) preceding an @ symbol with a maximum of 256 characters,
         * followed by at least one lower or upper case character, number or the "-" symbol,
         * with a maximum of 64 characters, followed by a dot and the same sequence pattern but
         * with a limit of 25 characters.
         */
        private val BASIC_EMAIL_REGEX = (
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
                ).toRegex()
    }

    @NativeCoroutines
    override suspend fun validate() = super.validate().run {
        ValidationResult(
            if (resultId == MATCH_REGEX_UNSATISFIED) BASIC_EMAIL_FORMAT_UNSATISFIED
            else resultId
        )
    }

}
