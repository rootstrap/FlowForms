package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.INCORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * FlowForm : Reactive declarative form intended to reduce the boilerplate code required to manage form
 * and field status changes.
 *
 * Based on flows, its status is updated automatically when any of the field's inner status changes.
 */
open class FForm {

    private val _fields = MutableStateFlow(mapOf<String, FField>())

    /**
     * Flow with the map of fields contained in this form.
     * Initially it is an empty map until [withFields] is called with some fields in it.
     */
    val fields = _fields.asStateFlow()

    /**
     * flow with the status of the form. Every value emitted by this form represents it's current
     * status, and values are emitted when this form's fields changes their inner status.
     *
     * The form status is initially [FormStatus.Unmodified], and becomes [FormStatus.Incomplete] as
     * its fields begin to change their inner status. It becomes [CORRECT] when
     * all the fields status are [CORRECT].
     *
     * If only one field is [INCORRECT] then the form status becomes [FormStatus.Incorrect] even if
     * the other fields are [UNMODIFIED]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val status : Flow<FormStatus> = _fields.flatMapLatest { fieldsMap ->
        combine(fieldsMap.values.map { it.status }) { fieldStatuses ->
            var unmodifiedFieldStatuses = 0
            var correctFieldStatuses = 0
            var failedFieldStatus = 0

            for (fieldStatus in fieldStatuses) {
                when (fieldStatus.code) {
                    UNMODIFIED -> unmodifiedFieldStatuses++
                    CORRECT -> correctFieldStatuses++
                    else -> {
                        failedFieldStatus++
                        // fail fast. It is not required to check the other fields at the moment. Update if needed.
                        break
                    }
                }
            }

            when {
                unmodifiedFieldStatuses == fieldStatuses.size -> FormStatus.Unmodified
                correctFieldStatuses == fieldStatuses.size -> FormStatus.Correct
                failedFieldStatus > 0 -> FormStatus.Incorrect
                else -> FormStatus.Incomplete
            }
        }
    }

    /**
     * Defines the map of fields contained in this form and returns itself to chain more methods
     * and allow to instance the Form in a declarative way.
     */
    fun withFields(vararg fields : FField) : FForm {
        val fieldsMap = fields.associateBy { it.id }
        this._fields.value = fieldsMap
        return this
    }

}
