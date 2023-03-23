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
    formModel.form.bindField(
      field: formModel.name,
      id: FormModel.companion.NAME
    ) {
      self.formModel.name = $0
      self.objectWillChange.send()
    }
  }
  
  var email: Binding<String> {
    formModel.form.bindField(
      field: formModel.email,
      id: FormModel.companion.EMAIL
    ) {
      self.formModel.email = $0
      self.objectWillChange.send()
    }
  }
  
  var password: Binding<String> {
    formModel.form.bindField(
      field: formModel.password,
      id: FormModel.companion.PASSWORD
    ) {
      self.formModel.password = $0
      self.objectWillChange.send()
    }
  }
  
  var confirmPassword: Binding<String> {
    formModel.form.bindField(
      field: formModel.confirmPassword,
      id: FormModel.companion.CONFIRM_PASSWORD
    ) {
      self.formModel.confirmPassword = $0
      self.objectWillChange.send()
    }
  }
  
  func configureBindings() {
    
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
  
  private func bindForm(inPublisher publisher: inout Published<String>.Publisher) {
    let formPublisher = createPublisher(for: formModel.form.statusNative)
    
    formPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: FFCStatusCodes.shared.INCORRECT)
      .assign(to: &publisher)
  }
  
  private func bind(
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
}

extension FFCFlowForm {
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
