package com.rootstrap.flowforms.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.PASSWORD
import com.rootstrap.flowforms.example.databinding.LayoutSimpleSignUpFormBinding
import com.rootstrap.flowforms.util.bind
import com.rootstrap.flowforms.util.repeatOnLifeCycleScope

class SignUpFormFragment : Fragment() {

    private var binding : LayoutSimpleSignUpFormBinding? = null
    private lateinit var viewModel : SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutSimpleSignUpFormBinding
            .inflate(layoutInflater, container, false)
            .let {
                binding = it
                it.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SignUpViewModel::class.java]
        binding?.let {
            it.lifecycleOwner = viewLifecycleOwner
            it.formModel = viewModel.formModel
            it.continueButton.setOnClickListener {
                viewModel.signUp()
            }
        }
        listenStatusChanges()
        bindFields()
    }

    private fun listenStatusChanges() {
        viewModel.form.apply {
            repeatOnLifeCycleScope(
                { field(NAME)?.status?.collect(::onNameStatusChange) },
                { field(EMAIL)?.status?.collect(::onEmailStatusChange) },
                { field(PASSWORD)?.status?.collect(::onPasswordStatusChange) },
                { field(CONFIRM_PASSWORD)?.status?.collect(::onConfirmPasswordChange) },
                { status.collect(::onFormStatusChange) },

                { viewModel.signUpEvents.collect(::onSignUpEvent) }
            )
        }
    }

    private fun onSignUpEvent(event : SignUpEvent) {
        when (event) {
            is SignUpEvent.SignUpSuccess -> {
                Toast.makeText(requireContext(), R.string.account_registered, Toast.LENGTH_SHORT).show()
            }
            is SignUpEvent.SignUpError -> {
                Toast.makeText(requireContext(), getString(R.string.review_form), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindFields() {
        binding?.apply {
            viewModel.form.bind(lifecycleScope,
                nameInputEditText to NAME,
                emailInputEditText to EMAIL,
                passwordInputEditText to PASSWORD,
                confirmPasswordInputEditText to CONFIRM_PASSWORD
            )
            viewModel.form.bind(this@SignUpFormFragment, lifecycleScope,
                viewModel.formModel.confirm to SignUpFormModel.CONFIRMATION
            )
        }
    }

    private fun onNameStatusChange(status: FieldStatus) {
        binding?.apply {
            when (status.code) {
                REQUIRED_UNSATISFIED -> nameInputLayout.error = getString(R.string.required_field)
                else -> nameInputLayout.error = null
            }
        }
    }

    private fun onEmailStatusChange(status: FieldStatus) {
        binding?.apply {
            emailLoadingProgressBar.visibility = View.GONE
            emailAvailableText.visibility = View.GONE
            when (status.code) {
                REQUIRED_UNSATISFIED -> emailInputLayout.error = getString(R.string.required_field)
                BASIC_EMAIL_FORMAT_UNSATISFIED -> emailInputLayout.error = getString(R.string.invalid_email)
                EMAIL_ALREADY_EXISTS -> emailInputLayout.error = getString(R.string.email_already_exist)
                IN_PROGRESS -> {
                    emailLoadingProgressBar.visibility = View.VISIBLE
                    emailInputLayout.error = null
                }
                CORRECT -> {
                    emailAvailableText.visibility = View.VISIBLE
                    emailInputLayout.error = null
                }
                else -> emailInputLayout.error = null
            }
        }
    }

    private fun onPasswordStatusChange(status: FieldStatus) {
        binding?.apply {
            when (status.code) {
                REQUIRED_UNSATISFIED -> passwordInputLayout.error = getString(R.string.required_field)
                MIN_LENGTH_UNSATISFIED -> passwordInputLayout.error = getString(R.string.min_length,
                    MIN_PASSWORD_LENGTH
                )
                else -> passwordInputLayout.error = null
            }
        }
    }

    private fun onConfirmPasswordChange(status: FieldStatus) {
        binding?.apply {
            when (status.code) {
                REQUIRED_UNSATISFIED -> confirmPasswordInputLayout.error = getString(R.string.required_field)
                MIN_LENGTH_UNSATISFIED -> confirmPasswordInputLayout.error = getString(R.string.min_length,
                    MIN_PASSWORD_LENGTH
                )
                MATCH_UNSATISFIED -> confirmPasswordInputLayout.error = getString(R.string.password_match)
                else -> confirmPasswordInputLayout.error = null
            }
        }
    }

    private fun onFormStatusChange(status: FormStatus) {
        binding?.apply {
            when (status.code) {
                CORRECT -> continueButton.isEnabled = true
                else -> continueButton.isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
