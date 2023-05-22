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
    case StatusCodes.shared.BASIC_EMAIL_FORMAT_UNSATISFIED:
      return "Bad Email format"
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Email required"
    case EmailDoesNotExistsInRemoteStorage.companion.EMAIL_ALREADY_EXISTS:
      return "Email already exist"
    default:
      return nil
    }
  }
  
  var nameErrorMessage: String? {
    switch nameStatus {
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Name required"
    default:
      return nil
    }
  }
  
  var passwordErrorMessage: String? {
    switch passwordStatus {
    case StatusCodes.shared.MIN_LENGTH_UNSATISFIED:
      return "Min length unsatisfied"
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Password required"
    default:
      return nil
    }
  }

  var confirmedPasswordErrorMessage: String? {
    switch confirmPasswordStatus {
    case StatusCodes.shared.MATCH_UNSATISFIED:
      return "Passwords don't match"
    case StatusCodes.shared.REQUIRED_UNSATISFIED:
      return "Password confirmation required"
    case StatusCodes.shared.MIN_LENGTH_UNSATISFIED:
      return "Min length unsatisfied"
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
