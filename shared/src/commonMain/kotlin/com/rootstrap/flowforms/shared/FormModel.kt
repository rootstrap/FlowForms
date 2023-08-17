package com.rootstrap.flowforms.shared

import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.validation.BasicEmailFormat
import com.rootstrap.flowforms.core.validation.Match
import com.rootstrap.flowforms.core.validation.MinLength
import com.rootstrap.flowforms.core.validation.Required
import com.rootstrap.flowforms.core.validation.RequiredTrue
import com.rootstrap.flowforms.core.validation.on
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
class FormModel constructor(
    private val asyncDispatcher: CoroutineDispatcher
) {

    // needed for Swift default parameters interop
    constructor() : this(Dispatchers.Default)

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
        dispatcher = asyncDispatcher
    }

    companion object {
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val CONFIRM_PASSWORD = "confirm_password"
        const val TERMS_ACCEPTED = "terms_accepted"
    }
}
