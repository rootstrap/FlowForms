package com.rootstrap.flowforms.shared

data class SignUpFormUiState(
    val fields: Map<SignUpField, SignUpFieldState> = SignUpField.values()
        .associateWith { SignUpFieldState() },
    val isFormValid: Boolean = false,
    val isEmailVerificationInProgress: Boolean = false,
) {
    val name = fields[SignUpField.Name]?.value
    val email = fields[SignUpField.Email]?.value
    val password = fields[SignUpField.Password]?.value
    val confirmPassword = fields[SignUpField.ConfirmPassword]?.value
    val termsAccepted = fields[SignUpField.TermsAccepted]?.value.toBoolean()

    val nameError = fields[SignUpField.Name]?.error
    val emailError = fields[SignUpField.Email]?.error
    val passwordError = fields[SignUpField.Password]?.error
    val confirmPasswordError = fields[SignUpField.ConfirmPassword]?.error
}

data class SignUpFieldState(
    val value: String = "",
    val error: String? = null,
)

enum class SignUpField(val id: String) {
    Name("name"),
    Email("email"),
    Password("password"),
    ConfirmPassword("confirmPassword"),
    TermsAccepted("termsAccepted");

    companion object {
        fun fromId(id: String) = values().first { it.id == id}
    }
}
