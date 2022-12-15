---
layout: default
title: FlowForms docs - Forms
---

## What is a FlowForm?

A FlowForm represents an application form, which has a form status and an undetermined amount of fields. 

We can build one using our FlowForms DSL : 

<pre><code class="kotlin">
val form = flowForm {

}
</code></pre>

The `flowForm` function generates a FlowForm, which we can customize inside the brackets by adding fields or modifying its properties.

We can add a field to the FlowForm by calling the `field` function on it, and we can call it as many times as fields we need, like the following :

<pre><code class="kotlin">
val form = flowForm {
    field("userName")
    field("email")
    field("password")
}
</code></pre>
<p class="comment">In the above code snippet we are declaring a form that has 3 fields: userName, email, and password.</p>

However, by using the DSL these fields can be further customized, adding validations or modifying their behaviors, which is covered in the [FlowFields page](FlowField).

## Reacting to the form

As mentioned before, a FlowForm has a status, which is updated automatically whenever any of its fields is updated. We can listen to the status changes by collecting its public `status` var, as shown below

<pre><code class="kotlin">
val form = ...

suspend fun listenToFormStatus() {
    form.status.collect { status ->
        when (status.code) {
            StatusCodes.INCORRECT -> { ... }
            StatusCodes.UNMODIFIED -> { ... }
            StatusCodes.INCOMPLETE -> { ... }
            StatusCodes.CORRECT -> { ... }
        }
    }
}
</code></pre>
<p class="comment">In the above code snippet we are declaring a collection function that will be executed any time the form status changes.</p>

As we can see there are 4 possible status codes for the form. Each one representing its current state :

 - **INCORRECT** : `One of the form's fields is either incorrect or has a custom error code.`
 - **UNMODIFIED** : `No fields were validated since form creation.`
 - **INCOMPLETE** : `Some fields were validated but not all of them.`
 - **CORRECT** : `All fields in the form are correct.`

 By reacting to these status you can customize the form's behavior, however, it is not mandatory to react to all of them together.

<p class="comment">As an example, in a sign-up form we can have a "register" button that only enables when the form's status code is CORRECT and disables on any other status.</p>

## Reacting to its fields' status

Previously, we were reacting to the form's status, which is updated automatically whenever its inner fields' status changes, but in addition to that, we can listen to each form's field status individually, allowing us to make changes on the UI easily, like showing an error message under an specific field, or displaying a progress bar when an asynchronous validation is in progress.

Based on the form generated in the [What is a FlowForm? section](#what-is-a-flowform), we can listen to each field's status individually by calling :

<pre><code class="kotlin">
val form = ...
val coroutineScope: CoroutineScope = ...

suspend fun listenToFieldsStatus() {
    coroutineScope {
        form.fields.value.let {
            launch { it["userName"]?.status?.collect(::onUserNameStatusChange) }
            launch { it["email"]?.status?.collect(::onEmailStatusChange) }
            launch { it["password"]?.status?.collect(::onPasswordStatusChange) }
        }
    }
}

private fun onNameStatusChange(status: FieldStatus) {
    when (status.code) {
        ...
    }
}

private fun onEmailStatusChange(status: FieldStatus) {
    when (status.code) {
        ...
    }
}

private fun onPasswordStatusChange(status: FieldStatus) {
    when (status.code) {
        ...
    }
}
</code></pre>
<p class="comment">In the above code snippet we are declaring a collection function for each field that will be executed whenever the specified field status changes.</p>

This way we can react to each field status change and do our custom logic for any case. For more information about the fields possible status check the [Field state section](FlowField#field-state)

## Triggering form validations

We learnt how to build a form, how to react to a form status and also how to react to its fields' status. But at this point nothing will happen, we just declared a form and what we will do when the form (or its fields) changes, so that leads to a question.

### How do we change the form and its fields status?

Basically, the form status changes automatically based on its fields status, so we don't need to care about it. What we are looking for is how to change its fields' status, and actually it is almost automatic too, as their status will change based on the validations the Field has.

So, the only thing we need to do, is just to call the form's triggerValidations method with the specific field ID we are validating when we need to. 
For example, everytime the password input is updated by the user, we just trigger the **password field validations**

<pre><code class="kotlin">
val form = ...
val coroutineScope: CoroutineScope = ...
val passwordInput = ...

fun triggerValidationsWhenInputIsUpdated() {
    passwordInput.doAfterTextChanged {
        coroutineScope.launch { validateOnValueChange(fieldId) }
    }
    passwordInput.doOnBlur {
        coroutineScope.launch { validateOnBlur(fieldId) }
    }
    passwordInput.doOnFocus {
        coroutineScope.launch { validateOnFocus(fieldId) }
    }
}
</code></pre>
<p class="comment">In the above code snippet we are triggering the validations for the password field whenever its value changes (input value was updated), it gains focus (onFocus) and when it loses the focus (onBlur). <br>
Here passwordInput represents an input in your UI library, so it may change based on the library you use. We also provide some binding utilities fot specific platforms. The current example is fictitious so it is not based in any existing library</p>

This will automatically trigger any of the password field's validations, which will then update the field's status based on their results, which will also automatically update the form's status.

Actually, there are 3 types of validations : 

 * OnValueChange validations 
 * OnFocus validations
 * OnBlur validations

To learn more about the validations please refer to the [Adding validations to a field section](FlowField#adding-validations-to-a-field).

If using **Android's UI**, we made a bunch of utilities to reduce even more the code needed for this. To take a look at it please refer to the [Android binding page](../android-utils/Binding)
