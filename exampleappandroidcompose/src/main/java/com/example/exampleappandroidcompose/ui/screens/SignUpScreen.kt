package com.example.exampleappandroidcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.exampleappandroidcompose.R
import com.example.exampleappandroidcompose.ui.common.AppTextField
import com.example.exampleappandroidcompose.ui.theme.FlowFormsTheme
import com.example.exampleappandroidcompose.ui.theme.Padding.huge
import com.rootstrap.flowforms.shared.SignUpFormUiState
import com.rootstrap.flowforms.shared.SignupViewModel

@Composable
fun SignUpScreen(
    viewModel: SignupViewModel = SignupViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SignUpScreen(
        uiState = uiState,
        onEmailChanged = viewModel::onEmailChange,
        onNameChanged = viewModel::onNameChange,
        onPasswordChanged = viewModel::onPasswordChange,
        onPasswordConfirmationChanged = viewModel::onPasswordConfirmChange,
        onSignupButtonClicked = {}
    )
}

@Composable
fun SignUpScreen(
    uiState: SignUpFormUiState,
    onEmailChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordConfirmationChanged: (String) -> Unit,
    onSignupButtonClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = huge)
    ) {
        AppTextField(
            value = uiState.name.orEmpty(),
            onValueChange = onNameChanged,
            label = stringResource(id = R.string.name),
            errorMessage = uiState.nameError
        )

        AppTextField(
            value = uiState.email.orEmpty(),
            onValueChange = onEmailChanged,
            label = stringResource(id = R.string.email),
            errorMessage = uiState.emailError
        )

        AppTextField(
            value = uiState.password.orEmpty(),
            onValueChange = onPasswordChanged,
            label = stringResource(id = R.string.password),
            errorMessage = uiState.passwordError,
            isPasswordField = true
        )

        AppTextField(
            value = uiState.confirmPassword.orEmpty(),
            onValueChange = onPasswordConfirmationChanged,
            label = stringResource(id = R.string.confirm_password),
            errorMessage = uiState.confirmPasswordError,
            isPasswordField = true
        )
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    FlowFormsTheme {
        SignUpScreen(
            SignUpFormUiState(),
            {},
            {},
            {},
            {},
            {}
        )
    }
}