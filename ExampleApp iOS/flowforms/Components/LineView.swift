//
//  LineView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 20/07/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct LineView: View {
  
  let thickness: CGFloat
  let color: Color
  
  var body: some View {
    Rectangle()
      .frame(
        maxWidth: .infinity,
        maxHeight: thickness
      )
      .foregroundColor(color)
  }
}
