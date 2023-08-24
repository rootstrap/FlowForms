package com.rootstrap.flowforms.shared

import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.validation.BasicEmailFormat
import com.rootstrap.flowforms.core.validation.Match
import com.rootstrap.flowforms.core.validation.MinLength
import com.rootstrap.flowforms.core.validation.Required
import com.rootstrap.flowforms.core.validation.RequiredTrue
import com.rootstrap.flowforms.core.validation.on
import com.rootstrap.flowforms.shared.utils.CoroutineViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SignupViewModel constructor(
    private val asyncDispatcher: CoroutineDispatcher
) : CoroutineViewModel() {

    // needed for Swift default parameters interop
    constructor() : this(Dispatchers.Default)

    private val _uiState = MutableStateFlow(SignUpFormUiState())
    val uiState = _uiState.asStateFlow()

    val form = flowForm {
        field(NAME, Required { uiState.value.name })
        field(
            id = EMAIL,
            Required { uiState.value.email },
            BasicEmailFormat { uiState.value.email },
            EmailDoesNotExistsInRemoteStorage(async = true) { uiState.value.email }
        )
        field(
            id = PASSWORD,
            Required { uiState.value.password },
            MinLength(8) { uiState.value.password },
            Match { uiState.value.password to uiState.value.confirmPassword } on CONFIRM_PASSWORD,
        )
        field(
            id = CONFIRM_PASSWORD,
            Required { uiState.value.confirmPassword },
            MinLength(8) { uiState.value.confirmPassword },
            Match { uiState.value.password to uiState.value.confirmPassword }
        )
        field(TERMS_ACCEPTED, RequiredTrue { uiState.value.termsAccepted })
        dispatcher = asyncDispatcher
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onPasswordConfirmChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun onAcceptTermsChange(value: Boolean) {
        _uiState.update { it.copy(termsAccepted = value) }
    }

    // needed for Swift interop
    fun observeUiState(onChange: (SignUpFormUiState) -> Unit) {
        uiState.onEach { onChange(it) }.launchIn(coroutineScope)
    }

    companion object {
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val CONFIRM_PASSWORD = "confirm_password"
        const val TERMS_ACCEPTED = "terms_accepted"
    }

}
