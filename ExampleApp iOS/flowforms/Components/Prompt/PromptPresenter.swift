//
//  PromptPresenter.swift
//  FlowForms
//
//  Created by Tarek Radovan on 27/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

enum PromptStyle {
  case error
  case warning
  case success
  
  var image: Image {
    switch self {
    case .error:
      return Image(systemName: "xmark.icloud.fill")
    case .warning:
      return Image(systemName: "exclamationmark.icloud.fill")
    case .success:
      return Image(systemName: "checkmark.icloud.fill")
    }
  }
  
  var color: Color {
    switch self {
    case .error:
        return .red
    case .warning:
        return .FFwarning
    case .success:
        return .green
    }
  }
}

struct PromptPresenter<TargetView>: View where TargetView: View {
  
  /// The binding that decides the appropriate drawing in the body.
  @Binding var isShowing: Bool
  
  let style: PromptStyle
  let message: String
  /// The view that will be "presenting" this prompt
  let targetView: () -> TargetView
  
  var body: some View {
    ZStack {
      targetView()
      VStack {
        Spacer()
        PromptView(style: style, message: message)
          .offset(x: .zero, y: isShowing ? .zero : 200)
      }
    }
  }
}

extension View {
  
  func showPrompt(
    _ show: Binding<Bool>,
    style: PromptStyle,
    message: String
  ) -> some View {
    PromptPresenter(
      isShowing: show,
      style: style,
      message: message,
      targetView: { self }
    )
  }
}
