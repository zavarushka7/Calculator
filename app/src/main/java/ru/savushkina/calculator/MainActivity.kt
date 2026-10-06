package ru.savushkina.calculator

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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
    lateinit var firstNumber: EditText
    lateinit var secondNumber: EditText
    lateinit var operationsSpinner: Spinner
    lateinit var btnCalculate: Button
    lateinit var result: TextView

    val operations = listOf("+", "-", "/", "*")

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
            operations)
        operationsSpinner.adapter = adapter

        setupClickListener()

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
            val first = firstNumber.text.toString().toDoubleOrNull()
            val second = secondNumber.text.toString().toDoubleOrNull()

            /**
             * selectedItem - текущий выбранный элемент Spinner (Any)
             */
            val op = operationsSpinner.selectedItem.toString()

            if (first == null || second == null) {
                result.text = getString(R.string.result_empty)
                Toast.makeText(this, "Введите оба числа", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (op == "/" && second == 0.0) {
                result.text = getString(R.string.result_empty)
                Toast.makeText(this, "Нельзя делить на ноль", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val computed = when (op) {
                "+" -> first + second
                "-" -> first - second
                "/" -> first / second
                "*" -> first * second
                else -> null
            }
            result.text = getString(R.string.result, computed.toString())


        }
    }

}

