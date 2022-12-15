//
//  PromptView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 27/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct PromptView: View {
  
    let style: PromptStyle
    let message: String
  
    var body: some View {
      HStack {
        Text(message)
          .minimumScaleFactor(0.5)
          .lineLimit(2)
        style.image
          .foregroundColor(style.color)
      }
      .padding()
      .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 10, style: .continuous))
      .frame(maxWidth: .infinity, alignment: .center)
      .padding()
    }
}
