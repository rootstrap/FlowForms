---
layout: default
title: FlowForms docs - Validations
---

## What is a Validation?

a Validation is a class that represents a validation process for a given value. It is used by the FlowFields to determine its state, and can be parameterized to change how it will behave in a field.

In fact, A validation is an abstract class with some optional parameters to define how the client will use them and a `validate()` method that must be implemented by any specific Validation type.

## The ValidationResult

The `validate()` method mentioned before doesn't receive any input, and returns a ValidationResult. 

The ValidationResult has two properties, a `resultId` and an `extras` map. And in the case the validation was successful, the `resultId` must be the `CORRECT` constant, as it determines that the validation was correct and it is what a Field verifies to determine its status.

If the validation is not successful, then any string can be returned as a `resultId`, (we encourage to create constants for the custom Validations, as they will make the code easier to understand and more maintainable)

The `extras` map is a map of String to Any, which is helpful when we need to share data from the Validation to the field status client, like for example, when there is a web request error that we want to handle outside of the validation.

## Built-in Validations

There are various out of the box validations that covers the majority of common use cases in forms so we don't need to implement them over and over across different projects:
* **Required** : `Validates if a String is not null nor empty.` 
* **RequiredTrue** : `Validates if a boolean is not null and is true.`
* **RequiredFalse** : `Validates if a boolean is not null and is false.`
* **MaxLength** : `Validates if a string has more than the desired amount of characters.`
* **MinLength** : `Validates if a string has less than the desired amount of characters.`
* **Match** : `Validates if an object is equal to another object by using == (.equals()).`
* **MatchRegex** : `Validates if a string matches the specified regex.`
* **BasicEmailFormat** : `Validates if a string matches a basic email format.`

For further details we can take a look at the Kdoc in each of the classes, where the ValidationResults for the failure cases are displayed.

## FailFast validations

`failFast` is one of the properties that we can configure when creating a Validation object. It determines if the field's validation process should stop or continue when this validation is not fulfilled. 

<pre><code class="kotlin">
var userName = ""
val form = flowForm {
    field("username", Required(failFast = false) { field })
}
</code></pre>
<p class="comment">Setting failFast as false in the Required Validation for the userName</p>

If the validation is not fulfilled and `failFast` is true, then the field's validation process will stop inmediatelly, updating its state based on the sum of all the ValidationResults of the validations already executed.

If the validation is not fulfilled and `failFast` is false, then the field's validation process will continue and the ValidationResult of this validation will be delivered once the process finishes.

<div class="rs-row comment"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment"> failFast is true by default on all Validations </div> </div>

### Async validations

One of the goals of FlowForms is to run validations asynchronously as easy as possible. And to do that, we only to set the `async` property as true when creating a Validation object.

<pre><code class="kotlin">
var userName = ""
val form = flowForm {
    field("username", Required(async = true) { userName })
    dispatcher = Dispatchers.IO
}
</code></pre>
<p class="comment">Setting async as true in the Required Validation for the username field</p>

`async` makes the validation to be executed after all the regular validations finished their execution (so we don't trigger them when unneded), it will also make the validation to run under a different coroutine, given by the flowForm's coroutineDispatcher.

as you can see in the example above, we are setting `dispatcher = Dispatchers.IO`, which sets an asynchronous coroutineDispatcher to the form (Dispatchers.IO). This is a required attribute that we need to set in the form when we are using asynchronous validations.

<div class="rs-row comment"> <i class="comment-icon fa fa-exclamation-triangle"></i> <div class="comment"> Dispatchers.IO is an android coroutine dispatcher for IO operations, you may need to use a different one based on your platform.</div> </div>

If a field has multiple asynchronous validations, all of them will be started at the same time. By default, if one of them is not fulfilled, the rest of the async validations will be cancelled automatically and the ValidationResults will be delivered to the field, updating its status. The `failFast` rule also applies here, modifying the mentioned behavior.

When an async validation is about to start, the field's status is updated with the `IN_PROGRESS` status code. Which indicates that all the regular validations were successful, but the final status of the field can not be determined yet because there are some validations still in progress.

Whenever we **re-trigger** the field's validations, all current async validations will be cancelled automatically to avoid wasting unnecesaary resources, and a new and fresh field validation process will be started.

<div class="rs-row comment"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment"> async is false by default on all Validations </div> </div>

### Custom validations

creating custom validations is a pretty easy process. In fact, all our built-in validations are pre-defined custom validations. Let's see the Required Validation implementation as an example :

<pre><code class="kotlin">
class Required(
    failFast : Boolean = true,
    async : Boolean = false,
    val valueProvider: () -> String?
) : Validation(failFast = failFast, async = async) {

    override suspend fun validate() = ValidationResult(
        if (valueProvider().isNullOrEmpty())
            REQUIRED_UNSATISFIED
        else
            CORRECT
    )
}
</code></pre>
<p class="comment">Snippet of the Required custom validation</p>

As you may see in the Required class snippet, the only thing we need to do is to implement the `Validation` abstract class and its `validate()` method. However, the Required class do a bit more things in order to be "flexible" enough for any possible use case, but don't worry, they are **optional**.

It declares the `failFast` and `async` properties and send them to the Validation class, this is to allow to customize the  behavior in the validation process when using this specific Validation, as mentioned in the previous sections.

Another thing it does is to define the `valueProvider` required val. This is a function that we use to provide the value being validated in real time. Inside the `validate()` function implementation we can see that this `valueProvider` is used right in the immediate `if()` clause.

The `validate()` function requires us to provide a `ValidationResult` instance, and when creating one we can define any string as the `resultId` to represent that the Validation was not fulfilled. However, if the validation is successful we must use the `CORRECT` resultId.
Another thing we can add to the `ValidationResult` is the `extras` property, which is a `Map` of `String` to `Any` that we can use to communicate details from the validation to the field status' client.

<div class="rs-row comment"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment"> As a side note, the `validate()` function is a suspend function, so we can execute any IO/background code safely (as long as we use it in combination with `async=true` or run those process in a different coroutine). </div> </div>


## Changing the field's validation behavior

By default all the Fields use the `DefaultFieldValidationBehavior` class to manage the validation process, including managing its asynchronous validations and how the field status is delivered.

If you want to modify how the validation behavior process works you can create you own by implementing the `FieldValidationBehavior` interface and defining the `validationBehavior` property when creating a Field using the FlowForms DSL

<pre><code class="kotlin">
var userName = ""
val form = flowForm {
    field("username", Required { userName } ) {
        validationBehavior = MyCustomValidationBehavior()
    }
}
</code></pre>
<p class="comment">Setting a custom ValidationBehavior on a field</p>

Usually you will not need to create a custom validation behavior as the `DefaultFieldValidationBehavior` should cover almost any use case. We encourage to raise a new issue in the repository if there is something we are not taking into account, so "creating a custom validation behavior" should be the last resort, as we can not garantee that the library is going to work as expected (because you are customizing it).

<div class="rs-row comment"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment"> Cancelling the current validations when re-triggering them is done at the Field level, so it will not be overriden when using a custom validation behavior.</div> </div>
