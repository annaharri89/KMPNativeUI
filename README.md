# Platform Gallery

Kotlin Multiplatform demo with shared business logic and native UI on each platform.

Android uses Material 3. iOS uses SwiftUI. Same ViewModels and favorites persistence — different controls on purpose.

Live data comes from my public GitHub repos: [`annaharri89`](https://github.com/annaharri89).

## Demo

![Platform Gallery Android and iOS portfolio demo](docs/platform-gallery-demo.gif)

[Full demo video (MP4)](docs/platform-gallery-demo.mp4)

Shared Kotlin handles networking, filtering, favorites, and ViewModels. Each platform owns its UI:

| | Android (Material 3) | iOS (SwiftUI / HIG) |
|---|---|---|
| Primary navigation | Bottom `NavigationBar` | System `TabView` |
| Project list | `LazyColumn` + `ListItem` | Inset-grouped `List` |
| Search | Material `SearchBar` | `.searchable` |
| Filters | `FilterChip` by language | Toolbar `Menu` by language |
| Detail favorite | FAB heart | Toolbar heart + `Toggle` |
| External link | Material `Button` | SwiftUI `Link` |
| Feedback | `Snackbar` | Alert / confirmation dialog |

Favorites go through a shared `FavoritesStore` backed by [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) (SharedPreferences on Android, NSUserDefaults on iOS).

There’s also a small UI Tour tab on each platform that shows Material vs HIG controls side by side with the same product features.

## Structure

```
PlatformGallery/
├── shared/       # KMP library: commonMain + androidMain + iosMain
├── androidApp/   # Android application (Compose / Material UI)
└── iosApp/       # Xcode project (SwiftUI UI)
```

## Run

**Android** (Android Studio or CLI):

```bash
./gradlew :androidApp:assembleDebug
```

**iOS** (macOS + Xcode): open `iosApp/iosApp.xcodeproj` and run. Xcode builds the `Shared` framework via Gradle before compiling Swift.

Unauthenticated GitHub API calls are capped at 60 requests/hour per IP.

Based on JetBrains’ [KMP-App-Template-Native](https://github.com/Kotlin/KMP-App-Template-Native). Repo metadata from the [GitHub REST API](https://docs.github.com/en/rest).
