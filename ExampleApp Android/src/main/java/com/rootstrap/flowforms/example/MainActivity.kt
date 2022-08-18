package com.rootstrap.flowforms.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.form.FormStatus
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NEW_PASSWORD
import com.rootstrap.flowforms.example.databinding.ActivityMainBinding
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.INVALID_EMAIL
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.MIN_LENGTH_UNSATISFIED
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.PASSWORD_MATCH_UNSATISFIED
import com.rootstrap.flowforms.util.bind
import com.rootstrap.flowforms.util.repeatOnLifeCycleScope

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.lifecycleOwner = this
        binding.formModel = viewModel.formModel

        listenStatusChanges()
        bindFields()
    }

    private fun listenStatusChanges() {
        viewModel.form.fields.value.let {
            repeatOnLifeCycleScope(
                { it[NAME]?.status?.collect(::onNameStatusChange) },
                { it[EMAIL]?.status?.collect(::onEmailStatusChange) },
                { it[NEW_PASSWORD]?.status?.collect(::onPasswordStatusChange) },
                { it[CONFIRM_PASSWORD]?.status?.collect(::onConfirmPasswordChange) },
                { viewModel.form.status.collect(::onFormStatusChange) }
            )
        }
    }

    private fun bindFields() {
        binding.apply {
            viewModel.form.bind(lifecycleScope,
                nameInputEditText to NAME,
                emailInputEditText to EMAIL,
                passwordInputEditText to NEW_PASSWORD,
                confirmPasswordInputEditText to CONFIRM_PASSWORD
            )
            viewModel.form.bind(this@MainActivity, lifecycleScope,
                viewModel.formModel.confirm to CONFIRMATION
            )
        }
    }

    private fun onNameStatusChange(status : FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.nameInputLayout.error = null
            else -> binding.nameInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onEmailStatusChange(status : FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.emailInputLayout.error = null
            INVALID_EMAIL -> binding.emailInputLayout.error = getString(R.string.invalid_email)
            else -> binding.emailInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onPasswordStatusChange(status : FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.passwordInputLayout.error = null
            MIN_LENGTH_UNSATISFIED -> binding.passwordInputLayout.error = getString(R.string.min_length, MIN_PASSWORD_LENGTH)
            else -> binding.passwordInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onConfirmPasswordChange(status : FieldStatus) {
        when (status.code) {
            CORRECT, UNMODIFIED -> binding.confirmPasswordInputLayout.error = null
            MIN_LENGTH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.min_length, MIN_PASSWORD_LENGTH)
            PASSWORD_MATCH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.password_match)
            else -> binding.confirmPasswordInputLayout.error = getString(R.string.required_field)
        }
    }

    private fun onFormStatusChange(status : FormStatus) {
        when (status.code) {
            CORRECT -> binding.continueButton.isEnabled = true
            else -> binding.continueButton.isEnabled = false
        }
    }

}
