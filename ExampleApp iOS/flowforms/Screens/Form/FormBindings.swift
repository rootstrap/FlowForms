//
//  FormBindings.swift
//  FlowForms
//
//  Created by Tarek Radovan on 15/03/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesCore

extension FormManager {
  //MARK: Bindings
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
extension FFCFlowForm {
  
  func bindStatus(withPublisher publisher: inout Published<String>.Publisher) {
    let formPublisher = createPublisher(for: status)
    
    formPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: FFCStatusCodes.shared.INCORRECT)
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
          completion($0)
          self.validateOnValueChange(
            fieldId: id
          ) { _ , _ in }
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
          self.validateOnValueChange(
            fieldId: id
          ) { _ , _ in }
        }
      )
  }
}

//MARK: FFCFlowField
extension FFCFlowField {
  func bindStatus(withPublisher
    publisher: inout Published<String>.Publisher
  ){
    let fieldPublisher = createPublisher(for: self.statusNative)
    
    fieldPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: FFCStatusCodes.shared.INCORRECT)
      .assign(to: &publisher)
  }

}
