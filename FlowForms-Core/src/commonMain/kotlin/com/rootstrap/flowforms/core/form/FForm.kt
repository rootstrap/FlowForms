package com.rootstrap.flowforms.core.form

import com.rootstrap.flowforms.core.field.FField

class FForm {

    val fields = mutableMapOf<String, FField>()

    fun withFields(fields : List<FField>) : FForm {
        fields.forEach {
            this.fields[it.id] = it
        }

        return this
    }

}
