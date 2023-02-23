//
//  FFormView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//

import SwiftUI
import shared

struct FFormView: View {
  
  @ObservedObject private var formManager: FormManager
  @State var showPrompt: Bool = false
  @State var nameErrorHeight: CGFloat = 0
  @State var emailErrorHeight: CGFloat = 0
  @State var passwordErrorHeight: CGFloat = 0
  @State var confirmedPasswordErrorHeight: CGFloat = 0

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
          VStack(spacing: .zero) {
            FormModelTextView(
              title: "Name",
              value: formManager.name
            )
            Text("Enter a valid name")
              .font(.caption)
              .frame(maxWidth: .infinity, maxHeight: nameErrorHeight, alignment: .leading)
              .foregroundColor(.red)
              .opacity(nameErrorHeight == 0 ? 0 : 1)
            .onReceive(formManager.nameComprobations) { invalidName in
              nameErrorHeight = invalidName ? 10 : 0
            }
          }
          
          HStack {
            VStack(spacing: .zero) {
              FormModelTextView(
                title: "Email",
                value: formManager.email
              )
              Text("Enter a valid email")
                .font(.caption)
                .frame(maxWidth: .infinity, maxHeight: emailErrorHeight, alignment: .leading)
                .foregroundColor(.red)
                .opacity(emailErrorHeight == 0 ? 0 : 1)
              .onReceive(formManager.emailComprobation) { invalidEmail in
                emailErrorHeight = invalidEmail ? 10 : 0
              }
            }
            if formManager.emailVerificationInProgress {
              ProgressView()
            }
          }
          VStack {
            FormModelTextView(
              title: "Password",
              value: formManager.password
            )
            Text("Enter a valid password")
              .font(.caption)
              .frame(maxWidth: .infinity, maxHeight: passwordErrorHeight, alignment: .leading)
              .foregroundColor(.red)
              .opacity(passwordErrorHeight == 0 ? 0 : 1)
            .onReceive(formManager.passwordComprobation) { invalidPassword in
              passwordErrorHeight = invalidPassword ? 10 : 0
            }
          }
          
          VStack {
            FormModelTextView(
              title: "Confirm password",
              value: formManager.confirmPassword
            )
            Text("Passwords don't match")
              .font(.caption)
              .frame(maxWidth: .infinity, maxHeight: confirmedPasswordErrorHeight, alignment: .leading)
              .foregroundColor(.red)
              .opacity(confirmedPasswordErrorHeight == 0 ? 0 : 1)
            .onReceive(formManager.confirmedPasswordComprobation) { invalidMatch in
              confirmedPasswordErrorHeight = invalidMatch ? 10 : 0
            }
          }
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
