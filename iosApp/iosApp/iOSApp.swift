import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            TabView {
                ListView()
                    .tabItem {
                        Label("Projects", systemImage: "shippingbox")
                    }

                FavoritesView()
                    .tabItem {
                        Label("Favorites", systemImage: "heart.fill")
                    }

                PlatformTourView()
                    .tabItem {
                        Label("UI Tour", systemImage: "info.circle")
                    }
            }
        }
    }
}
