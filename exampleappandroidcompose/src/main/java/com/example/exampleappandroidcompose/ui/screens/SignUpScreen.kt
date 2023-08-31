package com.example.exampleappandroidcompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.exampleappandroidcompose.R
import com.example.exampleappandroidcompose.ui.common.AppTextField
import com.example.exampleappandroidcompose.ui.theme.FlowFormsTheme
import com.example.exampleappandroidcompose.ui.theme.Padding.huge

@Composable
fun SignUpScreen() {

}

@Composable
fun SignUpScreen(
    onEmailChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordConfirmationChanged: (String) -> Unit,
    onLoginButtonClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = huge)
    ) {
        AppTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            label = stringResource(id = R.string.email),
            errorMessage = stringResource(id = R.string.invalid_email)
        )

        AppTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = stringResource(id = R.string.password),
            errorMessage = stringResource(id = R.string.required_field),
            isPasswordField = true
        )
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    FlowFormsTheme {
        SignUpScreen(
            {},
            {},
            {},
            {},
            {}
        )
    }
}