package com.rootstrap.flowforms.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val viewModelH: SignUpViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelH?.form?.fields?.value?.get("name")?.status?.collect { status ->
                        when (status.code) {
                            CORRECT -> {
                                //ocultar error
                            }
                            else -> {
                                // mostrar error
                            }
                        }
                    }
                }
                // otro launch por cada field
                launch {
                    viewModelH?.form?.status?.collect { status ->
                        when (status.code) {
                            CORRECT -> {
                                // habilito boton
                            }
                            INCORRECT -> {
                                // deshabilito boton
                            }
                        }
                    }
                }
            }
        }

        //por cada edit text, on text changed,
        viewModelH?.form?.fields?.value?.get("name")?.triggerValidations()
    }
}
