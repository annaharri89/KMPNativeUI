import SwiftUI
import Shared
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI

struct FavoritesView: View {
    @StateViewModel
    var viewModel = FavoritesViewModel(
        portfolioRepository: KoinDependencies().portfolioRepository,
        favoritesStore: KoinDependencies().favoritesStore
    )

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.favorites.isEmpty {
                    EmptyFavoritesView()
                } else {
                    List {
                        Section("Saved projects") {
                            ForEach(viewModel.favorites, id: \.id) { item in
                                NavigationLink {
                                    DetailView(projectId: item.id)
                                } label: {
                                    ProjectRow(project: item)
                                }
                            }
                        }
                    }
                    .listStyle(.insetGrouped)
                }
            }
            .navigationTitle("Favorites")
            .navigationBarTitleDisplayMode(.large)
        }
    }
}

struct EmptyFavoritesView: View {
    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "heart")
                .font(.system(size: 40))
                .foregroundStyle(.secondary)
            Text("No favorites yet")
                .font(.title3)
                .fontWeight(.semibold)
            Text("Tap the heart on a project to save it here.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding()
    }
}
