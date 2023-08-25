package com.rootstrap.flowforms.shared

import com.rootstrap.flowforms.core.common.StatusCodes
import com.rootstrap.flowforms.core.common.StatusCodes.BASIC_EMAIL_FORMAT_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.MIN_LENGTH_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_TRUE_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED
import com.rootstrap.flowforms.core.dsl.flowForm
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.validation.BasicEmailFormat
import com.rootstrap.flowforms.core.validation.Match
import com.rootstrap.flowforms.core.validation.MinLength
import com.rootstrap.flowforms.core.validation.Required
import com.rootstrap.flowforms.core.validation.RequiredTrue
import com.rootstrap.flowforms.core.validation.on
import com.rootstrap.flowforms.shared.EmailDoesNotExistsInRemoteStorage.ResultCode.EMAIL_ALREADY_EXISTS
import com.rootstrap.flowforms.shared.utils.CoroutineViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModel constructor(
    private val asyncDispatcher: CoroutineDispatcher
) : CoroutineViewModel() {

    // needed for Swift default parameters interop. Should be replaced by DI
    constructor() : this(Dispatchers.Default)

    private val _uiState = MutableStateFlow(SignUpFormUiState())
    val uiState = _uiState.asStateFlow()

    private val form = flowForm {
        field(SignUpField.Name.id, Required { uiState.value.name })
        field(
            id = SignUpField.Email.id,
            Required { uiState.value.email },
            BasicEmailFormat { uiState.value.email },
            EmailDoesNotExistsInRemoteStorage(async = true) { uiState.value.email }
        )
        field(
            id = SignUpField.Password.id,
            Required { uiState.value.password },
            MinLength(8) { uiState.value.password },
            Match {
                uiState.value.password to uiState.value.confirmPassword
            } on SignUpField.ConfirmPassword.id,
        )
        field(
            id = SignUpField.ConfirmPassword.id,
            Required { uiState.value.confirmPassword },
            MinLength(8) { uiState.value.confirmPassword },
            Match { uiState.value.password to uiState.value.confirmPassword }
        )
        field(SignUpField.TermsAccepted.id, RequiredTrue { uiState.value.termsAccepted })
        dispatcher = asyncDispatcher
    }

    init {
        form.status.onEach { status ->
            _uiState.update { it.copy(isFormValid = status.code == CORRECT) }
        }.launchIn(coroutineScope)

        coroutineScope.launch {
            flowOf(elements = form.fieldStatusFlows.toTypedArray())
                .flattenMerge()
                .collect { fieldStatus ->
                    updateFieldError(fieldStatus)
                    updateEmailVerificationInProgress(fieldStatus)
                }
        }
    }

    private fun updateFieldError(fieldStatus: FieldStatus) {
        val errorMessage = fieldStatus.getErrorMessage()
        _uiState.update {
            it.copy(
                fields = it.fields.toMutableMap().apply {
                    val field = SignUpField.fromId(fieldStatus.fieldId)
                    this[field] = this[field]!!.copy(error = errorMessage)
                }
            )
        }
    }

    private fun updateEmailVerificationInProgress(fieldStatus: FieldStatus) {
        if (fieldStatus.fieldId == SignUpField.Email.id) {
            _uiState.update { state ->
                state.copy(
                    isEmailVerificationInProgress = fieldStatus.code == StatusCodes.IN_PROGRESS
                )
            }
        }
    }

    fun onNameChange(value: String) {
        onFieldValueChange(SignUpField.Name, value)
    }

    fun onEmailChange(value: String) {
        onFieldValueChange(SignUpField.Email, value)
    }

    fun onPasswordChange(value: String) {
        onFieldValueChange(SignUpField.Password, value)
    }

    fun onPasswordConfirmChange(value: String) {
        onFieldValueChange(SignUpField.ConfirmPassword, value)
    }

    fun onAcceptTermsChange(value: Boolean) {
        onFieldValueChange(SignUpField.TermsAccepted, value.toString())
    }

    private fun onFieldValueChange(field: SignUpField, value: String) {
        _uiState.update {
            it.copy(
                fields = it.fields.toMutableMap().apply {
                    this[field] = this[field]!!.copy(value = value)
                }
            )
        }
        coroutineScope.launch {
            form.validateOnValueChange(field.id)
        }
    }

    // needed for Swift interop
    fun observeUiState(onChange: (SignUpFormUiState) -> Unit) {
        uiState.onEach {
            onChange(it)
        }.launchIn(coroutineScope)
    }

    private fun FieldStatus.getErrorMessage() = when (SignUpField.fromId(fieldId)) {
        SignUpField.Name -> when(code) {
            REQUIRED_UNSATISFIED -> Strings.NameRequired
            else -> null
        }
        SignUpField.Email -> when (code) {
            REQUIRED_UNSATISFIED -> Strings.EmailRequired
            BASIC_EMAIL_FORMAT_UNSATISFIED -> Strings.BadEmailFormat
            EMAIL_ALREADY_EXISTS -> Strings.EmailAlreadyExists
            else -> null
        }
        SignUpField.Password -> when (code) {
            REQUIRED_UNSATISFIED -> Strings.PasswordRequired
            MIN_LENGTH_UNSATISFIED -> Strings.PasswordMinLength
            else -> null
        }
        SignUpField.ConfirmPassword -> when (code) {
            REQUIRED_UNSATISFIED -> Strings.PasswordConfirmationRequired
            MIN_LENGTH_UNSATISFIED -> Strings.PasswordMinLength
            MATCH_UNSATISFIED -> Strings.PasswordsDontMatch
            else -> null
        }
        SignUpField.TermsAccepted -> when(code) {
            REQUIRED_TRUE_UNSATISFIED -> Strings.TermsRequired
            else -> null
        }
    }
}
