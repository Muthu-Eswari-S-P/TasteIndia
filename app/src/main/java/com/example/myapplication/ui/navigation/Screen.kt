package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object RecipeList : Screen("recipe_list")
    object RecipeDetail : Screen("recipe_detail/{mealId}") {
        fun createRoute(mealId: String) = "recipe_detail/$mealId"
    }
    object Favorites : Screen("favorites")
}
