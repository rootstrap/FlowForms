//
//  FormState.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import shared
import SwiftUI

final class FormState: ObservableObject {
  let viewModel = SignupViewModel()
  
  @Published var nameStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var emailStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var passwordStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var confirmPasswordStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var formStatus: String = StatusCodes.shared.UNMODIFIED
 
  @Published var uiState: SignUpFormUiState?
  
  // MARK: Errors
  var emailErrorMessage: String? {
    switch emailStatus {
    case StatusCodes.shared.BASIC_EMAIL_FORMAT_UNSATISFIED:
      return LocalizedString.FormState.emailBadFormatError
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormState.emailRequiredError
    case EmailDoesNotExistsInRemoteStorage.companion.EMAIL_ALREADY_EXISTS:
      return LocalizedString.FormState.emailAlreadyExistError
    default:
      return nil
    }
  }
  
  var nameErrorMessage: String? {
    switch nameStatus {
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormState.nameRequiredError
    default:
      return nil
    }
  }
  
  var passwordErrorMessage: String? {
    switch passwordStatus {
    case StatusCodes.shared.MIN_LENGTH_UNSATISFIED:
      return LocalizedString.FormState.passwordMinLengthError
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormState.passwordRequiredError
    default:
      return nil
    }
  }

  var confirmedPasswordErrorMessage: String? {
    switch confirmPasswordStatus {
    case StatusCodes.shared.MATCH_UNSATISFIED:
      return LocalizedString.FormState.passwordsDontMatchError
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormState.passwordConfirmationRequiredError
    case StatusCodes.shared.MIN_LENGTH_UNSATISFIED:
      return LocalizedString.FormState.passwordMinLengthError
    default:
      return nil
    }
  }
  
  var isEmailVerificationInProgress: Bool {
    return emailStatus == StatusCodes.shared.IN_PROGRESS
  }
  
  var isFormValid: Bool {
    return formStatus == StatusCodes.shared.CORRECT
  }
  
  init() {
    
    viewModel.observeUiState { state in
      self.uiState = state
    }
    
    configureBindings()
  }
  
  func configureBindings() {
    viewModel.form.onStatusChange { [weak self] status in
        self?.formStatus = status.code
    }
    
    viewModel.form
      .field(id: SignupViewModel.companion.NAME)?
      .onStatusChange(onStatusChange: { [weak self] status in
        self?.nameStatus = status.code
      })
    
    viewModel.form
      .field(id: SignupViewModel.companion.EMAIL)?
      .onStatusChange(onStatusChange: { [weak self] status in
        self?.emailStatus = status.code
      })
    viewModel.form
      .field(id: SignupViewModel.companion.PASSWORD)?
      .onStatusChange(onStatusChange: { [weak self] status in
        self?.passwordStatus = status.code
      })
    viewModel.form
      .field(id: SignupViewModel.companion.CONFIRM_PASSWORD)?
      .onStatusChange(onStatusChange: { [weak self] status in
        self?.confirmPasswordStatus = status.code
      })
  }
}

private extension LocalizedString {
  enum FormState {
    static let emailBadFormatError = "bad_email_format".localized
    static let emailRequiredError = "email_required".localized
    static let emailAlreadyExistError = "email_already_exist".localized
    static let nameRequiredError = "name_required".localized
    static let passwordMinLengthError = "password_min_length".localized
    static let passwordRequiredError = "password_required".localized
    static let passwordsDontMatchError = "passwords_dont_match".localized
    static let passwordConfirmationRequiredError = "password_confirmation_required".localized
  }
}
