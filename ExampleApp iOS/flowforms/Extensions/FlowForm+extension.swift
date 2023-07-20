//
//  FlowForm+extension.swift
//  FlowForms
//
//  Created by Tarek Radovan on 11/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesAsync

// MARK: FFCFlowForm
/// This extension provides additional functionality for the FlowForm class.
extension FlowForm {
  
  /// Binds the status of the form with a publisher.
  /// - Parameters:
  ///   - publisher: The publisher to bind the status with.
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
  
  /// Binds a field with the validation of the respective ID for that field.
  /// - Parameters:
  ///   - field: The field to bind.
  ///   - id: The ID of the field.
  ///   - completion: The completion handler to be called when the field value changes.
  /// - Returns: A binding object for the field.
  func bind(
    fieldNamed field: String,
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
            try await asyncFunction(for: self.validateOnValueChange(fieldId: id)
            )
          }
        }
      )
  }
  
  /// Binds a switch field with a completion handler and returns a binding object.
  /// - Parameters:
  ///   - field: The switch field to bind.
  ///   - id: The ID of the switch field.
  ///   - completion: The completion handler to be called when the switch value changes.
  /// - Returns: A binding object for the switch field.
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
            try await asyncFunction(for: self.validateOnValueChange(fieldId: id)
            )
          }
        }
      )
  }
}
