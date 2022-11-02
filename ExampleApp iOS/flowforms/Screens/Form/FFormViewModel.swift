//
//  FFormViewModel.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import Combine

final class FFormViewModel: ObservableObject {
  
  @Published var termsAccepted = false
  @Published var nameConfiguration = FFTextFieldViewConfiguration(
    title: LocalizedString.SignUpView.nameTitle,
    validations: [.nonEmpty],
    errorMessage: LocalizedString.SignUpView.nameTextFieldError
  )
  
  @Published var emailConfiguration = FFTextFieldViewConfiguration(
    title: LocalizedString.SignUpView.emailTitle,
    validations: [.email, .nonEmpty],
    errorMessage: LocalizedString.SignUpView.emailTextFieldError
  )
  @Published var passwordConfiguration = FFTextFieldViewConfiguration(
    title: LocalizedString.SignUpView.passwordTitle,
    validations: [.nonEmpty],
    isSecure: true,
    errorMessage: LocalizedString.SignUpView.passwordTextFieldError
  )
  @Published var passwordConfirmationConfiguration = FFTextFieldViewConfiguration(
    title: LocalizedString.SignUpView.confirmPasswordTitle,
    isSecure: true,
    errorMessage: LocalizedString.SignUpView.passwordConfirmationTextFieldError
  )
  
  @Published var passwordsMatch: Bool = false
  
  var isValidData: Bool {
    return [
      passwordConfiguration,
      passwordConfirmationConfiguration,
      emailConfiguration
    ].allSatisfy { $0.isValid } && passwordsMatch
  }
  
  init() {
    observePasswordMatching()
  }
  
  private func observePasswordMatching() {
    $passwordConfiguration
      .combineLatest($passwordConfirmationConfiguration)
      .map { $0.value == $1.value }
      .assign(to: &$passwordsMatch)
    
    passwordConfirmationConfiguration.validations = [
      .custom(isValid: { [weak self] _ in
        self?.passwordsMatch ?? false
      })
    ]
  }
}

private extension LocalizedString {
  enum SignUpView {
    static let nameTitle = "signup_name_title".localized
    static let emailTitle = "signup_email_title".localized
    static let passwordTitle = "signup_password_title".localized
    static let nameTextFieldError = "signup_name_textfield_error".localized
    static let emailTextFieldError = "signup_email_textfield_error".localized
    static let confirmPasswordTitle = "signup_confirm_password_title".localized
    static let passwordTextFieldError = "signup_password_textfield_error".localized
    static let passwordConfirmationTextFieldError = "signup_password_confirmation_textfield_error".localized
  }
}
