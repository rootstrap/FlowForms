---
layout: default
title: FlowForms KMP quickstart
---

## Kotlin multi platform quickstart
In order to use FlowForms you just need to do the following in a common module

Declare a Form, its fields, and their validations using our FlowForms DSL :

<pre><code class="kotlin">
class FormModel {

    var userName: String = "",
    var password: String = ""

    val form = flowForm {
        field(USERNAME, Required { userName })
        field(PASSWORD,
            Required { password },
            MinLength(MIN_PASSWORD_LENGTH) { password }
        )
    }

    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val MIN_PASSWORD_LENGTH = 8
    }
}
</code></pre>
<p class="comment">In the above snippet we are declaring a form with two fields (Username & Password). Both fields are required and the password field also requires to have at least 8 characters.</p>

### How to use the library in a Kotlin module

**1 :** Listen to the fields and/or form status : 

<pre><code class="kotlin">
class Foo {
    ...
    private val formModel = FormModel()
    
    suspend fun listenToFlowFormStatus() = coroutineScope {
        formModel.form.fields.value.let {
            launch { it[FormModel.USERNAME]?.status?.collect(::onUserNameStatusChange) }
            launch { it[FormModel.PASSWORD]?.status?.collect(::onPasswordStatusChange) }
            launch { formModel.form.status.collect(::onFormStatusChange) }
        }
    }

    private fun onUserNameStatusChange(status: FieldStatus) {
        when (status.code) {
            REQUIRED_UNSATISFIED -> { /* Display required error */ }
            else -> { /* Hide any error text on this field */ }
        }
    }

    private fun onPasswordStatusChange(status: FieldStatus) {
        when (status.code) {
            REQUIRED_UNSATISFIED -> { /* Display required error */ }
            MIN_LENGTH_UNSATISFIED -> { /* Display password requires x characters error */ }
            else -> { /* Hide any error text on this field */ }
        }
    }

    private fun onFormStatusChange(status: FormStatus) {
        when (status.code) {
            CORRECT -> { /* Enable continue button */ }
            else -> { /* Disable continue button */ }
        }
    }
}
</code></pre>
<p class="comment">In the above snippet we are collecting (observing) our fields' status and reacting to its changes, like displaying an error message when the field is incorrect (ie. a field's validation failed) or hiding it when it becomes correct. 
Both REQUIRED_UNSATISFIED and MIN_LENGTH_UNSATISFIED are status codes of the fields' validations.
Additionally, we are observing the general form status, which is updated whenever a field status change.</p>

**2 :** Call the form's validate functions whenever your inputs are affected : 

<pre><code class="kotlin">
class Foo {
    ...
    private val formModel = FormModel()

    // call this method after collecting the fields and form status
    private fun bindFields() {
        userNameInput.onTextChanged { value ->
            formModel.userName = "value"
            formModel.form.validateOnValueChange(formModel.USERNAME)
        }
        passwordInput.onTextChanged { value ->
            formModel.password = "value"
            formModel.form.validateOnValueChange(formModel.PASSWORD)
        }
    }
}
</code></pre>
<p class="comment">In the above snippet we are triggering the USERNAME field's validations whenever the text changes, these validations are the ones we specify when declaring the form in the Foo class.</p>
<div class="rs-row comment"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment">userNameInput and passwordInput are not part of kotlin's API and does not exists. We just used them to illustrate how to apply the code</div> </div>

### How to use the library in a swift module

Please refer to the [iOS quickStart guide](ios-quickstart)


And that's one of the easiest forms we can create using **FlowForms** in KMP, we don't need to care about managing the field's status manually nor making any complex logic to enable or disable the continue button. 

**FlowForms**'s full potential is better appreciated when making more complex forms. 

For an android usage example you can review the [android example app](https://github.com/rootstrap/FlowForms/tree/main/ExampleApp%20Android/src/main/java/com/rootstrap/flowforms/example) (there is an example using Activity and another one using Fragment).
For an ios usage example you can review the [ios example app](https://github.com/rootstrap/FlowForms/tree/main/ExampleApp%20iOS/flowforms/Screens/Form) (using swiftUI).

However, the example app and this guide may not cover all **FlowForms**'s features, so for a detailed list of all the available features please refer to the [documentation index](documentation-index)

<div class="rs-row comment"> <i class="comment-icon fa-solid fa-wand-magic-sparkles"></i> <div class="comment">Want to collaborate? Fork the repository and make a Pull request! </div> </div>
