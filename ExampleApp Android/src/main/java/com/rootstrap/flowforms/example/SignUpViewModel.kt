package com.rootstrap.flowforms.example

import androidx.lifecycle.ViewModel
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.*
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NEW_PASSWORD
import kotlinx.coroutines.Dispatchers

class SignUpViewModel : ViewModel() {

    val formModel = SignUpFormModel()

    val form = FlowForm().setFields(
        FlowField(NAME, listOf(Required { formModel.name })),
        FlowField(EMAIL, listOf(
            Required { formModel.email },
            BasicEmailFormat { formModel.email },
            EmailDoesNotExistsInRemoteStorage(async = true) { formModel.email }
        )),
        FlowField(NEW_PASSWORD, listOf(
            Required { formModel.newPassword },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.newPassword }
        )),
        FlowField(CONFIRM_PASSWORD, listOf(
            Required { formModel.confirmPassword },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.confirmPassword },
            Match { formModel.newPassword to formModel.confirmPassword }
        )),
        FlowField(CONFIRMATION, listOf(RequiredTrue { formModel.confirm.value }))
    ).setDispatcher(Dispatchers.IO)
}
