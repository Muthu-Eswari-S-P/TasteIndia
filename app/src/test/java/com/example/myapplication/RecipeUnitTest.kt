package com.example.myapplication

import com.example.myapplication.data.model.MealDetail
import com.example.myapplication.data.model.MealSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeUnitTest {

    @Test
    fun testIngredientNormalizationAndPairing() {
        // Test 1: Serialization and ingredient normalization helper
        val detail = MealDetail(
            idMeal = "123",
            strMeal = "Test Meal",
            strIngredient1 = "Chicken",
            strMeasure1 = "500g",
            strIngredient2 = "  ", // blank ingredient should be filtered out
            strMeasure2 = "1 cup",
            strIngredient3 = "null", // null string should be filtered out
            strMeasure3 = "2 tsp",
            strIngredient4 = "Garlic",
            strMeasure4 = "3 cloves"
        )

        val pairs = detail.getIngredientMeasurePairs()
        assertEquals(2, pairs.size)
        assertEquals(Pair("Chicken", "500g"), pairs[0])
        assertEquals(Pair("Garlic", "3 cloves"), pairs[1])
    }

    @Test
    fun testSortingLogic() {
        // Test 2: A-Z and Z-A sorting verification
        val meals = listOf(
            MealSummary("1", "Chicken Biryani", ""),
            MealSummary("2", "Dal Fry", ""),
            MealSummary("3", "Butter Chicken", "")
        )

        val sortedAz = meals.sortedBy { it.strMeal }
        assertEquals("Butter Chicken", sortedAz[0].strMeal)
        assertEquals("Chicken Biryani", sortedAz[1].strMeal)
        assertEquals("Dal Fry", sortedAz[2].strMeal)

        val sortedZa = meals.sortedByDescending { it.strMeal }
        assertEquals("Dal Fry", sortedZa[0].strMeal)
        assertEquals("Chicken Biryani", sortedZa[1].strMeal)
        assertEquals("Butter Chicken", sortedZa[2].strMeal)
    }

    @Test
    fun testSearchQueryFiltering() {
        // Test 3: Search query filtering logic
        val meals = listOf(
            MealSummary("1", "Chicken Handi", ""),
            MealSummary("2", "Dal Fry", ""),
            MealSummary("3", "Chicken Biryani", "")
        )

        val query = "chicken"
        val filtered = meals.filter { it.strMeal.lowercase().contains(query) }
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.strMeal.lowercase().contains("chicken") })
    }

    @Test
    fun testFavoritesSetOperations() {
        // Test 4: Favorites toggle & set persistence simulation
        var favorites = emptySet<String>()
        val mealId = "52804"

        // Add favorite
        favorites = favorites + mealId
        assertTrue(favorites.contains(mealId))

        // Remove favorite
        favorites = favorites - mealId
        assertTrue(!favorites.contains(mealId))
    }
}
