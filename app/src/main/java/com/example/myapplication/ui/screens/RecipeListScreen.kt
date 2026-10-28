package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.model.MealSummary
import com.example.myapplication.ui.viewmodel.RecipeViewModel
import com.example.myapplication.ui.viewmodel.SortOrder
import com.example.myapplication.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeViewModel,
    onMealClick: (String) -> Unit,
    onNavigateFavorites: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.listScrollIndex,
        initialFirstVisibleItemScrollOffset = viewModel.listScrollOffset
    )

    // Save scroll position on leave
    DisposableEffect(listState) {
        onDispose {
            viewModel.listScrollIndex = listState.firstVisibleItemIndex
            viewModel.listScrollOffset = listState.firstVisibleItemScrollOffset
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TasteIndia Recipes", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateFavorites) {
                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                            Text("${favoriteIds.size}")
                        }
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Indian recipes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filters & Sort Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorites only toggle chip
                FilterChip(
                    selected = viewModel.favoritesOnly,
                    onClick = { viewModel.toggleFavoritesOnly() },
                    label = { Text("Favorites") },
                    leadingIcon = {
                        Icon(
                            if (viewModel.favoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                // Sort toggle chip
                FilterChip(
                    selected = true,
                    onClick = {
                        val next = if (viewModel.sortOrder == SortOrder.AZ) SortOrder.ZA else SortOrder.AZ
                        viewModel.updateSortOrder(next)
                    },
                    label = { Text(if (viewModel.sortOrder == SortOrder.AZ) "Sort: A–Z" else "Sort: Z–A") }
                )
            }

            // Categories horizontal scroll
            if (viewModel.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Categories:", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.categories) { cat ->
                        FilterChip(
                            selected = viewModel.selectedCategory == cat,
                            onClick = { viewModel.selectCategory(cat) },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            // Ingredients horizontal scroll
            if (viewModel.ingredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Main Ingredients:", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.ingredients.take(25)) { ing ->
                        FilterChip(
                            selected = viewModel.selectedIngredient == ing,
                            onClick = { viewModel.selectIngredient(ing) },
                            label = { Text(ing) }
                        )
                    }
                }
            }

            // Result count & Clear All
            val hasActiveFilters = viewModel.searchQuery.isNotEmpty() ||
                    viewModel.selectedCategory != null ||
                    viewModel.selectedIngredient != null ||
                    viewModel.favoritesOnly ||
                    viewModel.sortOrder != SortOrder.AZ

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val count = (uiState as? UiState.Success)?.meals?.size ?: 0
                Text("Results: $count", style = MaterialTheme.typography.bodyMedium)

                if (hasActiveFilters) {
                    TextButton(onClick = { viewModel.clearAllFilters() }) {
                        Text("Clear all")
                    }
                }
            }

            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Main Content State Handling
            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadData() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    if (state.meals.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No recipes found matching your criteria.", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.meals, key = { it.idMeal }) { meal ->
                                val isFav = favoriteIds.contains(meal.idMeal)
                                RecipeCard(
                                    meal = meal,
                                    isFavorite = isFav,
                                    onClick = { onMealClick(meal.idMeal) },
                                    onFavoriteClick = { viewModel.toggleFavorite(meal.idMeal) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    meal: MealSummary,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = meal.strMeal,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${meal.idMeal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
