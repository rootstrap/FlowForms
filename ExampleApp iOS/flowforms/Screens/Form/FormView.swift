//
//  FormView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//

import SwiftUI
import shared

struct FormView: View {
  
  @ObservedObject private var formManager: FormManager
  @State var showPrompt: Bool = false
  
  init(formManager: FormManager = FormManager()) {
    self.formManager = formManager
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
            title: LocalizedString.FFormView.nameTextfieldTitle,
            value: formManager.name,
            errorMessage: formManager.nameErrorMessage
          )
          HStack {
            FormModelTextView(
              title: LocalizedString.FFormView.emailTextfieldTitle,
              value: formManager.email,
              errorMessage: formManager.emailErrorMessage
            )
            if formManager.emailVerificationInProgress {
              ProgressView()
            }
          }
          FormModelTextView(
            title: LocalizedString.FFormView.passwordTextfieldTitle,
            secureField: true,
            value: formManager.password,
            errorMessage: formManager.passwordErrorMessage
          )
          FormModelTextView(
            title: LocalizedString.FFormView.passwordConfirmationTextfieldTitle,
            secureField: true,
            value: formManager.confirmPassword,
            errorMessage: formManager.confirmedPasswordErrorMessage
          )
        }
        .padding([.leading, .trailing], UI.Padding.large)
        .frame(alignment: .center)
        Toggle(isOn: formManager.termsAccepted) {
          Text(LocalizedString.FFormView.termsAndConditionsText)
        }
        .tint(.pink)
        .padding([.leading, .trailing], UI.Padding.large)
        Spacer()
        Button {
         ///bind to form
        } label: {
          Text(LocalizedString.FFormView.signUpTitle)
            .foregroundColor(.black)
        }
        .disabled(!formManager.formValid)
        .frame(
          maxWidth: .infinity,
          maxHeight: UI.FFormView.signUpButtonHeight,
          alignment: .center
        )
        .padding(.bottom, UI.Padding.medium)
      }
      /// Harcoded response
      .showPrompt(
        $showPrompt,
        style: .success,
        message: LocalizedString.FFormView.successfullySignUpMessage
      )
      .navigationBarTitleDisplayMode(.large)
      .navigationTitle(LocalizedString.FFormView.signUpTitle)
      .background(Color.FFbackground)
    }
  }
}

extension LocalizedString {
  enum FFormView {
    static let termsAndConditionsText = "terms_conditions_text".localized
    static let signUpTitle = "signup_title".localized
    static let successfullySignUpMessage = "successfully_signup".localized
    static let nameTextfieldTitle = "name_textfield_title".localized
    static let emailTextfieldTitle = "email_textfield_title".localized
    static let passwordTextfieldTitle = "password_textfield_title".localized
    static let passwordConfirmationTextfieldTitle = "password_confirmation_textfield_title".localized
  }
}

private extension UI {
  enum FFormView {
    static let signUpButtonHeight: CGFloat = 40
  }
}
