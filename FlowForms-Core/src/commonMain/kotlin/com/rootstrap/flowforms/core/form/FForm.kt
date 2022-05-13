package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.common.StatusCodes.CORRECT
import com.rootstrap.flowforms.core.common.StatusCodes.UNMODIFIED
import com.rootstrap.flowforms.core.field.FField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class FForm {

    private val _fields = MutableStateFlow(mapOf<String, FField>())
    val fields = _fields.asStateFlow()

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

    fun withFields(vararg fields : FField) : FForm {
        val fieldsMap = fields.associateBy { it.id }
        this._fields.value = fieldsMap
        return this
    }

}
