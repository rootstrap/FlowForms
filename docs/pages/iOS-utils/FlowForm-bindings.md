---
layout: default
title: FlowForms docs - Forms
---

### FlowForm bindings

The FlowForms iOS library is designed to simplify the process of handling form data in your Swift code.
It provides a function that allows you to bind a publisher in your Swift code with the real-time status generated by the FlowForm.

<pre><code class="swift">
    formModel.form.bindStatus(withPublisher: &$formStatus)
</code></pre>
<p class="comment">The above code allows you to observe the status of the formModel and determine whether it is valid (i.e., when all the fields are valid).</p>

#### Fields binding

Also there are functions that allow us to bind the form fields to swift @Published variables and react to them directly from the view 
<pre><code class="swift">
     var username: Binding&lt;String&gt; {
        formModel.form.bind(
            fieldValue: formModel.username,
            id: FormModel.companion.USERNAME
        ) {
            self.formModel.username = $0
            self.objectWillChange.send()
        }
    }
</code></pre>
<p class="comment">The above code allows you to observe and bind the `formModel.username` field with its ID, enabling it to update in real-time. Additionally, it validates its content using the `validateOnValueChange` feature.</p>

#### Switcher binding
For switcher controls, we have a special function that allows you to observe the `termsAccepted` state and react to it:

<pre><code class="swift">
     var termsAccepted: Binding&lt;Bool&gt; {
        formModel.form.bindSwitch(
            field: formModel.termsAccepted,
            id: FormModel.companion.TERMS_ACCEPTED
        ) {
            self.formModel.termsAccepted = $0
            self.objectWillChange.send()
        }
    }
</code></pre>
<p class="comment">This code example demonstrates how to bind the formModel.termsAccepted with its ID, allowing you to observe the switcher's state changes and react to them in real-time.</p>

To gain a better understanding of the implementation of these tools and their usage in the View, you can explore the [iOS example app](https://github.com/rootstrap/FlowForms/tree/main/ExampleApp%20iOS/flowforms/Screens/Form)