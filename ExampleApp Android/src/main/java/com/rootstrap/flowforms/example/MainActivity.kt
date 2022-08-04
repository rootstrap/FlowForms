package com.rootstrap.flowforms.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRMATION
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.CONFIRM_PASSWORD
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.EMAIL
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.MIN_LENGTH
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NAME
import com.rootstrap.flowforms.example.SignUpFormModel.Companion.NEW_PASSWORD
import com.rootstrap.flowforms.example.databinding.ActivityMainBinding
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.INVALID_EMAIL
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.MIN_LENGTH_UNSATISFIED
import com.rootstrap.flowforms.example.validations.ValidEmail.Companion.PASSWORD_MATCH_UNSATISFIED
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SignUpViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.lifecycleOwner = this
        binding.formModel = viewModel.formModel

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.form.fields.collect {
                        lifecycleScope.launch {
                            repeatOnLifecycle(Lifecycle.State.STARTED) {
                                launch {
                                    it[NAME]?.status?.collect { status ->
                                        when (status.code) {
                                            CORRECT, UNMODIFIED -> binding.nameInputLayout.error = null
                                            else -> binding.nameInputLayout.error = getString(R.string.required_field)
                                        }
                                    }
                                }
                                launch {
                                    it[EMAIL]?.status?.collect { status ->
                                        when (status.code) {
                                            CORRECT, UNMODIFIED -> binding.emailInputLayout.error = null
                                            INVALID_EMAIL -> binding.emailInputLayout.error = getString(R.string.invalid_email)
                                            else -> binding.emailInputLayout.error = getString(R.string.required_field)
                                        }
                                    }
                                }
                                launch {
                                    it[NEW_PASSWORD]?.status?.collect { status ->
                                        when (status.code) {
                                            CORRECT, UNMODIFIED -> binding.passwordInputLayout.error = null
                                            MIN_LENGTH_UNSATISFIED -> binding.passwordInputLayout.error = getString(R.string.min_length, MIN_LENGTH)
                                            else -> binding.passwordInputLayout.error = getString(R.string.required_field)
                                        }
                                    }
                                }
                                launch {
                                    it[CONFIRM_PASSWORD]?.status?.collect { status ->
                                        when (status.code) {
                                            CORRECT, UNMODIFIED -> binding.confirmPasswordInputLayout.error = null
                                            MIN_LENGTH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.min_length, MIN_LENGTH)
                                            PASSWORD_MATCH_UNSATISFIED -> binding.confirmPasswordInputLayout.error = getString(R.string.password_match)
                                            else -> binding.confirmPasswordInputLayout.error = getString(R.string.required_field)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.form.status.collect { status ->
                        when (status.code) {
                            CORRECT -> {
                                binding.continueButton.isEnabled = true
                            }
                            else -> {
                                binding.continueButton.isEnabled = false
                            }
                        }
                    }
                }
            }
        }

        binding.nameInputEditText.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.form.validateOnValueChange(NAME)
            }
        }

        binding.emailInputEditText.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.form.validateOnValueChange(EMAIL)
            }
        }

        binding.passwordInputEditText.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.form.validateOnValueChange(NEW_PASSWORD)
            }
        }

        binding.confirmPasswordInputEditText.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.form.validateOnValueChange(CONFIRM_PASSWORD)
            }
        }

        viewModel.formModel.confirm.observe(this) {
            lifecycleScope.launch {
                viewModel.form.validateOnValueChange(CONFIRMATION)
            }
        }
    }
}
