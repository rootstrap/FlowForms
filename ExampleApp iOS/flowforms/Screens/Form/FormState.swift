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
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesAsync

final class FormState: ObservableObject {
  let formModel = FormModel()
  
  @Published var nameStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var emailStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var passwordStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var confirmPasswordStatus: String = StatusCodes.shared.UNMODIFIED
  @Published var formStatus: String = StatusCodes.shared.UNMODIFIED
 
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
  
  // MARK: Bindings
  var termsAccepted: Binding<Bool> {
    formModel.form.bindSwitch(
      field: formModel.termsAccepted,
      id: FormModel.companion.TERMS_ACCEPTED
    ) {
      self.formModel.termsAccepted = $0
      self.objectWillChange.send()
    }
  }
  
  var name: Binding<String> {
    formModel.form.bind(
      fieldValue: formModel.name,
      id: FormModel.companion.NAME
    ) {
      self.formModel.name = $0
      self.objectWillChange.send()
    }
  }
  
  var email: Binding<String> {
    formModel.form.bind(
      fieldValue: formModel.email,
      id: FormModel.companion.EMAIL
    ) {
      self.formModel.email = $0
      self.objectWillChange.send()
    }
  }
  
  var password: Binding<String> {
    formModel.form.bind(
      fieldValue: formModel.password,
      id: FormModel.companion.PASSWORD
    ) {
      self.formModel.password = $0
      self.objectWillChange.send()
    }
  }
  
  var confirmPassword: Binding<String> {
    formModel.form.bind(
      fieldValue: formModel.confirmPassword,
      id: FormModel.companion.CONFIRM_PASSWORD
    ) {
      self.formModel.confirmPassword = $0
      self.objectWillChange.send()
    }
  }

  init() {
    configureBindings()
  }
  
  func configureBindings() {
    formModel.form.bindStatus(withPublisher: &$formStatus)
    
    formModel.form
      .field(id: FormModel.companion.NAME)?
      .bindStatus(withPublisher: &$nameStatus)
    formModel.form
      .field(id: FormModel.companion.EMAIL)?
      .bindStatus(withPublisher: &$emailStatus)
    formModel.form
      .field(id: FormModel.companion.PASSWORD)?
      .bindStatus(withPublisher: &$passwordStatus)
    formModel.form
      .field(id: FormModel.companion.CONFIRM_PASSWORD)?
      .bindStatus(withPublisher: &$confirmPasswordStatus)
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
