---
layout: default
title: FlowForms Android quickstart
---

## Android quickstart
In order to use FlowForms you just need to make 3 steps :

**1 :** Declare a Form, its fields, and their validations using our FlowForms DSL in your ViewModel :

<pre><code class="kotlin">
class SignUpViewModel : ViewModel() {

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
<p class="comment">In the above snippet we are declaring a form with two fields (Username & Password). Both fields are required and the password field also requires to have at least 8 characters. </p>

**2 :** Listen to the fields status on your Fragment or Activity using our repeatOnLifeCycleScope extension : 

<pre><code class="kotlin">
class SignUpFormFragment : Fragment() {
    ...
    private var binding : SignUpFormBindingFragment? = null
    private lateinit var viewModel : SignUpViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SignUpViewModel::class.java] // or use you favorite DI tool
        ...
        viewModel.form.fields.value.let {
            repeatOnLifeCycleScope(
                { it[SignUpViewModel.USERNAME]?.status?.collect(::onUserNameStatusChange) },
                { it[SignUpViewModel.PASSWORD]?.status?.collect(::onPasswordStatusChange) },
                { viewModel.form.status.collect(::onFormStatusChange) }
            )
        }
    }

    private fun onUserNameStatusChange(status: FieldStatus) {
        when (status.code) {
            REQUIRED_UNSATISFIED -> binding?.usernameInputLayout.error = "this field is required"
            else -> binding?.usernameInputLayout.error = null
        }
    }

    private fun onPasswordStatusChange(status: FieldStatus) {
        when (status.code) {
            REQUIRED_UNSATISFIED -> binding?.passwordInputLayout.error = "this field is required"
            MIN_LENGTH_UNSATISFIED -> binding?.passwordInputLayout.error = "Should contain at least 8 characters"
            else -> binding?.passwordInputLayout.error = null
        }
    }

    private fun onFormStatusChange(status: FormStatus) {
        when (status.code) {
            CORRECT -> binding?.continueButton.isEnabled = true
            else -> binding?.continueButton.isEnabled = false
        }
    }
}
</code></pre>
<p class="comment">In the above snippet we are collecting (observing) our fields' status and displaying an error message in their input layouts when the field is incorrect (ie. a field's validation failed). Both REQUIRED_UNSATISFIED and MIN_LENGTH_UNSATISFIED are status codes of the fields' validations.
Additionally, we are observing the general form status, enabling a "continue" button when the form is correct (ie. all its fields are correct) and disabling it when any of its fields are incorrect (at least one validation failed on some field).</p>

**3 :** Bind your input views to the form fields using our bind form's extension function : 

<pre><code class="kotlin">
class SignUpFormFragment : Fragment() {
    ...
    // call this method after collecting the fields and form status
    private fun bindFields() {
        binding?.apply {
            viewModel.form.bind(lifecycleScope,
                usernameInputEditText to SignUpFormModel.USERNAME,
                passwordInputEditText to SignUpFormModel.PASSWORD
            )
        }
    }
}
</code></pre>
<p class="comment">In the above snippet we are binding the userName input to the username field in the form, and the password input to the password field. This automatically calls the respective field's validations when any of the inputs change, in our case it is when the user types something in them.</p>

And that's one of the easiest forms we can create using **FlowForms**, we don't need to care about managing the field's validation triggering nor making any complex logic to enable or disable the continue button. 
This quickstart example is also meant to be used with two-way databinding, setting and using our fields' values directly in our xml layouts as the following :
<pre><code class="xml">
android:text="@={viewModel.userName}"
</code></pre>
For further information about two-way databiding, refer to [this official documentation](https://developer.android.com/topic/libraries/data-binding/two-way). However, the snippets above can be easily adapted to avoid using two-way databinding.

**FlowForms**'s full potential is better appreciated when making more complex forms, you can review the [android example app](https://github.com/rootstrap/FlowForms/tree/main/ExampleApp%20Android/src/main/java/com/rootstrap/flowforms/example) included in the project. Which makes use of asynchronous validations and many other capabilities. You will see that the implementation steps doesn't change at all. BTW, there is an example using Activity and another one using Fragment.

However, the example app and this guide does not cover all **FlowForms**'s features, so for a detailed list of all the available features please refer to the [documentation index](pages/documentation-index)
