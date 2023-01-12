package com.rootstrap.flowforms.example

import androidx.lifecycle.MutableLiveData

data class SignUpFormModel(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var confirm: MutableLiveData<Boolean> = MutableLiveData(false)
) {
    companion object {
        const val NAME = "name"
        const val EMAIL = "email"
        const val CONFIRMATION = "confirmation"
        const val PASSWORD = "new_password"
        const val CONFIRM_PASSWORD = "confirm_password"
        const val MIN_PASSWORD_LENGTH = 6
    }
}
