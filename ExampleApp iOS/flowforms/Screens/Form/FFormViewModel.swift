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

final class FFormViewModel: ObservableObject {

  @Published var termsAccepted = false

  var cancelBag = Set<AnyCancellable>()
  init() { }
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

class FormManager: ObservableObject {
  let formModel = FormModel()
  let claseNueva = ClaseNueva()
  
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
  @Published var nameValid: Bool = false
  @Published var emailValid: Bool = false
  @Published var passwordValid: Bool = false
  @Published var confirmPasswordValid: Bool = false
 
  
 var isValid: Bool {
   nameValid
   && emailValid
 }
  
 var cancelBag: Set<AnyCancellable> = []

 init() {
  
  
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.NAME),
     inPublisher: &$nameValid,
     errorMessage: "invalid name"
   )
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.EMAIL),
     inPublisher: &$emailValid
   )
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.PASSWORD),
     inPublisher: &$passwordValid
   )
   bind(
     field: formModel.form.fieldFor(id: FormModel.companion.CONFIRM_PASSWORD),
     inPublisher: &$confirmPasswordValid
   )
 }
  
  func bind(
    field: FFCFlowField,
    inPublisher publisher: inout Published<Bool>.Publisher,
    errorMessage: String = ""
  ){
    let fieldPublisher = createPublisher(for: field.statusNative)
    
    fieldPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return true
        //return status.code == FFCStatusCodes.shared.CORRECT
      })
      .replaceError(with: false)
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
}
