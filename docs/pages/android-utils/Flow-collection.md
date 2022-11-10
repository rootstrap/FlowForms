---
layout: default
title: FlowForms docs - Forms
---

## repeatOnLifeCycleScope

`repeatOnLifeCycleScope` is an extension function available for the `Fragment` and `AppCompatActivity` classes and subclasses that allows us to easily collect Flows while automatically taking care of the lifecycle changes, cancelling the Flow collection when the `UI is not active` and restarting it when it enters the `Started` state (we can customize it by giving a custom `Lifecycle.State`).

<pre><code class="kotlin">
private fun listenStatusChanges() {
    viewModel.form.fields.value.let {
        repeatOnLifeCycleScope(
            { it[SignUpFormModel.NAME]?.status?.collect(::onNameStatusChange) },
            { it[SignUpFormModel.EMAIL]?.status?.collect(::onEmailStatusChange) },
            { it[SignUpFormModel.NEW_PASSWORD]?.status?.collect(::onPasswordStatusChange) },
            { it[SignUpFormModel.CONFIRM_PASSWORD]?.status?.collect(::onConfirmPasswordChange) },
            { viewModel.form.status.collect(::onFormStatusChange) }
        )
    }
}
</code></pre>
<p class="comment">Example extracted from the android example app.</p>

In the example above we are collection various Flows with just using a single line of code per each one. For example, we are calling `onNameStatusChange` whenever the `NAME` field status changes, sending the new status received to it. And doing the same with each field status and at the end with the form's status

It basically accepts a vararg of suspend functions where we can collect **safely** any type of flow, and internally it takes care of the lifecycle changes and safety measures for us.
