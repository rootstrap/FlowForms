//
//  FFormViewModel.swift
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

class FormManager: ObservableObject {
  let formModel = FormModel()

  var termsAccepted: Binding<Bool> {
    bindSwitch(
      field: formModel.termsAccepted,
      id: FormModel.companion.TERMS_ACCEPTED
    ) {
        print("switch : \($0)")
        self.formModel.termsAccepted = $0
      }
  }
  
  var name: Binding<String> {
    bindField(
      field: formModel.name,
      id: FormModel.companion.NAME
    ) {
      self.formModel.name = $0
    }
  }
  
  var email: Binding<String> {
    bindField(
      field: formModel.email,
      id: FormModel.companion.EMAIL
    ) {
        self.formModel.email = $0
    }
  }
  
  var password: Binding<String> {
    bindField(
      field: formModel.password,
      id: FormModel.companion.PASSWORD
    ) {
        self.formModel.password = $0
      }
  }
  
  var confirmPassword: Binding<String> {
    bindField(
      field: formModel.confirmPassword,
      id: FormModel.companion.CONFIRM_PASSWORD
    ) {
        self.formModel.confirmPassword = $0
      }
  }

  private var nameValidPublisher = CurrentValueSubject<Bool, Never>(false)
  
  @Published private var nameStatus: String = FFCStatusCodes.shared.UNMODIFIED
  @Published private var emailStatus: String = FFCStatusCodes.shared.UNMODIFIED
  @Published private var passwordStatus: String = FFCStatusCodes.shared.UNMODIFIED
  @Published private var confirmPasswordStatus: String = FFCStatusCodes.shared.UNMODIFIED
  @Published private var formStatus: String = FFCStatusCodes.shared.UNMODIFIED
  
  var emailVerificationInProgress: Bool {
    return emailStatus == FFCStatusCodes.shared.IN_PROGRESS
  }
  
  var formValid: Bool {
    return formStatus == FFCStatusCodes.shared.CORRECT
  }
  
  var nameComprobations: AnyPublisher<Bool, Never> {
    $nameStatus
      .receive(on: DispatchQueue.main)
      .map({ $0 == FFCStatusCodes.shared.REQUIRED_UNSATISFIED})
      .eraseToAnyPublisher()
  }
  
  var emailComprobation: AnyPublisher<Bool, Never> {
    $emailStatus
      .receive(on: DispatchQueue.main)
      .map({ status in
        print(status)
          return (status == FFCStatusCodes.shared.BASIC_EMAIL_FORMAT_UNSATISFIED)
          || (status == FFCStatusCodes.shared.REQUIRED_UNSATISFIED)
      }).eraseToAnyPublisher()
  }
  
  var passwordComprobation: AnyPublisher<Bool, Never> {
    $passwordStatus
      .receive(on: DispatchQueue.main)
      .map({
        ($0 == FFCStatusCodes.shared.MIN_LENGTH_UNSATISFIED)
        || ($0 == FFCStatusCodes.shared.REQUIRED_UNSATISFIED)
      }).eraseToAnyPublisher()
  }
  
  var confirmedPasswordComprobation: AnyPublisher<Bool, Never> {
    $confirmPasswordStatus
      .receive(on: DispatchQueue.main)
      .map({
        ($0 == FFCStatusCodes.shared.MATCH_UNSATISFIED)
        || ($0 == FFCStatusCodes.shared.REQUIRED_UNSATISFIED)
      }).eraseToAnyPublisher()
  }
  
 var cancelBag: Set<AnyCancellable> = []

 init() {
   
   bindForm(inPublisher: &$formStatus)
   
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.NAME),
     inPublisher: &$nameStatus
   )
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.EMAIL),
     inPublisher: &$emailStatus
   )
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.PASSWORD),
     inPublisher: &$passwordStatus
   )
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.CONFIRM_PASSWORD),
     inPublisher: &$confirmPasswordStatus
   )
 }
  
  func bindForm(inPublisher publisher: inout Published<String>.Publisher) {
    let formPublisher = createPublisher(for: formModel.form.statusNative)
    
    formPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: FFCStatusCodes.shared.INCORRECT)
      .assign(to: &publisher)
  }
  
  func bind(
    field: FFCFlowField,
    inPublisher publisher: inout Published<String>.Publisher
  ){
    let fieldPublisher = createPublisher(for: field.statusNative)
    
    fieldPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: FFCStatusCodes.shared.INCORRECT)
      .assign(to: &publisher)
  }
  
  func bindField(
    field: String,
    id: String,
    completion: @escaping (String) -> Void
  ) -> Binding<String> {
    return
      Binding(
        get: { field },
        set: {
          completion($0)
          self.formModel.form.validateOnValueChange(
            fieldId: id
          ) { _ , _ in }
          self.objectWillChange.send()
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
          self.formModel.form.validateOnValueChange(
            fieldId: id
          ) { _ , _ in }
          self.objectWillChange.send()
        }
      )
  }
}
