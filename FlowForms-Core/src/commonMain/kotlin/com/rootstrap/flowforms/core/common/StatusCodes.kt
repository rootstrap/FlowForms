package com.rootstrap.flowforms.core.common

import com.rootstrap.flowforms.core.field.FlowField
import com.rootstrap.flowforms.core.form.FlowForm
import com.rootstrap.flowforms.core.validation.*

/**
 * Out of the box [FlowForm], [FlowField], and [Validation] status codes for common use cases.
 */
object StatusCodes {

    /**
     * Default status for [FlowField]s and [FlowForm]s.
     */
    const val UNMODIFIED = "unmodified"

    /**
     * Status for successful [Validation]s and correct [FlowField]s and [FlowForm]s.
     */
    const val CORRECT = "correct"

    /**
     * Status for [FlowField]s whose async [Validation]s didn't finished yet
     */
    const val IN_PROGRESS = "in_progress"

    /**
     * Status for [FlowField]s or [FlowForm]s not validated at all.
     *
     * In [FlowField]s this happens when all the field's [Validation]s were not executed but all the executed
     * validations were [CORRECT].
     * ```
     * For example, a FlowField triggered its onValueChange validations and they were CORRECT, but it
     * still has onBlurValidations that were not executed because the user didn't took the focus off
     * the component.
     * ```
     * In [FlowForm]s this happens when some of its [FlowField]s are [CORRECT] and others are [UNMODIFIED].
     * ```
     * For example, a FlowForm with a CORRECT "email" and "password" fields, but with a "Confirm password"
     * field UNMODIFIED, because the user didn't interacted with the component yet.
     * ```
     * Keep in mind that if the [FlowForm] contains one **Failed** [FlowField] **then it's
     * considered failed too**, even if other fields are correct or unmodified.
     */
    const val INCOMPLETE = "incomplete"

    // out of the box validation result status codes

    /**
     * Represents an incorrect [FlowField] with a not well defined error code,
     * being it directly not defined (using this status code as the return value) or that more than
     * one validation failed (which means it may have various and different error codes).
     *
     * Also represents an incorrect [FlowForm] with one or more failed [FlowField]s.
     * Failed means that a [FlowField] has an status different than [CORRECT] ([INCORRECT] or custom ones)
     */
    const val INCORRECT = "incorrect"

    /**
     * Represents a [Required] validation failure.
     */
    const val REQUIRED_UNSATISFIED = "required_unsatisfied"

    /**
     * Represents a [RequiredTrue] validation failure.
     */
    const val REQUIRED_TRUE_UNSATISFIED = "required_true_unsatisfied"

    /**
     * Represents a [RequiredFalse] validation failure.
     */
    const val REQUIRED_FALSE_UNSATISFIED = "required_false_unsatisfied"

    /**
     * Represents a [MinLength] validation failure.
     */
    const val MIN_LENGTH_UNSATISFIED = "min_length_unsatisfied"

    /**
     * Represents a [MaxLength] validation failure.
     */
    const val MAX_LENGTH_UNSATISFIED = "max_length_unsatisfied"

    /**
     * Represents a [Match] validation failure.
     */
    const val MATCH_UNSATISFIED = "match_unsatisfied"

    /**
     * Represents a [MatchRegex] validation failure.
     */
    const val MATCH_REGEX_UNSATISFIED = "match_regex_unsatisfied"

    /**
     * Represents a [BasicEmailFormat] validation failure.
     */
    const val BASIC_EMAIL_FORMAT_UNSATISFIED = "basic_email_unsatisfied"
}
