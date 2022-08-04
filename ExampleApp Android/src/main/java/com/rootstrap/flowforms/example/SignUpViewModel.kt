package com.rootstrap.flowforms.example

import androidx.lifecycle.ViewModel
import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.Required

class SignUpViewModel : ViewModel() {

    val signUpFormModel = SignUpFormModel()

    val form = FlowForm().setFields(
        FlowField("name", listOf(Required { signUpFormModel.name })),
        FlowField("email", listOf(Required { signUpFormModel.email }))
    )
}
