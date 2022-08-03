package com.rootstrap.flowforms.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.field.FieldStatus
import com.rootstrap.flowforms.core.form.FormStatus
import com.rootstrap.flowforms.example.databinding.ActivityMainBinding
import com.rootstrap.flowforms.util.bind
import com.rootstrap.flowforms.util.collectOnLifeCycle

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SignUpViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.formModel = viewModel.signUpFormModel

        collectOnLifeCycle(
            { viewModel.form.fields.collect {
                collectOnLifeCycle(
                    { it["name"]?.status?.collect(::onNameStatusChange) },
                    { it["email"]?.status?.collect(::onEmailStatusChange) }
                )
            } },
            { viewModel.form.status.collect(::onFormStatusChange) }
        )

        binding.apply {
            viewModel.form.bind(
                lifecycleScope,
                nameInputEditText to "name",
                emailInputEditText to "email"
            )
        }
    }

    private fun onNameStatusChange(status : FieldStatus) {
        when (status.code) {
            CORRECT -> binding.nameInputLayout.error = null
            else -> binding.nameInputLayout.error = "This field is required"
        }
    }

    private fun onEmailStatusChange(status : FieldStatus) {
        when (status.code) {
            CORRECT -> binding.emailInputLayout.error = null
            else -> binding.emailInputLayout.error = "This field is required"
        }
    }

    private fun onFormStatusChange(status : FormStatus) {
        when (status.code) {
            CORRECT -> binding.continueButton.isEnabled = true
            else -> binding.continueButton.isEnabled = false
        }
    }

}
