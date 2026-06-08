package com.example.nutribalance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.core.widget.doAfterTextChanged
import com.example.nutribalance.databinding.ActivityMainBinding
import com.example.nutribalance.presentation.DiaryScreen
import com.example.nutribalance.presentation.DietViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.unit.dp

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DietViewModel by viewModels()

    // Внутреннее состояние ComposeView для интерактивного расчета ИМТ
    private val bmiResultState = mutableStateOf("Введите данные роста и веса")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем View Binding по нашему ТЗ
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отрисовка ComposeView внутри структуры XML макета
        binding.bmiComposeView.setContent {
            MaterialTheme {
                Text(
                    text = bmiResultState.value,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = androidx.compose.ui.Modifier.padding(vertical = 8.dp)
                )
            }
        }

        binding.etWeight.doAfterTextChanged { calculateBmi() }
        binding.etHeight.doAfterTextChanged { calculateBmi() }

        binding.btnOpenDiary.setOnClickListener {
            // Это временная Activity, потом поменяю
            setContentView(binding.bmiComposeView.rootView)
            binding.bmiComposeView.setContent {
                MaterialTheme {
                    DiaryScreen(viewModel = viewModel, onBack = {
                        // Возврат на XML Профиль
                        setContentView(binding.root)
                    })
                }
            }
        }
    }

    private fun calculateBmi() {
        val weight = binding.etWeight.text.toString().toDoubleOrNull()
        val heightCm = binding.etHeight.text.toString().toDoubleOrNull()

        if (weight != null && heightCm != null && heightCm > 0) {
            val heightMeters = heightCm / 100.0
            val bmi = weight / (heightMeters * heightMeters)

            val status = when {
                bmi < 18.5 -> "Дефицит веса"
                bmi in 18.5..24.9 -> "Нормальный вес (Всё супер!)"
                bmi in 25.0..29.9 -> "Избыточный вес"
                else -> "Ожирение"
            }
            // Обновляем Compose-текст прямо из логики XML-обработчика!
            bmiResultState.value = "Ваш ИМТ: ${String.format("%.1f", bmi)} ($status)"
        } else {
            bmiResultState.value = "Введите корректные данные"
        }
    }
}
