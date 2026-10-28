# TasteIndia Android App - Implementation Plan

TasteIndia is an Android intern assignment application showcasing Indian cuisine with rich filtering, searching, sorting, detailed recipe views, offline resilience, persistent favorites, and comprehensive unit testing.

## User Review Required

> [!IMPORTANT]
> - **Tech Stack**: Jetpack Compose, Material 3, Navigation Compose, ViewModel & StateFlow, Retrofit, Kotlin Serialization, Coil, and Preferences DataStore.
> - **Architecture**: Clean MVVM architecture with Repository pattern, in-memory caching & deduplication for detail requests, and offline JSON fixtures.

## Open Questions

- None. All requirements are aligned with TheMealDB V1 API and best practices for modern Android development.

## Proposed Changes

### Build Configuration & Dependencies
#### [MODIFY] [libs.versions.toml](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/gradle/libs.versions.toml)
- Add versions and library coordinates for Compose BOM, Navigation Compose, Lifecycle ViewModel Compose, Retrofit, Kotlin Serialization Converter, Coil Compose, and Preferences DataStore.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/build.gradle.kts)
- Enable Compose feature (`buildFeatures.compose = true`).
- Add dependencies for Compose UI, Material 3, Navigation, ViewModel, Retrofit, Serialization, Coil, DataStore, and Unit Testing (JUnit, MockWebServer / Turbine if needed, coroutines-test).

---

### Data & Network Layer
#### [NEW] [MealApi.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/remote/MealApi.kt)
- Retrofit interface defining endpoints for Indian filter (`filter.php?a=Indian`), lookup (`lookup.php?i={mealId}`), categories (`list.php?c=list`), and ingredients (`filter.php?i={ingredient}`).

#### [NEW] [MealModels.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/model/MealModels.kt)
- Kotlin serialization data classes for meal summaries, meal details (with dynamic ingredient/measurement mapping helper), categories, and ingredients.

#### [NEW] [MealRepository.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/repository/MealRepository.kt)
- Repository handling network requests, caching, detail request deduplication, offline fallback fixtures, and filter intersection within the Indian base collection.

---

### Domain & State Management
#### [NEW] [RecipeViewModel.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/viewmodel/RecipeViewModel.kt)
- Manages UI state (`StateFlow`), search query, category filter, ingredient filter, favourites-only filter, sorting (A–Z / Z–A), loading/error/offline states, navigation state restoration, and persistent favorites via DataStore.

---

### UI Layer & Navigation
#### [NEW] [Screen.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/navigation/Screen.kt)
- Navigation destinations (Recipe List, Recipe Details, Favorites).

#### [NEW] [RecipeListScreen.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/RecipeListScreen.kt)
- Scrollable Indian recipe list with images, names, favorite controls, search bar, filter chips (Category, Ingredient, Favorites-only), sorting options, result count, active-filter feedback, and "Clear all".

#### [NEW] [RecipeDetailScreen.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/RecipeDetailScreen.kt)
- Detailed recipe view showing all non-empty fields, ingredient/measurement pairs, instructions, tags, and valid web/video links.

#### [NEW] [FavoritesScreen.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/FavoritesScreen.kt)
- Separate favorites destination displaying saved recipes with removal capability.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/app/MainActivity.kt)
- Host for NavGraph and Compose theme setup.

---

### Unit Testing & Documentation
#### [NEW] [RecipeViewModelTest.kt](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/app/src/test/java/com/example/myapplication/RecipeViewModelTest.kt)
- At least four focused tests covering:
  1. Serialization & ingredient normalization.
  2. Filter intersection (Category + Ingredient within Indian collection).
  3. Latest-query/list state and sorting.
  4. Favorites restoration & persistence.

#### [NEW] [README.md](file:///C:/Users/ELCOT/AndroidStudioProjects/MyApplication/README.md)
- Setup instructions, architecture overview, API usage, test commands, known limitations, and AI-use disclosure.

## Verification Plan

### Automated Tests
- Run `./gradlew test` to execute unit tests verifying serialization, filtering, sorting, and state management.

### Manual Verification
- Deploy app to emulator/device using `./gradlew installDebug` or Android Studio run button, test search, filters, sorting, detail loading, favorites persistence, offline mode, and scroll position restoration.
