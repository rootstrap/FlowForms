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
  @ObservedObject private var viewModel: FFormViewModel
  
  @State var showPrompt: Bool = false
  @State var errorHeight: CGFloat = 0
  init(viewModel: FFormViewModel, formManager: FormManager = FormManager()) {
    self.viewModel = viewModel
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
          VStack {
            FormModelTextView(
              title: "Name",
              value: formManager.name
            )
            Text("Enter a valid name")
              .frame(maxWidth: .infinity, maxHeight: errorHeight, alignment: .leading)
              .opacity(errorHeight == 0 ? 0 : 1)
            .onReceive(formManager.$nameValid) { value in
              errorHeight = value ? 0 : 30
            }
          }
          
          FormModelTextView(
            title: "Email",
            value: formManager.email
          )
          FormModelTextView(
            title: "Password",
            value: formManager.password
          )
          FormModelTextView(
            title: "Confirm password",
            value: formManager.confirmPassword
          )
        }
        .padding([.leading, .trailing], UI.Layout.largePadding)
        .frame(alignment: .center)
        Toggle(isOn: $viewModel.termsAccepted) {
          Text(LocalizedString.FFormView.termsAndConditionsText)
        }
        .tint(.pink)
        .padding([.leading, .trailing], UI.Layout.largePadding)
        Spacer()
        Button {
          if
            viewModel.termsAccepted,
            formManager.isValid {
            signUp()
          }
        } label: {
          Text(LocalizedString.FFormView.signUpTitle)
            .foregroundColor(formManager.isValid ? .black : .gray)
        }
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
