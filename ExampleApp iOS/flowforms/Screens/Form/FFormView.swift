//
//  FFormView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//

import SwiftUI
import shared

struct FFormView: View {

  @ObservedObject private var viewModel: FFormViewModel
  @State var showPrompt: Bool = false
  init(viewModel: FFormViewModel) {
    self.viewModel = viewModel
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
          FFTextFieldView(configuration: $viewModel.nameConfiguration)
          FFTextFieldView(configuration: $viewModel.emailConfiguration)
          FFTextFieldView(configuration: $viewModel.passwordConfiguration)
          FFTextFieldView(configuration: $viewModel.passwordConfirmationConfiguration)
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
            viewModel.isValidData {
            signUp()
          }
        } label: {
          Text(LocalizedString.FFormView.signUpTitle)
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
