package com.rootstrap.flowforms.core.field

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rootstrap.flowforms.core.validation.Validation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Common interface used by the fields to trigger their validations.
 * Implement this to customize how the validations should be executed and behave within a field.
 *
 */
interface FieldValidationBehavior {

    /**
     * Trigger the given validations firing updates to the mutable field status.
     * How they are triggered and behave depends on the implementation.
     *
     * Must return a boolean indicating if all the validations were successful or not.
     */
    @NativeCoroutines
    suspend fun triggerValidations(
        mutableFieldStatus: MutableStateFlow<FieldStatus>,
        validations: List<Validation>,
        asyncCoroutineDispatcher: CoroutineDispatcher? = null
    ) : Boolean

}
