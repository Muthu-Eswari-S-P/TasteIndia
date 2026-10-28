package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.model.MealDetail
import com.example.myapplication.data.model.MealSummary
import com.example.myapplication.data.remote.MealApi
import com.example.myapplication.data.repository.MealRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

private val android.content.Context.favoritesDataStore by preferencesDataStore(name = "favorites_prefs")

enum class SortOrder {
    AZ, ZA
}

sealed interface UiState {
    object Loading : UiState
    data class Success(val meals: List<MealSummary>) : UiState
    data class Error(val message: String) : UiState
}

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl(MealApi.BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
    private val api = retrofit.create(MealApi::class.java)
    val repository = MealRepository(api)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var allMealsList: List<MealSummary> = emptyList()

    // Filter and Search states
    var searchQuery by mutableStateOf("")
        private set
    var selectedCategory by mutableStateOf<String?>(null)
        private set
    var selectedIngredient by mutableStateOf<String?>(null)
        private set
    var favoritesOnly by mutableStateOf(false)
        private set
    var sortOrder by mutableStateOf(SortOrder.AZ)
        private set

    // Filter options lists
    var categories by mutableStateOf<List<String>>(emptyList())
        private set
    var ingredients by mutableStateOf<List<String>>(emptyList())
        private set

    // Scroll position restoration state
    var listScrollIndex by mutableStateOf(0)
    var listScrollOffset by mutableStateOf(0)

    // Favorites DataStore
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_meal_ids")
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        loadFavorites()
        loadData()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            getApplication<Application>().applicationContext.favoritesDataStore.data
                .map { preferences -> preferences[FAVORITES_KEY] ?: emptySet() }
                .collect { ids ->
                    _favoriteIds.value = ids
                    if (favoritesOnly) {
                        updateFilteredList()
                    }
                }
        }
    }

    fun toggleFavorite(mealId: String) {
        viewModelScope.launch {
            getApplication<Application>().applicationContext.favoritesDataStore.edit { preferences ->
                val current = preferences[FAVORITES_KEY] ?: emptySet()
                val updated = if (current.contains(mealId)) current - mealId else current + mealId
                preferences[FAVORITES_KEY] = updated
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val baseMeals = repository.getIndianMeals()
                allMealsList = baseMeals
                categories = repository.getCategories()
                ingredients = repository.getIngredients()
                updateFilteredList()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Failed to load recipes. Please check your network connection.")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        updateFilteredList()
    }

    fun selectCategory(category: String?) {
        selectedCategory = if (selectedCategory == category) null else category
        updateFilteredList()
    }

    fun selectIngredient(ingredient: String?) {
        selectedIngredient = if (selectedIngredient == ingredient) null else ingredient
        updateFilteredList()
    }

    fun toggleFavoritesOnly() {
        favoritesOnly = !favoritesOnly
        updateFilteredList()
    }

    fun updateSortOrder(order: SortOrder) {
        sortOrder = order
        updateFilteredList()
    }

    fun clearAllFilters() {
        searchQuery = ""
        selectedCategory = null
        selectedIngredient = null
        favoritesOnly = false
        sortOrder = SortOrder.AZ
        updateFilteredList()
    }

    private fun updateFilteredList() {
        var filtered = allMealsList

        // Search query
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            filtered = filtered.filter { it.strMeal.lowercase().contains(q) }
        }

        // Favorites only
        if (favoritesOnly) {
            val favs = _favoriteIds.value
            filtered = filtered.filter { favs.contains(it.idMeal) }
        }

        // Category & Ingredient intersection (Requirement 8)
        viewModelScope.launch {
            var currentFiltered = filtered

            if (selectedCategory != null) {
                try {
                    val catMeals = api.getMealsByCategory(selectedCategory!!).meals.orEmpty()
                    val catIds = catMeals.map { it.idMeal }.toSet()
                    currentFiltered = currentFiltered.filter { catIds.contains(it.idMeal) }
                } catch (_: Exception) {}
            }

            if (selectedIngredient != null) {
                try {
                    val ingMeals = api.getMealsByIngredient(selectedIngredient!!).meals.orEmpty()
                    val ingIds = ingMeals.map { it.idMeal }.toSet()
                    currentFiltered = currentFiltered.filter { ingIds.contains(it.idMeal) }
                } catch (_: Exception) {}
            }

            // Sorting A-Z or Z-A
            currentFiltered = when (sortOrder) {
                SortOrder.AZ -> currentFiltered.sortedBy { it.strMeal }
                SortOrder.ZA -> currentFiltered.sortedByDescending { it.strMeal }
            }

            _uiState.value = UiState.Success(currentFiltered)
        }
    }

    suspend fun getMealDetail(mealId: String): MealDetail {
        return repository.getMealDetail(mealId)
    }
}
