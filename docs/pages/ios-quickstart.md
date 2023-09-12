---
layout: default
title: FlowForms iOS quickstart
---

## iOS quickstart

Continuing from the [KMP quickStart](kmp-quickstart)

**2 :** Create an instance of **SignupViewModel** within our **FormState** or class that stores the state of the form:

<pre><code class="swift">
import shared
import SwiftUI

final class FormState: ObservableObject {
  let viewModel = SignupViewModel()

  init() { }
}
</code></pre>

<p class="comment"> SignupViewModel is located within the common module and its pure kotlin code so it can be used by both platforms</p>

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
                // Sign up
            } label: {
                Text("Sign Up")
                    .foregroundColor(.black)
            }.disabled(formState.uiState?.isFormValid == false)
        }
    }
}
</code></pre>

And that's one of the easiest forms we can create using **FlowForms**, we don't need to care about managing the field's validation triggering nor making any complex logic to enable or disable the continue button.
