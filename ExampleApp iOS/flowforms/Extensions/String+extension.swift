//
//  String+extension.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

extension String {
  var localized: String {
    NSLocalizedString(self, comment: "")
  }
}
