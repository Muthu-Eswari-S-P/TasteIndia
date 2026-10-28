package com.example.myapplication.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.model.MealDetail
import com.example.myapplication.ui.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    mealId: String,
    viewModel: RecipeViewModel,
    onBackClick: () -> Unit
) {
    val actualContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val isFavorite = favoriteIds.contains(mealId)

    // Reset state whenever mealId changes to prevent showing stale details from previously viewed recipes
    var mealDetail by remember(mealId) { mutableStateOf<MealDetail?>(null) }
    var isLoading by remember(mealId) { mutableStateOf(true) }
    var errorMessage by remember(mealId) { mutableStateOf<String?>(null) }

    LaunchedEffect(mealId) {
        isLoading = true
        mealDetail = null
        errorMessage = null
        try {
            mealDetail = viewModel.getMealDetail(mealId)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Failed to load recipe details."
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mealDetail?.strMeal ?: "Loading Recipe...") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(mealId) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
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
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    mealDetail = viewModel.getMealDetail(mealId)
                                    errorMessage = null
                                } catch (e: Exception) {
                                    errorMessage = e.localizedMessage
                                } finally {
                                    isLoading = false
                                }
                            }
                        }) {
                            Text("Retry")
                        }
                    }
                }
                mealDetail != null -> {
                    val detail = mealDetail!!
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        if (!detail.strMealThumb.isNullOrBlank()) {
                            AsyncImage(
                                model = detail.strMealThumb,
                                contentDescription = detail.strMeal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(MaterialTheme.shapes.large),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Text(
                            text = detail.strMeal,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!detail.strCategory.isNullOrBlank()) {
                                AssistChip(onClick = {}, label = { Text("Category: ${detail.strCategory}") })
                            }
                            if (!detail.strArea.isNullOrBlank()) {
                                AssistChip(onClick = {}, label = { Text("Area: ${detail.strArea}") })
                            }
                        }

                        if (!detail.strTags.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tags: ${detail.strTags}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Ingredients & Measurements",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val pairs = detail.getIngredientMeasurePairs()
                        if (pairs.isEmpty()) {
                            Text("No ingredient details available.", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            pairs.forEach { (ingredient, measure) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• $ingredient", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    Text(measure, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = detail.strInstructions ?: "No instructions provided.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        val source = detail.strSource
                        val youtube = detail.strYoutube

                        if (!source.isNullOrBlank() || !youtube.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "External Links",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (!source.isNullOrBlank()) {
                                    Button(onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source))
                                            actualContext.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }) {
                                        Text("View Source")
                                    }
                                }
                                if (!youtube.isNullOrBlank()) {
                                    Button(onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtube))
                                            actualContext.startActivity(intent)
                                        } catch (_: Exception) {}
                                    }) {
                                        Text("Watch Video")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
