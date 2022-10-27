//
//  Validatable.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

enum ValidationType {
  case email
  case nonEmpty
  case numeric
  case date(dateFormat: String)
  case phone
  case none
  case custom(isValid: (String) -> Bool)
}

internal protocol Validatable {
  func validates(_ validations: [ValidationType]) -> Bool
}

extension String: Validatable {
  func validates(_ validations: [ValidationType]) -> Bool {
    validations.allSatisfy { validation in
      switch validation {
      case .email:
        return isEmailFormatted()
      case .numeric:
        return isInteger()
      case .date(dateFormat: let dateFormat):
        return isDateFormatted(dateFormat)
      case .phone:
        return isPhoneNumber()
      case .none:
        return true
      case .custom(isValid: let validationBlock):
        return validationBlock(self)
      default:
        return !isEmpty
      }
    }
  }
  
  func isEmailFormatted() -> Bool {
    let predicate = NSPredicate(
      format: "SELF MATCHES %@",
      "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@([A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?"
    )
    return predicate.evaluate(with: self)
  }
  
  func isInteger() -> Bool {
    Int(self) != nil
  }
  
  func isDateFormatted(_ dateFormat: String) -> Bool {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = dateFormat
    return dateFormatter.date(from: self) != nil
  }
  
  func isPhoneNumber() -> Bool {
    let phoneTest = NSPredicate(format: "SELF MATCHES %@", "^\\d{3}-\\d{3}-\\d{4}$")
    return phoneTest.evaluate(with: self)
  }
}
