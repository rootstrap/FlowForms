package com.rootstrap.flowforms.core.field

import kotlinx.coroutines.CoroutineDispatcher

interface FieldActions {

    /**
     * Triggers the onValueChange validations associated on a [Field][com.rootstrap.flowforms.core.field.FField]
     * based on the field's [FieldValidationBehavior].
     */
    suspend fun triggerOnValueChangeValidations(asyncCoroutineDispatcher: CoroutineDispatcher? = null) : Boolean

    /**
     * Triggers the onBlur validations associated on a [Field][com.rootstrap.flowforms.core.field.FField]
     * based on the field's [FieldValidationBehavior].
     */
    suspend fun triggerOnBlurValidations(asyncCoroutineDispatcher: CoroutineDispatcher? = null) : Boolean

    /**
     * Triggers the onFocus validations associated on a [Field][com.rootstrap.flowforms.core.field.FField]
     * based on the field's [FieldValidationBehavior].
     */
    suspend fun triggerOnFocusValidations(asyncCoroutineDispatcher: CoroutineDispatcher? = null) : Boolean
}