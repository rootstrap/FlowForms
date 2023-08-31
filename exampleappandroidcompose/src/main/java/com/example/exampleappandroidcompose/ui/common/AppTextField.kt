package com.example.exampleappandroidcompose.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.exampleappandroidcompose.R
import com.example.exampleappandroidcompose.ui.theme.Padding.small

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    isPasswordField: Boolean = false
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = small),
        label = { Text(label) },
        singleLine = true,
        supportingText = {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red
                )
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = Color.Black,
            disabledLabelColor = Color.Gray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        visualTransformation = if (!isPasswordField || isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            if (isPasswordField) TogglePasswordEye(isPasswordVisible) { isPasswordVisible = it }
        }
    )
}

@Composable
fun TogglePasswordEye(showPassword: Boolean, newState: (Boolean) -> Unit) {
    val image = if (showPassword) {
        R.drawable.ic_visibility
    } else {
        R.drawable.ic_visibility_off
    }

    IconButton(onClick = { newState(!showPassword) }) {
        Icon(painterResource(id = image), contentDescription = null)
    }
}
