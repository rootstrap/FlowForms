---
layout: default
title: FlowForms iOS quickstart
---

## iOS quickstart

Continuing from the [KMP quickStart](kmp-quickstart)

**1 :** Create an instance of **SignupViewModel** within our **FormState** or class that stores the state of the form:

<pre><code class="swift">
import shared
import SwiftUI

final class FormState: ObservableObject {
  let viewModel = SignupViewModel()

  init() { }
}
</code></pre>

<p class="comment">Since SignupViewModel is located within the common module and it is pure kotlin code we can use it directly in iOS</p>

**2 :** Now, we can declare a **@Published** property of type **SignUpFormUiState** to observe state changes from **SignupViewModel**, which holds the states of all the fields

<pre><code class="swift">
import shared
import SwiftUI

final class FormState: ObservableObject {
  let viewModel = SignupViewModel()
 
  @Published var uiState: SignUpFormUiState?
  
  init() {
    viewModel.observeUiState { state in
      self.uiState = state
    }
  }
}
</code></pre>

<p>In the view, we can implement the error responses and react to the formStatus as follows:</p>

<pre><code class="swift">
struct FormView: View {
    @ObservedObject var formState: FormState
	
    var body: some View {
        VStack {
            TextField(
                "Email textfield", 
                text: Binding(
                    get: { formState.uiState?.email ?? "" },
                    set: { formState.viewModel.onEmailChange(value: $0) }
                )
            )
            if let emailErrorMessage = formState.uiState?.emailError {
                ErrorMessageView(message: emailErrorMessage)
            }

            SecureField(
                "Password textfield", 
                text: Binding(
                    get: { formState.uiState?.password ?? "" },
                    set: { formState.viewModel.onPasswordChange(value: $0) }
                )
            )
            if let passwordErrorMessage = formState.uiState?.passwordError {
                ErrorMessageView(message: passwordErrorMessage)
            }
            ...
            Button {
                signUp()
            } label: {
                Text("Sign Up")
                    .foregroundColor(.black)
            }.disabled(formState.uiState?.isFormValid == false)
        }
    }
}
</code></pre>

And that's one of the easiest forms we can create using **FlowForms**, we don't need to care about managing the field's validation triggering or making any complex logic to enable or disable the signup button.

For more complex use cases and detailed insights into advanced functionalities and showcases of the **FlowForms** library on iOS, you can refer to the [iOS example app](https://github.com/rootstrap/FlowForms/tree/main/ExampleApp%20iOS) in the official repository. This comprehensive example provides
