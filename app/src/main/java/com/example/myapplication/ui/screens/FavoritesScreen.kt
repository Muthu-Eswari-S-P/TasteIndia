package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.MealSummary
import com.example.myapplication.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: RecipeViewModel,
    onMealClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    
    var favoriteMeals by remember { mutableStateOf<List<MealSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(favoriteIds) {
        isLoading = true
        val list = mutableListOf<MealSummary>()
        for (id in favoriteIds) {
            try {
                val detail = viewModel.getMealDetail(id)
                list.add(MealSummary(detail.idMeal, detail.strMeal, detail.strMealThumb ?: ""))
            } catch (_: Exception) {}
        }
        favoriteMeals = list
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Recipes (${favoriteIds.size})", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                favoriteMeals.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No favorite recipes saved yet.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favoriteMeals, key = { it.idMeal }) { meal ->
                            RecipeCard(
                                meal = meal,
                                isFavorite = true,
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
