//
//  FormView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//

import SwiftUI
import shared

struct FormView: View {
  private let signUpButtonHeight: CGFloat = 40
  
  @ObservedObject private var formState: FormState
  @State var showPrompt: Bool = false
  
  init(formState: FormState = FormState()) {
    self.formState = formState
  }

  func signUp() {
    showPrompt(true)
    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
      showPrompt(false)
    }
  }
  
  func showPrompt(_ show: Bool) {
    withAnimation(.spring(
      response: Animation.Duration.veryShort,
      dampingFraction: Animation.Duration.short,
      blendDuration: Animation.Duration.veryShort
    )) {
      showPrompt = show
    }
  }
  
  var body: some View {
    NavigationView {
      VStack {
        Spacer()
        VStack(spacing: UI.Padding.medium) {
          FormModelTextView(
            title: LocalizedString.FormView.nameTextfieldTitle,
            value: Binding(
              get: { formState.uiState?.name ?? "" },
              set: { formState.viewModel.onNameChange(value: $0) }
            ),
            errorMessage: formState.nameErrorMessage
          )
          HStack {
            FormModelTextView(
              title: LocalizedString.FormView.emailTextfieldTitle,
              value: Binding(
                get: { formState.uiState?.email ?? "" },
                set: { formState.viewModel.onEmailChange(value: $0) }
              ),
              errorMessage: formState.emailErrorMessage
            )
            if formState.isEmailVerificationInProgress {
              ProgressView()
            }
          }
          FormModelTextView(
            title: LocalizedString.FormView.passwordTextfieldTitle,
            isSecureField: true,
            value: Binding(
              get: { formState.uiState?.password ?? "" },
              set: { formState.viewModel.onPasswordChange(value: $0) }
            ),
            errorMessage: formState.passwordErrorMessage
          )
          FormModelTextView(
            title: LocalizedString.FormView.passwordConfirmationTextfieldTitle,
            isSecureField: true,
            value: Binding(
              get: { formState.uiState?.confirmPassword ?? "" },
              set: { formState.viewModel.onPasswordConfirmChange(value: $0) }
            ),
            errorMessage: formState.confirmedPasswordErrorMessage
          )
        }
        .padding([.leading, .trailing], UI.Padding.large)
        .frame(alignment: .center)
        Toggle(isOn: Binding(
          get: { formState.uiState?.termsAccepted ?? false },
          set: { formState.viewModel.onAcceptTermsChange(value: $0) }
        )) {
          Text(LocalizedString.FormView.termsAndConditionsText)
        }
        .tint(.pink)
        .padding([.leading, .trailing], UI.Padding.large)
        Spacer()
        Button {
          // Sign up
        } label: {
          Text(LocalizedString.FormView.signUpTitle)
            .foregroundColor(.black)
        }
        .disabled(!formState.isFormValid)
        .frame(
          maxWidth: .infinity,
          maxHeight: signUpButtonHeight,
          alignment: .center
        )
        .padding(.bottom, UI.Padding.medium)
      }
      /// Harcoded response
      .showPrompt(
        $showPrompt,
        style: .success,
        message: LocalizedString.FormView.successfullySignUpMessage
      )
      .navigationBarTitleDisplayMode(.large)
      .navigationTitle(LocalizedString.FormView.signUpTitle)
      .background(Color.FFbackground)
    }
  }
}

private extension LocalizedString {
  enum FormView {
    static let termsAndConditionsText = "terms_conditions_text".localized
    static let signUpTitle = "signup_title".localized
    static let successfullySignUpMessage = "successfully_signup".localized
    static let nameTextfieldTitle = "name_textfield_title".localized
    static let emailTextfieldTitle = "email_textfield_title".localized
    static let passwordTextfieldTitle = "password_textfield_title".localized
    static let passwordConfirmationTextfieldTitle = "password_confirmation_textfield_title".localized
  }
}
