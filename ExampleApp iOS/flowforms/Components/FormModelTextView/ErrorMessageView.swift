//
//  ErrorMessageView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 20/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct ErrorMessageView: View {
  private let errorMessageHeight: CGFloat = 10
  
  let message: String
  var body: some View {
    Text(message)
      .font(.caption)
      .frame(
        maxWidth: .infinity,
        maxHeight: errorMessageHeight,
        alignment: .leading
      )
      .foregroundColor(.red)
  }
}
