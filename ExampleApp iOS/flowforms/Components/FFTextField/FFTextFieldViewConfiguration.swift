//
//  FFTextFieldViewConfiguration.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

final class FFTextFieldViewConfiguration: ObservableObject {
  var value: String = "" {
    didSet {
      validate()
    }
  }
  
  var validations: [ValidationType]
  var errorMessage: String
  var title: String
  var isSecure = false

  private(set) var isValid = true
  
  var isEmpty: Bool {
    return value.isEmpty
  }
  
  var shouldShowError: Bool {
    !isEmpty && !isValid
  }
  
  init(title: String,
       value: String = "",
       validations: [ValidationType] = [.none],
       isSecure: Bool = false,
       errorMessage: String = "") {
    self.value = value
    self.title = title
    self.validations = validations
    self.errorMessage = errorMessage
    self.isSecure = isSecure
    
    validate()
  }
  
  func validate() {
    isValid = value.validates(validations)
  }
}

