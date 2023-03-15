package com.rootstrap.flowforms.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.validation.BasicEmailFormat
import com.rootstrap.flowforms.core.validation.Match
import com.rootstrap.flowforms.core.validation.MinLength
import com.rootstrap.flowforms.core.validation.Required
import com.rootstrap.flowforms.core.validation.RequiredTrue
import com.rootstrap.flowforms.core.validation.on
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.PASSWORD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val _signUpEvents = Channel<SignUpEvent>()
    val signUpEvents = _signUpEvents.receiveAsFlow()

    val formModel = SignUpFormModel()

    val form = flowForm {
        field(NAME, Required { formModel.name })
        field(EMAIL,
            Required { formModel.email },
            BasicEmailFormat { formModel.email },
            EmailDoesNotExistsInRemoteStorage(async = true) { formModel.email }
        )
        field(PASSWORD,
            Required { formModel.password },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.password },
            Match { formModel.password to formModel.confirmPassword } on CONFIRM_PASSWORD
        )
        field(CONFIRM_PASSWORD,
            Required { formModel.confirmPassword },
            MinLength(MIN_PASSWORD_LENGTH) { formModel.confirmPassword },
            Match { formModel.password to formModel.confirmPassword }
        )
        field(CONFIRMATION, RequiredTrue { formModel.confirm.value })
        dispatcher = Dispatchers.IO
    }

    fun signUp() {
        viewModelScope.launch {
            if (form.validateAllFields()) {
                _signUpEvents.send(SignUpEvent.SignUpSuccess)
            } else {
                _signUpEvents.send(SignUpEvent.SignUpError)
            }
        }
    }

}

sealed class SignUpEvent {
    object SignUpSuccess : SignUpEvent()
    object SignUpError : SignUpEvent()
}
