package com.rootstrap.flowforms.core.validation

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.MATCH_UNSATISFIED

/**
 * Validation to use when one value should match to another value. It uses the valueProvider to
 * receive both values (as a [Pair]) at runtime when the validation fun is called.
 *
 * The validation fun returns [CORRECT] if both values are equals (defined by their equals
 * implementation). Otherwise returns [MATCH_UNSATISFIED].
 *
 * Example :
 *
 * - 1 to 1 = true
 * - 1 to 10 = false
 * - null to null = true
 * - "" to "" = true
 * - "" to " " = false
 * - "aBc" to "aBc" = true
 * - "aBc" to "abc" = false
 * - object to object = depends on its equals implementation.
 *
 * @param failFast **Optional**, refer to failFast property on [Validation] class.
 * @param async **Optional**, refer to async property on [Validation] class.
 * @param valueProvider function that returns the values used by the [validate] implementation.
 */
class Match(
    failFast : Boolean = true,
    async : Boolean = false,
    val valueProvider: () -> Pair<Any?, Any?>
) : Validation(failFast = failFast, async = async) {

    @NativeCoroutines
    override suspend fun validate() : ValidationResult {
        val value = valueProvider()

        return ValidationResult(
            if (value.first == value.second)
                CORRECT
            else
                MATCH_UNSATISFIED
        )
    }
}
