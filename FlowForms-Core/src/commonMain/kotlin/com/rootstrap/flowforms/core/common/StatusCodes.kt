package com.rootstrap.flowforms.core.common

object StatusCodes {

    /**
     * Default status for fields and forms.
     */
    const val UNMODIFIED = "unmodified"

    /**
     * Status for successful validations and correct fields and forms.
     */
    const val CORRECT = "correct"

    /**
     * Status for a form not validated at all. This can happen when some fields are Correct
     * and others are unmodified.
     *
     * If the form contains one failed field then it's considered failed too, even if other
     * fields are correct or unmodified.
     */
    const val INCOMPLETE = "incomplete"

    // out of the box validation result status codes

    /**
     * Represents an incorrect field with a not well defined error code.
     *
     * Being it directly not defined or that more than one validation failed.
     */
    const val INCORRECT = "incorrect"

    /**
     * Represents a required validation failure.
     */
    const val REQUIRED_UNSATISFIED = "required_unsatisfied"
}
