//
//  FormErrors.swift
//  FlowForms
//
//  Created by Tarek Radovan on 15/03/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

extension FormManager {
  
  var emailErrorMessage: String? {
    switch emailStatus {
    case FFCStatusCodes.shared.BASIC_EMAIL_FORMAT_UNSATISFIED:
      return "Bad Email format"
    case FFCStatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Email required"
    case EmailDoesNotExistsInRemoteStorage.companion.EMAIL_ALREADY_EXISTS:
      return "Email already exist"
    default:
      return nil
    }
  }
  
  var nameErrorMessage: String? {
    switch nameStatus {
    case FFCStatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Name required"
    default:
      return nil
    }
  }
  
  var passwordErrorMessage: String? {
    switch passwordStatus {
    case FFCStatusCodes.shared.MIN_LENGTH_UNSATISFIED:
      return "Min length unsatisfied"
    case FFCStatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Password required"
    default:
      return nil
    }
  }

  var confirmedPasswordErrorMessage: String? {
    switch passwordStatus {
    case FFCStatusCodes.shared.MATCH_UNSATISFIED:
      return "Passwords don't match"
    case FFCStatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Password confirmation required"
    default:
      return nil
    }
  }
  
  var emailVerificationInProgress: Bool {
    return emailStatus == FFCStatusCodes.shared.IN_PROGRESS
  }
  
  var formValid: Bool {
    return formStatus == FFCStatusCodes.shared.CORRECT
  }
  
}
