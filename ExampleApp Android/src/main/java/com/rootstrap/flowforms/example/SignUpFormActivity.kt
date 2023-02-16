package com.rootstrap.flowforms.example

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rootstrap.flowforms.core.common.StatusCodes.BASIC_EMAIL_FORMAT_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.IN_PROGRESS
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.MIN_LENGTH_UNSATISFIED
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_UNSATISFIED
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.form.FormStatus
import com.rootstrap.flowforms.example.EmailDoesNotExistsInRemoteStorage.ResultCode.EMAIL_ALREADY_EXISTS
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.PASSWORD
import com.rootstrap.flowforms.example.databinding.LayoutSimpleSignUpFormBinding
import com.rootstrap.flowforms.util.bind
import com.rootstrap.flowforms.util.repeatOnLifeCycleScope

class SignUpFormActivity : AppCompatActivity() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: LayoutSimpleSignUpFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSimpleSignUpFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.lifecycleOwner = this
        binding.formModel = viewModel.formModel
        binding.continueButton.setOnClickListener {
            Toast.makeText(this, R.string.account_registered, Toast.LENGTH_SHORT).show()
        }

        listenStatusChanges()
        bindFields()
    }

    private fun listenStatusChanges() {
        viewModel.form.apply {
            repeatOnLifeCycleScope(
                { field(NAME)?.status?.collect(::onNameStatusChange) },
                { field(EMAIL)?.status?.collect(::onNameStatusChange) },
                { field(PASSWORD)?.status?.collect(::onNameStatusChange) },
                { field(CONFIRM_PASSWORD)?.status?.collect(::onNameStatusChange) },
                { status.collect(::onFormStatusChange) }
            )
        }
    }

    private fun bindFields() {
        binding.apply {
            viewModel.form.bind(lifecycleScope,
                nameInputEditText to NAME,
                emailInputEditText to EMAIL,
                passwordInputEditText to PASSWORD,
                confirmPasswordInputEditText to CONFIRM_PASSWORD
            )
            viewModel.form.bind(this@SignUpFormActivity, lifecycleScope,
                viewModel.formModel.confirm to CONFIRMATION
            )
        }
    }

    private fun onNameStatusChange(status: FieldStatus) {
        when (status.code) {
            REQUIRED_UNSATISFIED -> binding.nameInputLayout.error = getString(R.string.required_field)
            else -> binding.nameInputLayout.error = null
        }
    }

    private fun onEmailStatusChange(status: FieldStatus) {
        binding.apply {
            emailAvailableText.visibility = View.GONE
            emailLoadingProgressBar.visibility = View.GONE
            emailInputLayout.error = null
            when (status.code) {
                REQUIRED_UNSATISFIED -> emailInputLayout.error = getString(R.string.required_field)
                BASIC_EMAIL_FORMAT_UNSATISFIED -> emailInputLayout.error = getString(R.string.invalid_email)
                EMAIL_ALREADY_EXISTS -> emailInputLayout.error = getString(R.string.email_already_exist)
                IN_PROGRESS -> emailLoadingProgressBar.visibility = View.VISIBLE
                CORRECT ->  emailAvailableText.visibility = View.VISIBLE
            }
        }
    }

    private fun onPasswordStatusChange(status: FieldStatus) {
        when (status.code) {
            REQUIRED_UNSATISFIED -> binding.passwordInputLayout.error = getString(R.string.required_field)
            MIN_LENGTH_UNSATISFIED -> binding.passwordInputLayout.error = getString(R.string.min_length, MIN_PASSWORD_LENGTH)
            else -> binding.passwordInputLayout.error = null
        }
    }

    private fun onConfirmPasswordChange(status: FieldStatus) {
        when (status.code) {
            REQUIRED_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.required_field)
            MIN_LENGTH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.min_length, MIN_PASSWORD_LENGTH)
            MATCH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.password_match)
            else -> binding.confirmPasswordInputLayout.error = null
        }
    }

    private fun onFormStatusChange(status: FormStatus) {
        when (status.code) {
            CORRECT -> binding.continueButton.isEnabled = true
            else -> binding.continueButton.isEnabled = false
        }
    }

}
