package com.example.nutribalance.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nutribalance.core.domain.model.Meal
import com.example.nutribalance.core.domain.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(viewModel: DietViewModel, onBack: () -> Unit) {
    val meals by viewModel.mealsState.collectAsState()
    val summary by viewModel.nutritionSummary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val targetGoal = 2000.0 // Норма калорий худеющего

    // АНИМАЦИЯ 1: Плавное заполнение полосы прогресса калорий (animateFloatAsState)
    val progressTarget = (summary.calories / targetGoal).coerceIn(0.0, 1.0).toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 1000)
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("AI Дневник калорий") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Панель прогресса калорий за день
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Калории: ${summary.calories.toInt()} / ${targetGoal.toInt()} ккал", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Б: ${summary.proteins.toInt()}г  |  Ж: ${summary.fats.toInt()}г  |  У: ${summary.carbs.toInt()}г", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Поле ввода для ИИ
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Что вы съели? (Голосовой/текстовый ввод ИИ)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.addMealViaAi(textInput, MealType.BREAKFAST)
                        textInput = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Распознать через GigaChat ИИ")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список приемов пищи за день
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(meals) { meal ->
                    MealItem(meal = meal)
                }
            }
        }
    }
}

@Composable
fun MealItem(meal: Meal) {
    var expanded by remember { mutableStateOf(false) }

    // АНИМАЦИЯ 2: Плавное раскрытие карточки БЖУ при клике (animateContentSize)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(meal.type.name, style = MaterialTheme.typography.titleMedium)
                Text("${meal.totalCalories.toInt()} ккал", style = MaterialTheme.typography.titleSmall)
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                meal.ingredients.forEach { ingredient ->
                    Text("• ${ingredient.name} (${ingredient.weightGrams.toInt()}г) — ${ingredient.calories.toInt()} ккал", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
