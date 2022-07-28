package com.rootstrap.flowforms.example

data class SignUpFormModel(
    var name: String = "",
    var email: String = "",
    var newPassword: String = "",
    var confirmPassword: String = "",
    var confirm: Boolean = false
)
