package com.example.nutribalance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutribalance.core.domain.model.Meal
import com.example.nutribalance.core.domain.model.MealType
import com.example.nutribalance.core.domain.repository.AiRepository
import com.example.nutribalance.core.domain.repository.MealRepository
import com.example.nutribalance.core.domain.usecase.CalculateDailyNutritionUseCase
import com.example.nutribalance.core.domain.usecase.DailyNutritionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DietViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val aiRepository: AiRepository,
    private val calculateDailyNutritionUseCase: CalculateDailyNutritionUseCase
) : ViewModel() {

    private val _mealsState = MutableStateFlow<List<Meal>>(emptyList())
    val mealsState: StateFlow<List<Meal>> = _mealsState

    private val _nutritionSummary = MutableStateFlow(DailyNutritionResult(0.0, 0.0, 0.0, 0.0))
    val nutritionSummary: StateFlow<DailyNutritionResult> = _nutritionSummary

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadTodayMeals()
    }

    fun loadTodayMeals() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val start = calendar.timeInMillis
            val end = start + 86400000L

            val meals = mealRepository.getMealsByDay(start, end)
            _mealsState.value = meals
            _nutritionSummary.value = calculateDailyNutritionUseCase(meals)
        }
    }

    fun addMealViaAi(text: String, type: MealType) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val ingredients = aiRepository.parseMealText(text)
                if (ingredients.isNotEmpty()) {
                    val newMeal = Meal(
                        type = type,
                        timestamp = System.currentTimeMillis(),
                        ingredients = ingredients
                    )
                    mealRepository.insertMeal(newMeal)
                    loadTodayMeals() // Перезагружаем список
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}
