---
layout: default
title: FlowForms iOS quickstart
---

## iOS quickstart

Continuing from the [KMP quickStart](kmp-quickstart)

**1 :** Add the [KMPNativeCoroutines library](https://github.com/rickclephas/KMP-NativeCoroutines) that will help you to listen to flow state changes from swift  

**2 :** Create an instance of **FormModel** within our **FormState** or class that stores the state of the form:

<pre><code class="swift">
final class FormState: ObservableObject {
  let formModel = FormModel()
}
</code></pre>

<p class="comment">In Swift, we can bind all the properties of formModel to their respective IDs. To achieve this, we can use this tool included in the iOS library source code: </p>

<pre><code class="swift">
bind(
    fieldValue field: String,
    id: String,
    completion: @escaping (String) -> Void
) -> Binding<`String`>
</code></pre>

<p class="comment"> The implementation will be: </p>

<pre><code class="swift">
class FormState: ObservableObject {
    let formModel = FormModel()
    
    var name: Binding<`String`> {
        formModel.form.bind(
            fieldValue: formModel.username,
            id: FormModel.companion.USERNAME
        ) {
            self.formModel.username = $0
            self.objectWillChange.send()
        }
    }
    
    var password: Binding<`String`> {
        formModel.form.bind(
            fieldValue: formModel.password,
            id: FormModel.companion.PASSWORD
        ) {
            self.formModel.password = $0
            self.objectWillChange.send()
        }
    }
}
</code></pre>

<p class="comment"> In the View we can respond to those Bindings in this way: </p>

<pre><code class="swift">
struct FormView: View {
  @ObservedObject var formState: FormState
	
    var body: some View {
        VStack {
            TextField("Username textfield", text: formState.username)
            SecureField("Password textfield", text: formState.password)
        }
    }
}
</code></pre>

<p class="comment"> In Kotlin, we are getting each field using the field(...) function and then collecting (observing) their status. We display an error message in their input layouts when the field is incorrect (i.e. a field's validation failed). Both REQUIRED_UNSATISFIED and MIN_LENGTH_UNSATISFIED are specific status codes of the defined fields' validations in the form declaration (on the VM). Additionally, we are collecting the general form status, enabling a "continue" button when the form is correct (i.e. all its fields are correct) and disabling it when any of its fields are incorrect (at least one validation failed on some field).</p>


**3 :** In swift we can declare **@Published** variables to publish the statuses from each field, i.e:

<pre><code class="swift">
class FormState: ObservableObject {
	//...
	@Published var usernameStatus: String = StatusCodes.shared.UNMODIFIED
    @Published var passwordStatus: String = StatusCodes.shared.UNMODIFIED
	//...
}
</code></pre>

<p>To observe any errors or statuses provided by Swift Publisher, we need to bind them with the field we receive from the formModel. The iOS library source code includes a tool that can be used to bind the publishers with the statuses.</p>

<pre><code class="swift">
func bindStatus(withPublisher publisher: inout Published<`String`>.Publisher)
</code></pre>

<pre><code class="swift">
class FormState: ObservableObject {
	//...
	@Published var usernameStatus: String = StatusCodes.shared.UNMODIFIED
    @Published var passwordStatus: String = StatusCodes.shared.UNMODIFIED
	
	init() {
        formModel.form
            .field(id: FormModel.companion.USERNAME)?
            .bindStatus(withPublisher: &$usernameStatus)
   
        formModel.form
            .field(id: FormModel.companion.PASSWORD)?
            .bindStatus(withPublisher: &$passwordStatus)
	}
}
</code></pre>

<p class="comment">Then we can customize the errorMessages that we want to show depending of the UI or wording that we are managing in any case</p>

<pre><code class="swift">
final class FormState: ObservableObject {
	//...
	@Published var usernameStatus: String = StatusCodes.shared.UNMODIFIED
    @Published var passwordStatus: String = StatusCodes.shared.UNMODIFIED
	
	var usernameErrorMessage: String? {
        switch usernameStatus {
        case StatusCodes.shared.REQUIRED_UNSATISFIED:
            return LocalizedString.FormState.nameRequiredError
        default:
            return nil
        }
  }
  
    var passwordErrorMessage: String? {
        switch passwordStatus {
        case StatusCodes.shared.MIN_LENGTH_UNSATISFIED:
            return LocalizedString.FormState.passwordMinLengthError
        case StatusCodes.shared.REQUIRED_UNSATISFIED:
            return LocalizedString.FormState.passwordRequiredError
        default:
            return nil
        }
    }
	//...
}
</code></pre>
<p class="comment">As we can see in the above snippet, we are responding to the publisher statuses that we capture from the `StatusCodes` types that exist in the library source code.</p>

<p>In the view we can implement this error responses in this way:</p>

<pre><code class="swift">
struct FormView: View {
    @ObservedObject var formState: FormState
	
    var body: some View {
        VStack {
            TextField("Username textfield", text: formState.username)
            if let usernameErrorMessage = formState.usernameErrorMessage {
                ErrorMessageView(message: usernameErrorMessage)
            }
            SecureField("Password textfield", text: formState.password)
            if let passwordErrorMessage = formState.passwordErrorMessage {
                ErrorMessageView(message: passwordErrorMessage)
            }
        }
    }
}
</code></pre>

And that's one of the easiest forms we can create using **FlowForms**, we don't need to care about managing the field's validation triggering nor making any complex logic to enable or disable the continue button.
