package com.rootstrap.flowforms.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.form.FormStatus
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_PASSWORD_LENGTH
import com.rootstrap.flowforms.example.databinding.LayoutSimpleSignUpFormBinding
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.INVALID_EMAIL
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.MIN_LENGTH_UNSATISFIED
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.PASSWORD_MATCH_UNSATISFIED
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
                Toast.makeText(requireContext(), R.string.account_registered, Toast.LENGTH_SHORT).show()
            }
        }
        listenStatusChanges()
        bindFields()
    }

    private fun listenStatusChanges() {
        viewModel.form.fields.value.let {
            repeatOnLifeCycleScope(
                { it[SignUpFormModel.NAME]?.status?.collect(::onNameStatusChange) },
                { it[SignUpFormModel.EMAIL]?.status?.collect(::onEmailStatusChange) },
                { it[SignUpFormModel.NEW_PASSWORD]?.status?.collect(::onPasswordStatusChange) },
                { it[SignUpFormModel.CONFIRM_PASSWORD]?.status?.collect(::onConfirmPasswordChange) },
                { viewModel.form.status.collect(::onFormStatusChange) }
            )
        }
    }

    private fun bindFields() {
        binding?.apply {
            viewModel.form.bind(lifecycleScope,
                nameInputEditText to SignUpFormModel.NAME,
                emailInputEditText to SignUpFormModel.EMAIL,
                passwordInputEditText to SignUpFormModel.NEW_PASSWORD,
                confirmPasswordInputEditText to SignUpFormModel.CONFIRM_PASSWORD
            )
            viewModel.form.bind(this@SignUpFormFragment, lifecycleScope,
                viewModel.formModel.confirm to SignUpFormModel.CONFIRMATION
            )
        }
    }

    private fun onNameStatusChange(status: FieldStatus) {
        binding?.apply {
            when (status.code) {
                CORRECT, UNMODIFIED -> nameInputLayout.error = null
                else -> nameInputLayout.error = getString(R.string.required_field)
            }
        }
    }

    private fun onEmailStatusChange(status: FieldStatus) {
        binding?.apply {
            when (status.code) {
                CORRECT, UNMODIFIED -> emailInputLayout.error = null
                INVALID_EMAIL -> emailInputLayout.error = getString(R.string.invalid_email)
                else -> emailInputLayout.error = getString(R.string.required_field)
            }
        }
    }

    private fun onPasswordStatusChange(status: FieldStatus) {
        binding?.apply {
            when (status.code) {
                CORRECT, UNMODIFIED -> passwordInputLayout.error = null
                MIN_LENGTH_UNSATISFIED -> passwordInputLayout.error = getString(R.string.min_length,
                    MIN_PASSWORD_LENGTH
                )
                else -> passwordInputLayout.error = getString(R.string.required_field)
            }
        }
    }

    private fun onConfirmPasswordChange(status: FieldStatus) {
        binding?.apply {
            when (status.code) {
                CORRECT, UNMODIFIED -> confirmPasswordInputLayout.error = null
                MIN_LENGTH_UNSATISFIED -> confirmPasswordInputLayout.error = getString(R.string.min_length,
                    MIN_PASSWORD_LENGTH
                )
                PASSWORD_MATCH_UNSATISFIED -> confirmPasswordInputLayout.error = getString(R.string.password_match)
                else -> confirmPasswordInputLayout.error = getString(R.string.required_field)
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
