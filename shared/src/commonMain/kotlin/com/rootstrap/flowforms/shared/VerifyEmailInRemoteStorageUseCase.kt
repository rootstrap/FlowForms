package com.rootstrap.flowforms.shared

import kotlinx.coroutines.delay

suspend fun verifyEmailInRemoteStorage(email : String): EmailExistenceResponse {
    delay(3000) // simulates a 3 seconds request.
    return when(email) {
        "existent@email.com" -> EmailExistenceResponse.EmailExist
        else -> EmailExistenceResponse.EmailDoesNotExist
    }
}

sealed class EmailExistenceResponse {
    object EmailExist : EmailExistenceResponse()
    object EmailDoesNotExist : EmailExistenceResponse()
}
