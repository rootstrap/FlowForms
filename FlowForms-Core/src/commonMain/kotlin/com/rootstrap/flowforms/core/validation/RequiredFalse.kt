package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_FALSE_UNSATISFIED

/**
 * Validation to use when the given boolean should be false. It uses the valueProvider
 * to receive the boolean at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] if the boolean is exactly true. Otherwise returns
 * [REQUIRED_FALSE_UNSATISFIED].
 *
 * Examples
 *
 *  - false = true
 *  - true = false
 *  - null = false
 *
 * @param valueProvider function that returns the value used by the [validate] implementation.
 */
class RequiredFalse(val valueProvider : () -> Boolean?) : Validation() {

    override suspend fun validate() = ValidationResult(
        if (valueProvider() == false)
            CORRECT
        else
            REQUIRED_FALSE_UNSATISFIED
    )
}
