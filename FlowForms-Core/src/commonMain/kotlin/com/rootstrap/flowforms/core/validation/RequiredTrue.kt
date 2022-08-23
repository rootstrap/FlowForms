package com.rootstrap.flowforms.core.validation

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.REQUIRED_TRUE_UNSATISFIED

/**
 * Validation to use when the given boolean should be true. It uses the valueProvider
 * to receive the boolean at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] if the boolean is exactly true. Otherwise returns
 * [REQUIRED_TRUE_UNSATISFIED].
 *
 * Examples
 *
 *  - true = true
 *  - false = false
 *  - null = false
 *
 * @param valueProvider function that returns the value used by the [validate] implementation.
 */
class RequiredTrue(val valueProvider : () -> Boolean?) : Validation() {

    override suspend fun validate() = ValidationResult(
        if (valueProvider() == true)
            CORRECT
        else
            REQUIRED_TRUE_UNSATISFIED
    )
}
