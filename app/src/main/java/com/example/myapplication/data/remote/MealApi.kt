package com.example.myapplication.data.remote

import com.example.myapplication.data.model.CategoryResponse
import com.example.myapplication.data.model.IngredientResponse
import com.example.myapplication.data.model.MealDetailResponse
import com.example.myapplication.data.model.MealSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("filter.php")
    suspend fun getMealsByArea(@Query("a") area: String = "Indian"): MealSummaryResponse

    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") mealId: String): MealDetailResponse

    @GET("list.php")
    suspend fun listCategories(@Query("c") category: String = "list"): CategoryResponse

    @GET("list.php")
    suspend fun listIngredients(@Query("i") ingredient: String = "list"): IngredientResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealSummaryResponse

    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): MealSummaryResponse

    companion object {
        const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
}
