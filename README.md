# TasteIndia 🇮🇳

TasteIndia is an Android application built as an intern assignment showcasing authentic Indian cuisine using TheMealDB API.

---

## Architecture & Tech Stack

- **UI Framework**: Jetpack Compose & Material 3
- **Navigation**: Jetpack Navigation Compose
- **State Management**: ViewModel & Kotlin StateFlow
- **Networking**: Retrofit with OkHttp and Kotlinx Serialization JSON converter
- **Image Loading**: Coil Compose (`AsyncImage`)
- **Persistence**: Preferences DataStore for persistent saved favorites and scroll state preservation
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with Repository pattern, in-memory caching, request deduplication, and offline fallback fixtures.

---

## API Usage

The app uses [TheMealDB V1 API](https://www.themealdb.com/api/json/v1/1/) with educational test key `1`:
- **Indian Base Collection**: `filter.php?a=Indian`
- **Recipe Details**: `lookup.php?i={mealId}`
- **Categories**: `list.php?c=list`
- **Ingredients**: `list.php?i={ingredient}`
- **Category / Ingredient Filtering**: `filter.php?c={category}` & `filter.php?i={ingredient}`

---

## Features Implemented

1. **Scrollable Indian Recipe List**: Displays thumbnail images, recipe names, and favorite toggle buttons.
2. **Advanced Filtering & Search**: Name search, category filter, main ingredient filter, and favorites-only toggle.
3. **Sorting & Feedback**: A–Z and Z–A sorting, active-filter feedback pills, result count, and "Clear all" button.
4. **Detailed Recipe View**: Loaded by stable meal ID, showing all non-empty fields, ingredient/measurement pairs, instructions, tags, and valid source/video links.
5. **Persistent Favorites**: Separate favorites destination with persistent saved IDs and removal.
6. **State & Position Restoration**: Restores search, filters, sorting, and scroll position when returning from details.
7. **Robust Error Handling**: Loading, empty, error, retry, offline resilience with JSON fixtures, timeout, and partial data handling.
8. **Caching & Deduplication**: Detail requests are cached in memory and deduplicated; network calls are not executed during composable rendering.
9. **Unit Testing**: Focused tests covering serialization/ingredient normalization, sorting, search filtering, and favorites persistence.

---

## How to Build & Test

- **Run Unit Tests**:
  ```bash
  ./gradlew test
  ```
- **Build Debug APK**:
  ```bash
  ./gradlew assembleDebug
  ```
- **Install & Run on Connected Device/Emulator**:
  ```bash
  ./gradlew installDebug
  ```

---

## Known Limitations

- TheMealDB free educational tier provides test images and summaries; some external recipe links may occasionally be unavailable or rate-limited, for which offline fallback fixtures ensure uninterrupted usage.

---

## AI-Use Disclosure

This application was developed with assistance from advanced AI code generation tools to structure modern Jetpack Compose layouts, MVVM architecture, Retrofit integration, and unit tests adhering to Android best practices.
