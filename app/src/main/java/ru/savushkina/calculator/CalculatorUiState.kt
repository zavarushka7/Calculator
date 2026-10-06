package ru.savushkina.calculator

/**
 * CalculatorUiState - модель состояния UI
 *
 * Что это:
 *  data class, описывающий ВСЕ, что видит пользователь на экране
 *  "единый источник правды" (Single Source of Truth) для UI
 *
 * Почему data class:
 * - автоматически генерирует equals(), hashCode(), toString(), copy()
 * - copy() позволяет создать новый объект с измененными полями - это ключевой инструмент для обновления состояния в StateFlow
 *
 * Почему поля val?
 *  состояние незменяемое - чтобы изменить создаем новый объект через copy().
 *  это предотвращает случайные мутации из разных мест
 *
 * Почему значения по умолчанию:
 *  чтобы можно было написать CalculatorUiState() без аргументов - начальное состояние экрана (пусто, без ошибок)
 */
data class CalculatorUiState(
    val resultText: String = "",
    val error: String? = null,
)