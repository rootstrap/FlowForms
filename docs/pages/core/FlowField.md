---
layout: default
title: FlowForms docs - Fields
---

## What is a FlowField?

A FlowField represents a field in a form. It has a state and 0 or more validations. 
Within a `flowForm { ... }` call, we can create a FlowField using the FlowForms DSL :

<pre><code class="kotlin">
val form = flowForm {
    ...
    field("username")
    ...
}
</code></pre>
<p class="comment">Declaring an empty field with a "username" ID. </p>

But that doesn't do much by itself, one of the goals of FlowForms is to provide an easy way to declare the field's validations, which we will cover in the next section

## Adding validations to a field

Whenever we use the `field()` function, we can add an unlimited amount of validations to the field by simply adding them after the field's ID.

<pre><code class="kotlin">
var userName = ""
val form = flowForm {
    ...
    field("username", Required { userName }, MinLength(3) { userName })
    ...
}
</code></pre>
<p class="comment">Declaring an empty field with a "username" ID, a Required validation and a MinLength of 3 characters validation. </p>

In the above example, we are basically declaring a **Required** field with "username" as ID, which also requires to have at least 3 characters. 

Is important to note the `var userName` declared before the form, as whenever we trigger the field's onValueChange validations its two validations (Required and MinLength) will check if the `userName` property meets their criteria, so it is important to update the `userName` var before validating the field.

### There are 3 types of validations that a field can have.

Currently, a field can have 3 types of validations, with the difference on when they are executed :
<pre><code class="kotlin">
var userName = ""
val form = flowForm {
    ...
    field("username") {
        onValueChange(Required { userName })
        onBlur(MinLength(3) { userName })
        onFocus(Required { userName })
    }
    ...
}
</code></pre>
<p class="comment">Declaring an empty field with a "username" ID and different types of validations. </p>

* **OnValueChange validations** : Are usually the most common validations, they are executed when triggering the onValueChange validations.

* **OnBlur validations** : They are executed when triggering the onBlur validations, also known as "when the field loses the focus".

* **OnFocus validations** : They are executed when triggering the onFocus validations, also known as "when the field gains focus".

In the above example, we are declaring a "username" field that is Required and must at least have 3 characters, the same use case as the other examples. However, we are declaring that the `Required` validation will be executed only when the field's value changes or when it gains focus _(onFocus)_, but not when it loses focus. And we are also declaring that the `MinLength` validation will be executed only when the field loses focus _(onBlur)_, so the user doesn't see the error while typing.

This flexibility allows us to define different behaviors for the different situations a field can have in our apps easily, allowing us to provide better UI experiences with minimum effort.


## Field state

As you have seen, the `userName` variable is independent from the form itself, and it is returned by the functions we add to the Validations (between the brackets `{ }` added after declaring the Validation). 

We did it that way so it is easy to customize the value of our variables before validating them, for example, we can have a phoneNumber with a mask, and we can add or remove the mask before validating the field, or we can declare different validations for different sections of the phone number and then split the phone number into its various "sections" and send each section to their specific Validations. The use cases are a lot and can be as complex as the human brain can go.

### But before we mentioned that the field has a state, if the `userName` value is not the state of the field, then what is it?

Well, when we refer to the Field's state, we are not talking about the data bounded to that field (the `userName`). We talk about the **status** of the field, which represent if the field's validations has been triggered or not, and their respective results. For example, a field can be in a **Correct** status if **all** its validations were triggered and were all successful.

Basically, each of the field status represents one possible situation a field can have : 
* **UNMODIFIED** : `No validations were triggered in the field.`
* **CORRECT** : `All validations in the field were triggered and were successful.`
* **IN_PROGRESS** : `The field is processing one or more asynchronous validations.`
* **INCOMPLETE** : `There are some validations in the Field that were executed and were successful, but not all validations were executed yet. This is the case when, for example, there are OnValueChange validations and onBlur validations, and only the onValueChange validations were triggered (because the user didn't removed the focus from the field yet)`
* **INCORRECT** : `There is a failing validation without an specific error code, or there are more than one validation failing, which could happen when setting failFast as false on a Validation.`
* **Custom status codes** : `The field is in an incorrect status but only one Validation was not successful, so instead of just being "Incorrect" the new status code of the field is the result code of the failing Validation.` 

Each one of the built-in Validations has its own custom error status code, which we can see in the [built-in validations section](Validation#built-in-validations).

## Reacting to the field status

Generally, we will react to the changes in our fields' status in the next way:

<pre><code class="kotlin">
...
private suspend fun listenStatusChanges() {
    form.fields.value["username"]?.status?.collect { status ->
        when (status.code) {
            REQUIRED_UNSATISFIED -> { /* Show field required message */ }
            MIN_LENGTH_UNSATISFIED -> { /* Show field must have at least 3 characters message */ }
            else -> { /* Hide any error message */ }
        }
    }
}
</code></pre>
<p class="comment">Listening to status changes on a field with "username" ID.</p>

In the above example, status.code is where we got the status identifier, which will be one of the mentioned options in the previous list. 
There we are showing a custom error message for each of our validations' custom error codes and we are just hiding the error messages for any other field status, because in our case we don't need to care about the other possible status codes, but it is good to know that they are there, as they are be useful for different use cases.

## Accessing Validation Results on the field's status

Sometimes, we may want to get additional information from the validations executed in a Field, for example, which validations failed in an Incorrect field with various `non failFast` validations. For such cases we can get the `validationResults` from within the same status object:

<pre><code class="kotlin">
...
private suspend fun listenStatusChanges() {
    form.fields.value["username"]?.status?.collect { status ->
        val validationResults = status.validationResults
    }
}
</code></pre>
<p class="comment">Getting the ValidationResults on a field with "username" ID whenever its status changes.</p>

The validation results is a list containing the results of all the validations **completely executed**, being them successful or failure.

Each of these validation results contains a `resultId` (for example, `REQUIRED_UNSATISFIED` in the case of the Required validation), and an `extras` map of String to Any.

Please refer to the [Validations page](Validation) for more information.