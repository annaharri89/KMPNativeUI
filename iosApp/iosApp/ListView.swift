import SwiftUI
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared

struct ListView: View {
    @StateViewModel
    var viewModel = ListViewModel(
        museumRepository: KoinDependencies().museumRepository
    )

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.objects.isEmpty {
                    EmptyCollectionView(
                        title: viewModel.query.isEmpty && viewModel.departmentFilter == nil
                            ? "No data available"
                            : "No matching artworks"
                    )
                } else {
                    List {
                        Section {
                            ForEach(viewModel.objects, id: \.objectID) { item in
                                NavigationLink {
                                    DetailView(objectId: item.objectID)
                                } label: {
                                    MuseumRow(obj: item)
                                }
                            }
                        } header: {
                            Text(sectionHeader)
                        }
                    }
                    .listStyle(.insetGrouped)
                }
            }
            .navigationTitle("KMP App")
            .navigationBarTitleDisplayMode(.large)
            .searchable(
                text: Binding(
                    get: { viewModel.query },
                    set: { viewModel.setSearchQuery(value: $0) }
                ),
                prompt: "Search artworks"
            )
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Button("All departments") {
                            viewModel.setDepartmentFilter(department: nil)
                        }
                        Divider()
                        ForEach(viewModel.departments, id: \.self) { department in
                            Button {
                                let currentlySelected = viewModel.departmentFilter == department
                                viewModel.setDepartmentFilter(
                                    department: currentlySelected ? nil : department
                                )
                            } label: {
                                HStack {
                                    Text(department)
                                    if viewModel.departmentFilter == department {
                                        Image(systemName: "checkmark")
                                    }
                                }
                            }
                        }
                    } label: {
                        Image(systemName: viewModel.departmentFilter == nil
                              ? "line.3.horizontal.decrease.circle"
                              : "line.3.horizontal.decrease.circle.fill")
                    }
                }
            }
        }
    }

    private var sectionHeader: String {
        if let department = viewModel.departmentFilter {
            return department
        }
        return "Artworks"
    }
}

struct EmptyCollectionView: View {
    let title: String

    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "photo.on.rectangle.angled")
                .font(.system(size: 40))
                .foregroundStyle(.secondary)
            Text(title)
                .font(.title3)
                .fontWeight(.semibold)
            Text("Try a different search or filter.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding()
    }
}

struct MuseumRow: View {
    let obj: MuseumObject

    var body: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: obj.primaryImageSmall)) { phase in
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
            .clipShape(RoundedRectangle(cornerRadius: 8))

            VStack(alignment: .leading, spacing: 4) {
                Text(obj.title.isEmpty ? "Untitled" : obj.title)
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
        let artist = obj.artistDisplayName.isEmpty ? "Unknown artist" : obj.artistDisplayName
        if obj.objectDate.isEmpty {
            return artist
        }
        return "\(artist) · \(obj.objectDate)"
    }
}
