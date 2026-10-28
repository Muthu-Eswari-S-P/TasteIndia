package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.screens.FavoritesScreen
import com.example.myapplication.ui.screens.RecipeDetailScreen
import com.example.myapplication.ui.screens.RecipeListScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.RecipeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: RecipeViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Screen.RecipeList.route
                ) {
                    composable(Screen.RecipeList.route) {
                        RecipeListScreen(
                            viewModel = viewModel,
                            onMealClick = { mealId ->
                                navController.navigate(Screen.RecipeDetail.createRoute(mealId))
                            },
                            onNavigateFavorites = {
                                navController.navigate(Screen.Favorites.route)
                            }
                        )
                    }
                    composable(
                        route = Screen.RecipeDetail.route,
                        arguments = listOf(navArgument("mealId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
                        RecipeDetailScreen(
                            mealId = mealId,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Favorites.route) {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onMealClick = { mealId ->
                                navController.navigate(Screen.RecipeDetail.createRoute(mealId))
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
