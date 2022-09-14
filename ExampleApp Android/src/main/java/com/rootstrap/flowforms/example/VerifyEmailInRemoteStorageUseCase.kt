package com.rootstrap.flowforms.example

import com.rootstrap.flowforms.example.EmailExistenceResponse.EmailDoesNotExist
import com.rootstrap.flowforms.example.EmailExistenceResponse.EmailExist
import kotlinx.coroutines.delay

suspend fun verifyEmailInRemoteStorage(email : String): EmailExistenceResponse {
    delay(3000) // simulates a 3 seconds request.
    return when(email) {
        "existent@email.com" -> EmailExist
        else -> EmailDoesNotExist
    }
}

sealed class EmailExistenceResponse {
    object EmailExist : EmailExistenceResponse()
    object EmailDoesNotExist : EmailExistenceResponse()
}
