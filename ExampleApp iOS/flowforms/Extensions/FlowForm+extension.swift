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

// MARK: FFCFlowForm
/// This extension provides additional functionality for the FlowForm class.
extension FlowForm {
  
  /// Binds a field with the validation of the respective ID for that field.
  /// - Parameters:
  ///   - fieldValue: The current field's value.
  ///   - id: The ID of the field.
  ///   - completion: The completion handler to be called when the field value changes.
  /// - Returns: A binding object for the field.
  func bind(
    fieldValue field: String,
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
            self.validateOnValueChange(fieldId: id, onCompletion: { _ in })
          }
        }
      )
  }
  
  /// Binds a switch field with a completion handler and returns a binding object.
  /// - Parameters:
  ///   - fieldValue: The current field's value..
  ///   - id: The ID of the switch field.
  ///   - completion: The completion handler to be called when the switch value changes.
  /// - Returns: A binding object for the switch field.
  func bindSwitch(
    fieldValue: Bool,
    id: String,
    completion: @escaping (Bool) -> Void
  ) -> Binding<Bool> {
    return
      Binding(
        get: { fieldValue },
        set: {
          completion($0)
          Task {
            self.validateOnValueChange(fieldId: id, onCompletion: { _ in })
          }
        }
      )
  }
}
