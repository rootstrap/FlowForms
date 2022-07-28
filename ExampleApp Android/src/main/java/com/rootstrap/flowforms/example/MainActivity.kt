package com.rootstrap.flowforms.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.example.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SignUpViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.formModel = viewModel.signUpFormModel

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.form.fields.collect {
                        lifecycleScope.launch {
                            repeatOnLifecycle(Lifecycle.State.STARTED) {
                                launch {
                                    it["name"]?.status?.collect { status ->
                                        when (status.code) {
                                            CORRECT -> binding.nameInputLayout.error = null
                                            else -> binding.nameInputLayout.error = "This field is required"
                                        }
                                    }
                                }
                                launch {
                                    it["email"]?.status?.collect { status ->
                                        when (status.code) {
                                            CORRECT -> binding.emailInputLayout.error = null
                                            else -> binding.emailInputLayout.error = "This field is required"
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
                viewModel.form.validateOnValueChange("name")
            }
        }

        binding.emailInputEditText.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.form.validateOnValueChange("email")
            }
        }
    }
}
