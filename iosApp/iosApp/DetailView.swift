import Foundation
import SwiftUI
import Shared
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI

struct DetailView: View {
    @StateViewModel
    var viewModel = DetailViewModel(
        museumRepository: KoinDependencies().museumRepository,
        favoritesStore: KoinDependencies().favoritesStore
    )

    let objectId: Int32

    var body: some View {
        Group {
            if let obj = viewModel.museumObject {
                ObjectDetails(
                    obj: obj,
                    isFavorite: viewModel.isFavorite,
                    onToggleFavorite: { viewModel.toggleFavorite() }
                )
            } else {
                ProgressView("Loading…")
            }
        }
        .navigationTitle("Artwork")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    viewModel.toggleFavorite()
                } label: {
                    Image(systemName: viewModel.isFavorite ? "heart.fill" : "heart")
                        .foregroundStyle(viewModel.isFavorite ? .red : .primary)
                }
                .accessibilityLabel(viewModel.isFavorite ? "Remove favorite" : "Save favorite")
            }
        }
        .onAppear {
            viewModel.setId(objectId: objectId)
        }
    }
}

struct ObjectDetails: View {
    var obj: MuseumObject
    var isFavorite: Bool
    var onToggleFavorite: () -> Void

    var body: some View {
        Form {
            Section {
                AsyncImage(url: URL(string: obj.primaryImageSmall)) { phase in
                    switch phase {
                    case .empty:
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .frame(height: 220)
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(maxWidth: .infinity)
                            .frame(height: 220)
                            .clipped()
                            .listRowInsets(EdgeInsets())
                    default:
                        Color.gray.opacity(0.15)
                            .frame(height: 220)
                            .listRowInsets(EdgeInsets())
                    }
                }
            }

            Section("Artwork") {
                LabeledContent("Title", value: obj.title.isEmpty ? "Untitled" : obj.title)
                LabeledContent(
                    "Artist",
                    value: obj.artistDisplayName.isEmpty ? "Unknown artist" : obj.artistDisplayName
                )
                if !obj.objectDate.isEmpty {
                    LabeledContent("Date", value: obj.objectDate)
                }
                if !obj.medium.isEmpty {
                    LabeledContent("Medium", value: obj.medium)
                }
                if !obj.dimensions.isEmpty {
                    LabeledContent("Dimensions", value: obj.dimensions)
                }
            }

            Section("Collection") {
                if !obj.department.isEmpty {
                    LabeledContent("Department", value: obj.department)
                }
                if !obj.repository.isEmpty {
                    LabeledContent("Repository", value: obj.repository)
                }
                if !obj.creditLine.isEmpty {
                    LabeledContent("Credits", value: obj.creditLine)
                }
            }

            Section {
                Toggle(isOn: Binding(
                    get: { isFavorite },
                    set: { _ in onToggleFavorite() }
                )) {
                    Label("Favorite", systemImage: isFavorite ? "heart.fill" : "heart")
                }
                .tint(.red)
            }
        }
    }
}
