//
//  FormModelTextView.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//

import SwiftUI
import shared

struct FormModelTextView: View {

  @Binding var valueText: String
  @Binding var title: String
  private var errorMessage: String?
  private let isSecureField: Bool
  
  init(title: String, isSecureField: Bool = false, value: Binding<String>, errorMessage: String? = nil) {
    _title = Binding.constant(title)
    self.isSecureField = isSecureField
    _valueText = value
    self.errorMessage = errorMessage
  }
  
  var body: some View {
    VStack {
      ZStack {
        Text(title)
          .frame(maxWidth: .infinity, alignment: .leading)
          .font(Font.headline.weight(.regular))
          .foregroundColor(.black)
          .offset(valueText.isEmpty ? .zero : CGSize(width: .zero, height: UI.FormModelTextView.titleOffSet))
          .animation(.easeOut(duration: Animation.Duration.veryShort),
                     value: valueText)
          .scaleEffect(
            valueText.isEmpty ? UI.FormModelTextView.titleScaleEmpty : UI.FormModelTextView.titleScaleNonEmpty,
            anchor: .bottomLeading
          )
        if isSecureField {
          SecureField("", text: $valueText)
            .autocapitalization(.none)
        } else {
          TextField("", text: $valueText)
            .autocapitalization(.none)
        }
      }
      LineView(
        thickness: UI.FormModelTextView.textfieldLineHeight,
        color: .gray
      )
      if let errorMessage = errorMessage {
        ErrorMessageView(message: errorMessage)
      }
    }.frame(maxWidth: .infinity, maxHeight: UI.FormModelTextView.height)
  }
}

private extension UI {
  enum FormModelTextView {
    static let height: CGFloat = 60
    static let titleScaleNonEmpty: Double = 0.8
    static let titleScaleEmpty: Double = 1
    static let textfieldLineHeight: CGFloat = 1
    static let titleOffSet: CGFloat = -30
    static let errorTextOffset: CGFloat = -5
  }
}

