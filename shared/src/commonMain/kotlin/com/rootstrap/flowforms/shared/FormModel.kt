package com.rootstrap.flowforms.shared

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.BasicEmailFormat
import com.rootstrap.flowforms.core.validation.Match
import com.rootstrap.flowforms.core.validation.MinLength
import com.rootstrap.flowforms.core.validation.Required
import com.rootstrap.flowforms.core.validation.RequiredTrue
import com.rootstrap.flowforms.core.validation.on
import kotlinx.coroutines.Dispatchers

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
        field(
            id = EMAIL,
            Required { email},
            BasicEmailFormat { email },
            EmailDoesNotExistsInRemoteStorage(async = true) { email }
        )
        field(
            id = PASSWORD,
            Required { password },
            MinLength(8) { password },
            Match { password to confirmPassword } on CONFIRM_PASSWORD,
        )
        field(
            id = CONFIRM_PASSWORD,
            Required { confirmPassword },
            MinLength(8) { confirmPassword},
            Match { password to confirmPassword }
        )
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
