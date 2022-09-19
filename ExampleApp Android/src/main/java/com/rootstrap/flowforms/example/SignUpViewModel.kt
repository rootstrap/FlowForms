package com.rootstrap.flowforms.example

import androidx.lifecycle.ViewModel
import com.rootstrap.flowforms.core.form.flowForm
import com.rootstrap.flowforms.core.validation.BasicEmailFormat
import com.rootstrap.flowforms.core.validation.Match
import com.rootstrap.flowforms.core.validation.MinLength
import com.rootstrap.flowforms.core.validation.Required
import com.rootstrap.flowforms.core.validation.RequiredTrue
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NEW_PASSWORD
import kotlinx.coroutines.Dispatchers

class SignUpViewModel : ViewModel() {

    val formModel = SignUpFormModel()

    val form = flowForm {
        field(NAME, Required { formModel.name })
        field(EMAIL,
            Required { formModel.email },
            BasicEmailFormat { formModel.email },
            EmailDoesNotExistsInRemoteStorage(async = true) { formModel.email }
        )
        field(NEW_PASSWORD,
            Required { formModel.newPassword },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.newPassword }
        )
        field(CONFIRM_PASSWORD,
            Required { formModel.confirmPassword },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.confirmPassword },
            Match { formModel.newPassword to formModel.confirmPassword }
        )
        field(CONFIRMATION, RequiredTrue { formModel.confirm.value })
        dispatcher = Dispatchers.IO
    }

}
