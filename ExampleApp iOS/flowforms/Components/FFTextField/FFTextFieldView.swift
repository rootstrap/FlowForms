//
//  FFTextFieldView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//

import SwiftUI
import shared

struct FormModelTextView: View {

  @Binding var valueText: String
  @Binding var title: String

  init(title: String, value: Binding<String>) {
    _title = Binding.constant(title)
    _valueText = value
  }
  
  var body: some View {
    VStack {
      ZStack {
        Text(title)
          .frame(maxWidth: .infinity, alignment: .leading)
          .font(Font.headline.weight(.regular))
          .foregroundColor(.black)
          .offset(valueText.isEmpty ? .zero : CGSize(width: .zero, height: UI.OssTextField.titleOffSet))
          .animation(.easeOut(duration: UI.OssTextField.textAnimationDuration),
                     value: valueText)
          .scaleEffect(
            valueText.isEmpty ? UI.OssTextField.titleScaleEmpty : UI.OssTextField.titleScaleNonEmpty,
            anchor: .bottomLeading
          )
        TextField("", text: $valueText)
          .autocapitalization(.none)
      }
      Rectangle()
        .frame(
          maxWidth: .infinity,
          maxHeight: UI.OssTextField.textfieldLineHeight,
          alignment: .bottomLeading
        )
        .foregroundColor(.gray)
    }.frame(maxWidth: .infinity, maxHeight: 60)
  }
}
