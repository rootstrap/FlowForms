package com.rootstrap.flowforms.example

import androidx.lifecycle.ViewModel
import com.rootstrap.flowforms.core.field.FField
import com.rootstrap.flowforms.core.form.FForm
import com.rootstrap.flowforms.core.validation.Required

class SignUpViewModel : ViewModel() {

    val signUpFormModel = SignUpFormModel()

    val form = FForm().withFields(
        FField("name", listOf(Required { signUpFormModel.name }))
    )
}
