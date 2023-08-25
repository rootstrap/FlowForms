//
//  FormState.swift
//  FlowForms
//
//  Created by Tarek Radovan on 26/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import shared
import SwiftUI

final class FormState: ObservableObject {
  let viewModel = SignupViewModel()
 
  @Published var uiState: SignUpFormUiState?
  
  init() {

    viewModel.observeUiState { state in
      self.uiState = state
    }
    
  }
  
}
