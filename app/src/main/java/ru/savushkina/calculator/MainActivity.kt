package ru.savushkina.calculator

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var firstNumber: EditText
    lateinit var secondNumber: EditText
    lateinit var operationsSpinner: Spinner
    lateinit var btnCalculate: Button
    lateinit var result: TextView

    val operations = listOf("+", "-", "/", "*")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstNumber = findViewById(R.id.first_number)
        secondNumber = findViewById(R.id.second_number)
        operationsSpinner = findViewById(R.id.operations)
        btnCalculate = findViewById(R.id.btn_calculate)
        result = findViewById(R.id.result)


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, operations)
        operationsSpinner.adapter = adapter

        calculate()


    }

    private fun calculate() {
        btnCalculate.setOnClickListener {
            val first = firstNumber.text.toString().toDoubleOrNull()
            val second = secondNumber.text.toString().toDoubleOrNull()
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

