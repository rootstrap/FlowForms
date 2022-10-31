---
layout: default
title: FlowForms docs - Forms
---

## Form bindings

At a glance, to trigger the Form's field validations we need to call its trigger validation methods manually, but that seems to be too much boilerplate code, and none of us wants that, right?. 

### View to FlowField binding 

Well, for android projects we have a `FlowForm.bind(...)` extension method that helps us reduce that boiler plate. In this case this "bind" function allow us to bind a field in our FlowForm to a View in the UI, which will automatically take care of triggering the FlowForm's `onValueChange`, `onFocus`, and `onBlur` validations for each field we specify when such events occur.

<pre><code class="kotlin">
private fun bindFields() {
    binding?.apply {
        viewModel.form.bind(lifecycleScope,
            nameInputEditText to SignUpFormModel.NAME,
            emailInputEditText to SignUpFormModel.EMAIL,
            passwordInputEditText to SignUpFormModel.NEW_PASSWORD,
            confirmPasswordInputEditText to SignUpFormModel.CONFIRM_PASSWORD
        )
    }
}
</code></pre>
<p class="comment">Example extracted from the android example app.</p>

In the above example, we are binding the `nameInputEditText` View to `SignUpFormModel.NAME` (which is a valid field ID in our form). By doing this, we are basically telling FlowForms to automatically call its `validateOnValueChange()`, `validateOnFocus()` and `validateOnBlur` methods, the former whenever the user changes the current value of the View (for example, adds a character in an InputEditText), the second whenever the View obtains focus and the last one whenever the View loses it. Everything in just one line.

In the example we do the same for each field we want to bind on the form by separating the "bind declaration" (ie View to Field ID) with a `,`

#### Supported ViewTypes 

At the moment, for automatic View to FlowField binding we support the following View types : 
 * `EditText`

Trying to use an unsupported View type will result in `IllegalArgumentException`.

<div class="rs-row comment"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment">We will be adding more View types in the future, please feel free to raise an issue with your use case if you don't find the View you are using in the above list.</div> </div>

### LiveData to FlowField binding 

In addition to "View to FlowField binding", we have "LiveData to FlowField binding", which automatically binds a LiveData object to the given FlowField.

<pre><code class="kotlin">
private fun bindFields() {
    binding?.apply {
        ...
        viewModel.form.bind(this@SignUpFormFragment, lifecycleScope,
            viewModel.formModel.confirm to SignUpFormModel.CONFIRMATION
        )
    }
}
</code></pre>
<p class="comment">Example extracted from the android example app.</p>

In this case, whenever the specified LiveData's value change the indicated field's `onValueChange` validations will be triggered. This is very useful for use cases like confirming terms and conditions of use, which require using a Checkbox to define a boolean value.

<div class="rs-row comment"> <i class="comment-icon fa-solid fa-circle-info"></i> <div class="comment">when interacting with a checkbox and using Two-way View binding at the same time, it becomes almost necessary to use a LiveData to get notified of the value changes, because Checkbox Views doesn't allow to have more than one onValueChange listener and doesn't provide any method to get the current listener.</div> </div>


### Why do we need to pass lifecycleScope when using the bind extensions?

The bind methods require to pass the `lifecycleScope` as argument because the validate functions in the FlowForm are `suspending functions`, hence they need to be called from a `coroutine`, and using the `lifecycleScope` gives us the benefit that anything we called will be immediatelly cancelled if the `lifecycleScope` is `cancelled/destroyed`. 

In the case of binding a `LiveData` to a `FlowField`, we also need to pass a `LifecycleOwner` to be able to listen to the `LiveData` changes only when the fragment's/activity's lifecycle is on an active state (`started` or `resumed`). basically we call the `LiveData.observe(...)` method using the given `LifecycleOwner`.

