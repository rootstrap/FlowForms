//
//  LocalizedString.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

enum LocalizedString {}

extension LocalizedString {
  static func parametrizedString(format: String, params: String...) -> String {
    String(format: format, arguments: params)
  }
}
