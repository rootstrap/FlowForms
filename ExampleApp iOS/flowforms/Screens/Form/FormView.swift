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
    withAnimation(.spring(response: 0.4, dampingFraction: 0.6, blendDuration: 0.3)) {
      showPrompt = show
    }
  }
  
  var body: some View {
    NavigationView {
      VStack {
        Spacer()
        VStack(spacing: UI.Layout.mediumPadding) {
          FormModelTextView(
            title: "Name",
            value: formManager.name,
            errorMessage: formManager.nameErrorMessage
          )
          HStack {
            FormModelTextView(
              title: "Email",
              value: formManager.email,
              errorMessage: formManager.emailErrorMessage
            )
            if formManager.emailVerificationInProgress {
              ProgressView()
            }
          }
          FormModelTextView(
            title: "Password",
            value: formManager.password,
            errorMessage: formManager.passwordErrorMessage
          )
          FormModelTextView(
            title: "Confirm password",
            value: formManager.confirmPassword,
            errorMessage: formManager.confirmedPasswordErrorMessage
          )
        }
        .padding([.leading, .trailing], UI.Layout.largePadding)
        .frame(alignment: .center)
        Toggle(isOn: formManager.termsAccepted) {
          Text(LocalizedString.FFormView.termsAndConditionsText)
        }
        .tint(.pink)
        .padding([.leading, .trailing], UI.Layout.largePadding)
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
          maxHeight: 40,
          alignment: .center
        )
        .padding(.bottom, 20)
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
  }
}
