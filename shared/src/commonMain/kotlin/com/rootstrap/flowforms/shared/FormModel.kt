package com.rootstrap.flowforms.shared

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.*
import kotlinx.coroutines.*

class StatusCode {
    var code = StatusCodes
}

class FormModel {
    var termsAccepted: Boolean = false
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""

    val form = flowForm {
        field(NAME, Required { name })
        field(EMAIL, Required { email}, BasicEmailFormat { email }, EmailDoesNotExistsInRemoteStorage(async=true) { email })
        field(PASSWORD, MinLength(8) { password })
        field(CONFIRM_PASSWORD, MinLength(8) { confirmPassword}, Match { password to confirmPassword })
        field(TERMS_ACCEPTED, RequiredTrue { termsAccepted })
        dispatcher = Dispatchers.Default
    }

    companion object {
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val CONFIRM_PASSWORD = "confirm_password"
        const val TERMS_ACCEPTED = "terms_accepted"
    }
}

fun FlowForm.fieldFor(id: String) = fields.value[id] as FlowField
