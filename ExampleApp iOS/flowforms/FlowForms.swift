import SwiftUI
import shared

@main
struct FlowForms: App {
	var body: some Scene {
		WindowGroup {
      FFormView(viewModel: FFormViewModel())
		}
	}
}
