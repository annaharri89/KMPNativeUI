import SwiftUI

struct PlatformTourView: View {
    @State private var selectedStyle = TourStyle.grouped
    @State private var notificationsEnabled = true
    @State private var showConfirmation = false
    @State private var showAlert = false
    @State private var lastAction = "Try a control below"

    private enum TourStyle: String, CaseIterable, Identifiable {
        case grouped = "Grouped"
        case plain = "Plain"
        var id: String { rawValue }
    }

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    Text("These controls follow Apple HIG. Android uses Material 3 patterns for the same product features.")
                        .font(.body)
                        .foregroundStyle(.secondary)
                }

                Section("Segmented picker") {
                    Picker("List style", selection: $selectedStyle) {
                        ForEach(TourStyle.allCases) { style in
                            Text(style.rawValue).tag(style)
                        }
                    }
                    .pickerStyle(.segmented)

                    Text("Android filters with chips. iOS often uses segmented controls or menus.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }

                Section("Toggle") {
                    Toggle("Local demo preference", isOn: $notificationsEnabled)
                    Text("Favorites persistence is shared Kotlin. This toggle is platform-local presentation state.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }

                Section("Alerts & confirmation") {
                    Button("Show confirmation dialog") {
                        showConfirmation = true
                    }
                    Button("Show alert") {
                        showAlert = true
                    }
                    Text(lastAction)
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }

                Section("TabView") {
                    Label("This screen lives in a system TabView", systemImage: "square.grid.2x2")
                    Text("Android uses a Material NavigationBar for the same three destinations.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
            }
            .navigationTitle("UI Tour")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        lastAction = "Toolbar action tapped"
                    } label: {
                        Image(systemName: "ellipsis.circle")
                    }
                }
            }
            .confirmationDialog(
                "Remove sample item?",
                isPresented: $showConfirmation,
                titleVisibility: .visible
            ) {
                Button("Remove", role: .destructive) {
                    lastAction = "Confirmation dialog: Remove"
                }
                Button("Cancel", role: .cancel) {
                    lastAction = "Confirmation dialog: Cancel"
                }
            }
            .alert("Material vs HIG", isPresented: $showAlert) {
                Button("OK", role: .cancel) {
                    lastAction = "Alert dismissed"
                }
            } message: {
                Text("Same shared ViewModels. Different native chrome.")
            }
        }
    }
}
