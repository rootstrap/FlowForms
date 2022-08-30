package com.rootstrap.flowforms.example

import com.rootstrap.flowforms.core.validation.Validation
import com.rootstrap.flowforms.core.validation.ValidationResult

class EmailDoesNotExistInRemoteStorage(
    failFast : Boolean = true,
    async : Boolean = false,
    private val emailProvider : () -> String?
) : Validation(failFast = failFast, async = async) {

    override suspend fun validate(): ValidationResult {
        val email = emailProvider() ?: return ValidationResult(EMAIL_ALREADY_EXIST)
        return when (verifyEmailInRemoteStorage(email)) {
            is EmailExistenceResponse.EmailExist -> ValidationResult(EMAIL_ALREADY_EXIST)
            is EmailExistenceResponse.EmailDoesNotExist -> ValidationResult.Correct
        }
    }

    companion object ResultCode {
        const val EMAIL_ALREADY_EXIST = "email-already-exist"
    }

}
