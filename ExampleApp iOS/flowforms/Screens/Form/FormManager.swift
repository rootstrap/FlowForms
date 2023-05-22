//
//  FormViewModel.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import shared
import SwiftUI

final class FormManager: ObservableObject {
  let formModel = FormModel()
  
  // MARK: Validations
  @Published var nameStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var emailStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var passwordStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var confirmPasswordStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var formStatus: String = StatusCodes.shared.UNMODIFIED
 
 var cancelBag: Set<AnyCancellable> = []

  init() {
    configureBindings()
    
    $confirmPasswordStatus
      .sink { value in
        print(value)
      }.store(in: &cancelBag)
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
