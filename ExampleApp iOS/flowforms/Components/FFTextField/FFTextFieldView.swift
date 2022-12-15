//
//  FFTextFieldView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//

import SwiftUI
import shared

struct FFTextFieldView: View {

  @Binding var configuration: FFTextFieldViewConfiguration
  
  var body: some View {
    VStack {
      ZStack {
        Text(configuration.title)
          .frame(maxWidth: .infinity, alignment: .leading)
          .font(Font.headline.weight(.regular))
          .foregroundColor(configuration.isEmpty ? .gray : .black)
          .offset(configuration.isEmpty ? .zero : CGSize(width: .zero, height: UI.OssTextField.titleOffSet))
          .animation(.easeOut(duration: UI.OssTextField.textAnimationDuration),
                     value: configuration.value)
          .scaleEffect(
            configuration.isEmpty ? UI.OssTextField.titleScaleEmpty : UI.OssTextField.titleScaleNonEmpty,
            anchor: .bottomLeading
          )
        if configuration.isSecure {
          SecureField("", text: $configuration.value)
        } else {
          TextField("", text: $configuration.value)
            .autocapitalization(.none)
        }
      }
      Rectangle()
        .frame(
          maxWidth: .infinity,
          maxHeight: UI.OssTextField.textfieldLineHeight,
          alignment: .bottomLeading
        )
        .foregroundColor(configuration.shouldShowError ? .red : .gray)
      Text(configuration.errorMessage)
        .foregroundColor(.red)
        .font(.footnote)
        .animation(.easeInOut)
        .frame(maxWidth: .infinity, alignment: .leading)
        .offset(CGSize(
          width: .zero,
          height: UI.OssTextField.errorTextOffset
        ))
        .opacity(configuration.shouldShowError ? 1 : .zero)
    }.frame(maxWidth: .infinity, maxHeight: 60)
  }
}
