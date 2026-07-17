import Foundation
import SwiftUI
import Shared
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI

struct DetailView: View {
    @StateViewModel
    var viewModel = DetailViewModel(
        portfolioRepository: KoinDependencies().portfolioRepository,
        favoritesStore: KoinDependencies().favoritesStore
    )

    let projectId: Int64

    var body: some View {
        Group {
            if let project = viewModel.project {
                ProjectDetails(
                    project: project,
                    isFavorite: viewModel.isFavorite,
                    onToggleFavorite: { viewModel.toggleFavorite() }
                )
            } else {
                ProgressView("Loading…")
            }
        }
        .navigationTitle("Project")
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
            viewModel.setId(projectId: projectId)
        }
    }
}

struct ProjectDetails: View {
    var project: PortfolioProject
    var isFavorite: Bool
    var onToggleFavorite: () -> Void

    var body: some View {
        Form {
            Section {
                HStack {
                    Spacer()
                    AsyncImage(url: URL(string: project.avatarUrl)) { phase in
                        switch phase {
                        case .empty:
                            ProgressView()
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()
                        default:
                            Color.gray.opacity(0.15)
                        }
                    }
                    .frame(width: 96, height: 96)
                    .clipShape(Circle())
                    Spacer()
                }
                .listRowBackground(Color.clear)
            }

            Section("Project") {
                LabeledContent("Name", value: project.name)
                if let summary = project.summary, !summary.isEmpty {
                    LabeledContent("Description", value: summary)
                }
                LabeledContent("Language", value: project.languageOrUnknown)
                LabeledContent("Updated", value: project.updatedDateLabel)
            }

            Section("Stats") {
                LabeledContent("Stars", value: String(project.stargazersCount))
                LabeledContent("Forks", value: String(project.forksCount))
                LabeledContent("Owner", value: project.ownerLogin)
                if !project.licenseName.isEmpty {
                    LabeledContent("License", value: project.licenseName)
                }
            }

            Section {
                if let url = URL(string: project.htmlUrl) {
                    Link("Open on GitHub", destination: url)
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
