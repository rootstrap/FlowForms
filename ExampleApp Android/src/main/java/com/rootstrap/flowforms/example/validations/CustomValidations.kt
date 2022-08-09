package com.rootstrap.flowforms.example.validations

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED
import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.MIN_LENGTH_UNSATISFIED
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.PASSWORD_MATCH_UNSATISFIED


class RequiredTrue(val valueProvider : () -> Boolean?) : Validation() {

    override suspend fun validate() = ValidationResult(
        if (valueProvider() == true)
            CORRECT
        else
            REQUIRED_UNSATISFIED
    )
}

class MinLength(private val minLength: Int, val valueProvider : () -> String?) : Validation() {

    override suspend fun validate() = ValidationResult(
        if ((valueProvider()?.length ?: 0) >= minLength)
            CORRECT
        else
            MIN_LENGTH_UNSATISFIED
    )
}

class Match(val valueProvider : () -> Pair<String?, String?>) : Validation() {

    override suspend fun validate() : ValidationResult {
        val value = valueProvider()

        return ValidationResult(
            if (value.first == value.second)
                CORRECT
            else
                PASSWORD_MATCH_UNSATISFIED
        )
    }
}

class ValidEmail(val valueProvider : () -> String?) : Validation() {

    override suspend fun validate() = ValidationResult(
        if (valueProvider()?.isEmail() == true)
            CORRECT
        else
            INVALID_EMAIL
    )

    private fun String.validate(pattern: String): Boolean {
        return pattern.toRegex().matches(this)
    }

    private fun String.isEmail(): Boolean {
        return validate(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
    }

    companion object {
        const val INVALID_EMAIL = "invalid_email"
        const val MIN_LENGTH_UNSATISFIED = "min_length_unsatisfied"
        const val PASSWORD_MATCH_UNSATISFIED = "password_match_unsatisfied"
    }
}
