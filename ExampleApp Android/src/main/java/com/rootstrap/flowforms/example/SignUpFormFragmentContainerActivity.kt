package com.rootstrap.flowforms.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rootstrap.flowforms.example.databinding.ActivitySimpleSignUpFragmentFormBinding

class SignUpFormFragmentContainerActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySimpleSignUpFragmentFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleSignUpFragmentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
