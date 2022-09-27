---
layout: default
title: FlowForms KMP quickstart
---

## Kotlin multi plaftform quickstart
In order to use FlowForms you just need to make 3 steps :

**1 :** Declare a Form, its fields, and their validations using our FlowForms DSL :

<pre><code class="kotlin">
class Foo {

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

**2 :** Listen to the fields and/or form status : 

<pre><code class="kotlin">
class Bar {
    ...
    private val foo = Foo()
    
    override fun listenToFlowFormStatus() {
        foo.form.fields.value.let {
            launch { it[SignUpViewModel.USERNAME]?.status?.collect(::onUserNameStatusChange) }
            launch { it[SignUpViewModel.PASSWORD]?.status?.collect(::onPasswordStatusChange) }
            launch { viewModel.form.status.collect(::onFormStatusChange) }
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

**3 :** Call the form's validate functions whenever your inputs are affected : 

<pre><code class="kotlin">
class Bar {
    ...
    private val foo = Foo()

    // call this method after collecting the fields and form status
    private fun bindFields() {
        userNameInput.onTextChanged { value ->
            foo.userName = "value"
            foo.form.validateOnValueChange(foo.USERNAME)
        }
        passwordInput.onTextChanged { value ->
            foo.password = "value"
            foo.form.validateOnValueChange(foo.PASSWORD)
        }
    }
}
</code></pre>
<p class="comment">In the above snippet we are triggering the USERNAME field's validations whenever the text changes, these validations are the ones we specify when declaring the form in the Foo class.</p>
<div class="rs-row"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment">userNameInput and passwordInput are not part of kotlin's API and does not exists. We just used them to illustrate how to apply the code</div> </div>

And that's one of the easiest forms we can create using **FlowForms** in KMP, we don't need to care about managing the field's status manually nor making any complex logic to enable or disable the continue button. 

**FlowForms**'s full potential is better appreciated when making more complex forms, for an android usage example you can review the [android example app](https://github.com/rootstrap/FlowForms/tree/main/ExampleApp%20Android/src/main/java/com/rootstrap/flowforms/example) included the project. Which makes use of asynchronous validations and many other capabilities. You will see that the implementation steps doesn't change at all, except that it uses some specific android utilities in the UI layer. BTW, there is an example using Activity and another one using Fragment.

<div class="rs-row center-second-axis"> <i class="comment-icon fa-solid fa-wand-magic-sparkles"></i> <div class="comment">A multi platform example app would be very helpful and a very good addition to the library. Want to collaborate? Fork the repository and make a Pull request! </div> </div>

However, the example app and this guide does not cover all **FlowForms**'s features, so for a detailed list of all the available features please refer to the [documentation index](pages/documentation-index)
