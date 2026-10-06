package ru.savushkina.calculator

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

/**
 * MainActivity - главный и единственный экран приложения
 * Наследуется от AppCompatActivity
 * - дает совместимость с разными версиями Android
 * - требует AppCompat-тему (themes.xml)
 */
class MainActivity : AppCompatActivity() {
    /**
     * lateinit - обещание компилятору: инициализирую позже, в onCreate
     * позволяет не делать тип nullable
     * Если обратиться до инициализации - UninitializedPropertyAccessException.
    */
    private lateinit var firstNumber: EditText
    private lateinit var secondNumber: EditText
    private lateinit var operationsSpinner: Spinner
    private lateinit var btnCalculate: Button
    private lateinit var result: TextView

    /**
     * by ViewModels() - делегат для получения ViewModel
     *
     * Что делает:
     *  - создает ViewModel при первом обращении
     *  - при пересоздании Activity возвращает ТУ ЖЕ ViewModel (потому что ViewModel хранится в ViewModelStore, который переживает пересоздание)
     *  - привязывает ViewModel к lifecycle Activity:
     *   когда Activity уничтожается окончательно (не поворот), вызывается onCleared() у ViewModel
     *
     * Почему by viewModels():
     *  - не нужно вручную создавать ViewModelProvider
     *  - тип выводится автоматически (CalculatorViewModel)
     *  - безопасно: если ViewModel уже есть - вернется она
     */
    private val viewModel: CalculatorViewModel by viewModels()

    /**
     * onCreate - точка входа Activity
     * Вызывается один раз при создании
     * Здесь: связываем разметку, находим View, вешаем слушатели
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * super - обязательный вызов родителя
         * Без него AppCompatActivity не выполнит свою инициализацию
         */
        super.onCreate(savedInstanceState)
        /**
         * setContentView связывает Activity с XML разметкой
         */
        setContentView(R.layout.activity_main)

        firstNumber = findViewById(R.id.first_number)
        secondNumber = findViewById(R.id.second_number)
        operationsSpinner = findViewById(R.id.operations)
        btnCalculate = findViewById(R.id.btn_calculate)
        result = findViewById(R.id.result)

        /**
         * ArrayAdapter - мост между списком данных и Spinner
         */
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.operations)
        operationsSpinner.adapter = adapter

        setupClickListener()

        /**
         * Подписка на uiState
         *
         * Как это работает:
         *  1) lifecycleScope.launch - корутина, привязанная к Activity
         *  Отменяется при onDestroy
         *
         *  2) repeatOnLifecycle(STARTED) - блок выполняется, пока Activity в состоянии STARTED или выше
         *  При onStop - отписка, при onStart - снова подписка
         *
         *  3) viewModel.uiState.collect { state -> ... } - сбор значений
         *  collect вызывается при КАЖДОМ обновлении _uiState
         *
         * Зачем repeatOnLifecycle:
         *  - без него collect работал бы даже когда Activity невидима, расходуя ресурсы
         *  - при повороте подписка корректно пересоздается
         *  - при onStop - не тратим батарею
         *
         *  state - это снимок состояния на момент вызова collect
         */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    result.text = state.resultText
                    if (state.error != null){
                        Toast.makeText(this@MainActivity, state.error, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }
            }
        }

    }

    private fun setupClickListener() {
        /**
         * setOnClickListener - лямбда, которая выполняется при каждом нажатии
         */
        btnCalculate.setOnClickListener {
            /**
             * Считываем текст из EditText
             * .text возвращает Editable -> .toString() дает String
             * toDoubleOrNull() пытается преобразовать в Double
             * - если получилось - Double
             * - если не получилось (пусто, буквы) - null
             */
            val first = firstNumber.text.toString()
            val second = secondNumber.text.toString()
            /**
             * selectedItem - текущий выбранный элемент Spinner (Any)
             */
            val op = operationsSpinner.selectedItem.toString()

            viewModel.onCalculateClick(first, second, op)
        }
    }

}

