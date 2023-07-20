//
//  FieldDefinition+extension.swift
//  FlowForms
//
//  Created by Tarek Radovan on 11/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import KMPNativeCoroutinesCombine
import KMPNativeCoroutinesCore

/// Extension for FlowField that binds the status property with a publisher.
extension FieldDefinition {
    /// Binds the status property with a publisher.
    ///
    /// - Parameters:
    ///   - publisher: The publisher to bind the status property with.
  func bindStatus(withPublisher publisher: inout Published<String>.Publisher) {
    
    let fieldPublisher = createPublisher(for: FieldDefinitionNativeKt.status(self))
    
    fieldPublisher
      .receive(on: DispatchQueue.main)
      .map({ status in
        return status.code
      })
      .replaceError(with: StatusCodes.shared.INCORRECT)
      .assign(to: &publisher)
  }
}
