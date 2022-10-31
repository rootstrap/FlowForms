import SwiftUI
import shared

@main
struct FlowFormsApp: App {
	var body: some Scene {
		WindowGroup {
      FFormView(viewModel: FFormViewModel())
		}
	}
}
