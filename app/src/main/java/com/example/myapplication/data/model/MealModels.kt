package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealSummaryResponse(
    @SerialName("meals") val meals: List<MealSummary>? = null
)

@Serializable
data class MealSummary(
    @SerialName("idMeal") val idMeal: String,
    @SerialName("strMeal") val strMeal: String,
    @SerialName("strMealThumb") val strMealThumb: String
)

@Serializable
data class MealDetailResponse(
    @SerialName("meals") val meals: List<MealDetail>? = null
)

@Serializable
data class MealDetail(
    @SerialName("idMeal") val idMeal: String,
    @SerialName("strMeal") val strMeal: String,
    @SerialName("strDrinkAlternate") val strDrinkAlternate: String? = null,
    @SerialName("strCategory") val strCategory: String? = null,
    @SerialName("strArea") val strArea: String? = null,
    @SerialName("strInstructions") val strInstructions: String? = null,
    @SerialName("strMealThumb") val strMealThumb: String? = null,
    @SerialName("strTags") val strTags: String? = null,
    @SerialName("strYoutube") val strYoutube: String? = null,
    @SerialName("strSource") val strSource: String? = null,
    
    // Ingredients 1 to 20
    @SerialName("strIngredient1") val strIngredient1: String? = null,
    @SerialName("strIngredient2") val strIngredient2: String? = null,
    @SerialName("strIngredient3") val strIngredient3: String? = null,
    @SerialName("strIngredient4") val strIngredient4: String? = null,
    @SerialName("strIngredient5") val strIngredient5: String? = null,
    @SerialName("strIngredient6") val strIngredient6: String? = null,
    @SerialName("strIngredient7") val strIngredient7: String? = null,
    @SerialName("strIngredient8") val strIngredient8: String? = null,
    @SerialName("strIngredient9") val strIngredient9: String? = null,
    @SerialName("strIngredient10") val strIngredient10: String? = null,
    @SerialName("strIngredient11") val strIngredient11: String? = null,
    @SerialName("strIngredient12") val strIngredient12: String? = null,
    @SerialName("strIngredient13") val strIngredient13: String? = null,
    @SerialName("strIngredient14") val strIngredient14: String? = null,
    @SerialName("strIngredient15") val strIngredient15: String? = null,
    @SerialName("strIngredient16") val strIngredient16: String? = null,
    @SerialName("strIngredient17") val strIngredient17: String? = null,
    @SerialName("strIngredient18") val strIngredient18: String? = null,
    @SerialName("strIngredient19") val strIngredient19: String? = null,
    @SerialName("strIngredient20") val strIngredient20: String? = null,

    // Measures 1 to 20
    @SerialName("strMeasure1") val strMeasure1: String? = null,
    @SerialName("strMeasure2") val strMeasure2: String? = null,
    @SerialName("strMeasure3") val strMeasure3: String? = null,
    @SerialName("strMeasure4") val strMeasure4: String? = null,
    @SerialName("strMeasure5") val strMeasure5: String? = null,
    @SerialName("strMeasure6") val strMeasure6: String? = null,
    @SerialName("strMeasure7") val strMeasure7: String? = null,
    @SerialName("strMeasure8") val strMeasure8: String? = null,
    @SerialName("strMeasure9") val strMeasure9: String? = null,
    @SerialName("strMeasure10") val strMeasure10: String? = null,
    @SerialName("strMeasure11") val strMeasure11: String? = null,
    @SerialName("strMeasure12") val strMeasure12: String? = null,
    @SerialName("strMeasure13") val strMeasure13: String? = null,
    @SerialName("strMeasure14") val strMeasure14: String? = null,
    @SerialName("strMeasure15") val strMeasure15: String? = null,
    @SerialName("strMeasure16") val strMeasure16: String? = null,
    @SerialName("strMeasure17") val strMeasure17: String? = null,
    @SerialName("strMeasure18") val strMeasure18: String? = null,
    @SerialName("strMeasure19") val strMeasure19: String? = null,
    @SerialName("strMeasure20") val strMeasure20: String? = null
) {
    fun getIngredientMeasurePairs(): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        val ingredients = listOf(
            strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
            strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
            strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
            strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
        )
        val measures = listOf(
            strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
            strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
            strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
            strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
        )
        for (i in ingredients.indices) {
            val ing = ingredients[i]?.trim()
            val meas = measures[i]?.trim() ?: ""
            if (!ing.isNullOrBlank() && ing.lowercase() != "null") {
                list.add(Pair(ing, meas))
            }
        }
        return list
    }
}

@Serializable
data class CategoryResponse(
    @SerialName("meals") val meals: List<CategoryItem>? = null
)

@Serializable
data class CategoryItem(
    @SerialName("strCategory") val strCategory: String
)

@Serializable
data class IngredientResponse(
    @SerialName("meals") val meals: List<IngredientItem>? = null
)

@Serializable
data class IngredientItem(
    @SerialName("strIngredient") val strIngredient: String? = null
)
