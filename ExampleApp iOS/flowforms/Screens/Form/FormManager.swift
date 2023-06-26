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
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesAsync

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

// MARK: Errors
extension FormManager {
  
  var emailErrorMessage: String? {
    switch emailStatus {
    case StatusCodes.shared.BASIC_EMAIL_FORMAT_UNSATISFIED:
      return LocalizedString.FormManager.emailBadFormatError
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormManager.emailRequiredError
    case EmailDoesNotExistsInRemoteStorage.companion.EMAIL_ALREADY_EXISTS:
      return LocalizedString.FormManager.emailAlreadyExistError
    default:
      return nil
    }
  }
  
  var nameErrorMessage: String? {
    switch nameStatus {
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormManager.nameRequiredError
    default:
      return nil
    }
  }
  
  var passwordErrorMessage: String? {
    switch passwordStatus {
    case StatusCodes.shared.MIN_LENGTH_UNSATISFIED:
      return LocalizedString.FormManager.passwordMinLengthError
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormManager.passwordRequiredError
    default:
      return nil
    }
  }

  var confirmedPasswordErrorMessage: String? {
    switch confirmPasswordStatus {
    case StatusCodes.shared.MATCH_UNSATISFIED:
      return LocalizedString.FormManager.passwordsDontMatchError
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return LocalizedString.FormManager.passwordConfirmationRequiredError
    case StatusCodes.shared.MIN_LENGTH_UNSATISFIED:
      return LocalizedString.FormManager.passwordMinLengthError
    default:
      return nil
    }
  }
  
  var emailVerificationInProgress: Bool {
    return emailStatus == StatusCodes.shared.IN_PROGRESS
  }
  
  var formValid: Bool {
    return formStatus == StatusCodes.shared.CORRECT
  }
}

// MARK: Bindings
extension FormManager {
  
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
    formModel.form.bindField(
      formModel.name,
      id: FormModel.companion.NAME
    ) {
      self.formModel.name = $0
      self.objectWillChange.send()
    }
  }
  
  var email: Binding<String> {
    formModel.form.bindField(
      formModel.email,
      id: FormModel.companion.EMAIL
    ) {
      self.formModel.email = $0
      self.objectWillChange.send()
    }
  }
  
  var password: Binding<String> {
    formModel.form.bindField(
      formModel.password,
      id: FormModel.companion.PASSWORD
    ) {
      self.formModel.password = $0
      self.objectWillChange.send()
    }
  }
  
  var confirmPassword: Binding<String> {
    formModel.form.bindField(
      formModel.confirmPassword,
      id: FormModel.companion.CONFIRM_PASSWORD
    ) {
      self.formModel.confirmPassword = $0
      self.objectWillChange.send()
    }
  }
  
  func configureBindings() {
    formModel.form.bindStatus(withPublisher: &$formStatus)
    
    formModel.form
      .fieldFor(id: FormModel.companion.NAME)
      .bindStatus(withPublisher: &$nameStatus)
    formModel.form
      .fieldFor(id: FormModel.companion.EMAIL)
      .bindStatus(withPublisher: &$emailStatus)
    formModel.form
      .fieldFor(id: FormModel.companion.PASSWORD)
      .bindStatus(withPublisher: &$passwordStatus)
    formModel.form
      .fieldFor(id: FormModel.companion.CONFIRM_PASSWORD)
      .bindStatus(withPublisher: &$confirmPasswordStatus)
  }
}

// MARK: FFCFlowForm
extension FlowForm {
  
  func bindStatus(withPublisher publisher: inout Published<String>.Publisher) {
    let formPublisher = createPublisher(for: status)
    
    formPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: StatusCodes.shared.INCORRECT)
      .assign(to: &publisher)
  }
  
  func bindField(
    _ field: String,
    id: String,
    completion: @escaping (String) -> Void
  ) -> Binding<String> {
    return
      Binding(
        get: { field },
        set: {
          guard $0 != field else {
            return
          }
          completion($0)
          Task {
            let variable = try await asyncFunction(for: self.validateOnValueChange(fieldId: id)
            )
            print(variable)
          }
        }
      )
  }
  
  func bindSwitch(
    field: Bool,
    id: String,
    completion: @escaping (Bool) -> Void
  ) -> Binding<Bool> {
    return
      Binding(
        get: { field },
        set: {
          completion($0)
          Task {
            let variable = try await asyncFunction(for: self.validateOnValueChange(fieldId: id)
            )
            print(variable)
          }
        }
      )
  }
}

//MARK: FFCFlowField
extension FlowField {
  func bindStatus(withPublisher
    publisher: inout Published<String>.Publisher
  ){
      let fieldPublisher = createPublisher(for: self.status)
    
    fieldPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: StatusCodes.shared.INCORRECT)
      .assign(to: &publisher)
  }
}


private extension LocalizedString {
  enum FormManager {
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
