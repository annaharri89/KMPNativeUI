import SwiftUI
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared

struct ListView: View {
    @StateViewModel
    var viewModel = ListViewModel(
        portfolioRepository: KoinDependencies().portfolioRepository
    )

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.projects.isEmpty {
                    EmptyCollectionView(
                        title: viewModel.query.isEmpty && viewModel.languageFilter == nil
                            ? "No projects available"
                            : "No matching projects"
                    )
                } else {
                    List {
                        Section {
                            ForEach(viewModel.projects, id: \.id) { item in
                                NavigationLink {
                                    DetailView(projectId: item.id)
                                } label: {
                                    ProjectRow(project: item)
                                }
                            }
                        } header: {
                            Text(sectionHeader)
                        }
                    }
                    .listStyle(.insetGrouped)
                }
            }
            .navigationTitle("Projects")
            .navigationBarTitleDisplayMode(.large)
            .searchable(
                text: Binding(
                    get: { viewModel.query },
                    set: { viewModel.setSearchQuery(value: $0) }
                ),
                prompt: "Search projects"
            )
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Button("All languages") {
                            viewModel.setLanguageFilter(language: nil)
                        }
                        Divider()
                        ForEach(viewModel.languages, id: \.self) { language in
                            Button {
                                let currentlySelected = viewModel.languageFilter == language
                                viewModel.setLanguageFilter(
                                    language: currentlySelected ? nil : language
                                )
                            } label: {
                                HStack {
                                    Text(language)
                                    if viewModel.languageFilter == language {
                                        Image(systemName: "checkmark")
                                    }
                                }
                            }
                        }
                    } label: {
                        Image(systemName: viewModel.languageFilter == nil
                              ? "line.3.horizontal.decrease.circle"
                              : "line.3.horizontal.decrease.circle.fill")
                    }
                }
            }
        }
    }

    private var sectionHeader: String {
        if let language = viewModel.languageFilter {
            return language
        }
        return "Repositories"
    }
}

struct EmptyCollectionView: View {
    let title: String

    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "shippingbox")
                .font(.system(size: 40))
                .foregroundStyle(.secondary)
            Text(title)
                .font(.title3)
                .fontWeight(.semibold)
            Text("Try a different search or language filter.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding()
    }
}

struct ProjectRow: View {
    let project: PortfolioProject

    var body: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: project.avatarUrl)) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                case .empty:
                    ProgressView()
                default:
                    Color.gray.opacity(0.2)
                }
            }
            .frame(width: 56, height: 56)
            .clipShape(Circle())

            VStack(alignment: .leading, spacing: 4) {
                Text(project.name)
                    .font(.body)
                    .foregroundStyle(.primary)
                    .lineLimit(1)

                Text(subtitle)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .lineLimit(2)
            }
        }
        .padding(.vertical, 2)
    }

    private var subtitle: String {
        "\(project.languageOrUnknown) · \(project.starsAndForks) · \(project.updatedDateLabel)"
    }
}
