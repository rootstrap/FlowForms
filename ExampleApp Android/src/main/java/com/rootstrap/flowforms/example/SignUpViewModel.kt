package com.rootstrap.flowforms.example

import androidx.lifecycle.ViewModel
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.Required
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NEW_PASSWORD
import com.rootstrap.flowforms.example.validations.MinLength
import com.rootstrap.flowforms.example.validations.Match
import com.rootstrap.flowforms.example.validations.RequiredTrue
import com.rootstrap.flowforms.example.validations.ValidEmail

class SignUpViewModel : ViewModel() {

    val formModel = SignUpFormModel()

    val form = FlowForm().withFields(
        FlowField(NAME, listOf(Required { formModel.name })),
        FlowField(EMAIL, listOf(Required { formModel.email }, ValidEmail { formModel.email })),
        FlowField(NEW_PASSWORD, listOf(
            Required { formModel.newPassword },
            MinLength(MIN_LENGTH) { formModel.newPassword }
        )),
        FlowField(CONFIRM_PASSWORD, listOf(
            Required { formModel.confirmPassword },
            MinLength(MIN_LENGTH) { formModel.confirmPassword },
            Match { formModel.newPassword to formModel.confirmPassword }
        )),
        FlowField(CONFIRMATION, listOf(RequiredTrue { formModel.confirm.value }))
    )
}
