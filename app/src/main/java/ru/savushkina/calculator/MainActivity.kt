package ru.savushkina.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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
    lateinit var op: String
    var res: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstNumber = findViewById(R.id.first_number)
        secondNumber = findViewById(R.id.second_number)
        operationsSpinner = findViewById(R.id.operations)
        btnCalculate = findViewById(R.id.btn_calculate)
        result = findViewById(R.id.result)


        // считать два числа
        // считать операцию
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, operations)
        operationsSpinner.adapter = adapter
        operationsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                op = operations[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        // определить кнопку, вычислить
        btnCalculate.setOnClickListener {
            val first = firstNumber.text.toString().toDouble()
            val second = secondNumber.text.toString().toDouble()
            if (op == "/" && second == 0.0){
                Toast.makeText(this, "Нельзя делить на ноль", Toast.LENGTH_LONG ).show()
                return@setOnClickListener
            }
            res = when (op) {
                "+" -> first + second
                "-" -> first - second
                "/" -> first / second
                "*" -> first * second
                else -> null
            }
            // показать результат или ошибку
            result.text = res.toString()
        }


    }




}

