package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCOMPLETE
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED

/**
 * Represents the current status of a [FForm]
 */
class FormStatus(
    val code: String
) {
    companion object {

        /**
         * Represents a form with status [CORRECT]
         */
        val Correct = FormStatus(CORRECT)

        /**
         * Represents a form with status [UNMODIFIED]
         */
        val Unmodified = FormStatus(UNMODIFIED)

        /**
         * Represents a form with status [INCORRECT]
         */
        val Incorrect = FormStatus(INCORRECT)

        /**
         * Represents a form with status [INCOMPLETE]
         */
        val Incomplete = FormStatus(INCOMPLETE)
    }
}
