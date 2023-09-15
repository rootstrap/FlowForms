package com.example.exampleappandroidcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    viewModel: SignupViewModel = remember { SignupViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    SignUpScreen(
        uiState = uiState,
        onEmailChanged = viewModel::onEmailChange,
        onNameChanged = viewModel::onNameChange,
        onPasswordChanged = viewModel::onPasswordChange,
        onPasswordConfirmationChanged = viewModel::onPasswordConfirmChange,
        onTermsAcceptedChange = viewModel::onAcceptTermsChange,
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
    onTermsAcceptedChange: (Boolean) -> Unit,
    onSignupButtonClicked: () -> Unit
) {
    Column(
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = uiState.termsAccepted,
                onCheckedChange = onTermsAcceptedChange
            )

            Text(
                text = stringResource(R.string.confirmation),
                color = Color.DarkGray
            )
        }

        Button(
            onClick = onSignupButtonClicked,
            enabled = uiState.isFormValid,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(
                text = stringResource(id = R.string.signup),
                color = Color.DarkGray
            )
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    FlowFormsTheme {
        SignUpScreen(
            SignUpFormUiState(),
            onNameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onPasswordConfirmationChanged = {},
            onTermsAcceptedChange = {},
            onSignupButtonClicked = {}
        )
    }
}