package ru.savushkina.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * CalculatorViewModel - слой логики и состояния
 *
 * Что такое ViewModel:
 *  - класс, который хранит состояние и логику экрана
 *  - переживает пересоздание Activity (поворот экрана, смена темы)
 *  - не имеет ссылки на View, Context, Activity
 *
 * Почему ViewModel не знает о View:
 *  1) утечка памяти: ViewModel живет дольше Activity. Если бы она держала ссылку на Activity - Activity не уничтожалась бы  GC
 *  2) разделение ответственности: ViewModel - логика, View - отображение
 *  3) тестируемость: ViewModel можно тестировать без Android
 *
 * Почему ViewModel не работает с getString():
 *  getString - метод Context. У ViewModel нет Context.
 *  Строки - забота UI, ViewModel отдает сырые данные, UI их форматирует
 *
 *  Что такое UDF (Unidirectional data Flow):
 *   - события идут вверх (UI -> ViewModel): пользователь нажал кнопку, Activity вызывает viewModel.onCalculateClick(...)
 *   - состояние идет вниз (ViewModel -> UI): ViewModel пишет в _uiState, UI получает новое значение через collect
 *   Однонаправленность = предсказуемость
 */
class CalculatorViewModel : ViewModel() {
    /**
     * _uiState - приватное изменяемое состояние
     * MutableStateFlow - поток, который хранит текущее значение и уведомляет подписчиков при изменении
     */
    private val _uiState = MutableStateFlow(CalculatorUiState())

    /**
     * uiState - публичное состояние только для чтения
     * asStateFlow() возвращает StateFlow (read-only обертку над MutableStateFlow)
     * снаружи можно:
     *  - uiState.value - прочитать текущее значение
     *  - uiState.collect { } - подписаться на обновления
     * нельзя:
     *  - uiState.value = ...
     *
     * Почему StateFlow, а не LiveData:
     *  - StateFlow - часть корутин, работает с Flow-операторами
     *  - StateFlow всегда имеет начальное значение (не nullable)
     *  - StateFlow - hot-поток, хранит текущее значение и replays его новым подписчикам (в отличие от холодного Flow)
     *  - В Compose StateFlow работает напрямую через collectAsState()
     *  - LiveData - legacy, привязан к lifecycle, менее гибок
     */
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()
    val operations: List<String> = listOf("+", "-", "/", "*")

    /**
     * onCalculateClick - событие от UI "пользователь нажал Вычислить"
     *
     * Почему ничего не возвращает:
     *  UDF. ViewModel не отдает результат по запросу
     *  Она пишет результат в _uiState. UI получит его через collect
     *
     * Как обновляется состояние:
     *  _uiState.update{ it.copy(...) } - атомарная операция
     *  update берет текущее значение, применяет лямбду, сохраняет новое
     *  copy() создает новый объект с измененными полями
     *  Неизмененные поля сохраняются
     */
    fun onCalculateClick(firstInput: String, secondInput: String, op: String){
        val first = firstInput.toDoubleOrNull()
        val second = secondInput.toDoubleOrNull()
        if (first == null || second == null) {
            /**
             * it - текущее состояние (CalculatorUiState)
             */
            _uiState.update {
                it.copy(
                    resultText = "Результат: -",
                    error = "Заполните оба числа"
                )

            }
            return
        }
        if (op == "/" && second == 0.0) {
            _uiState.update {
                it.copy(
                    resultText = "Результат: -",
                    error = "Нельзя делить на ноль"
                )
            }
            return
        }
        val computed = when (op) {
            "+" -> first + second
            "-" -> first - second
            "/" -> first / second
            "*" -> first * second
            else -> null
        }
        _uiState.update {
            it.copy(
                resultText = "Результат: $computed",
                error = null
            )
        }
    }

    /**
     * clearError - сбросить ошибку в состоянии
     *
     * Зачем нужен:
     *  UI в collect показывает Toast при error != null
     *  Но collect вызываенся при каждом обновлении _uiState
     *  Если error останется, Toast будет показываться снова и снова
     *  clearError() записывает error = null -> collect вызывается, но Toast не покажется
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}