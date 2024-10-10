package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder

class CalcActivity : AppCompatActivity() {

    private lateinit var expressionTextView: TextView
    private lateinit var resultTextView: TextView
    private val expression = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expressionTextView = findViewById(R.id.expression)
        resultTextView = findViewById(R.id.result)

        initButtons()
    }

    private fun initButtons() {
        val buttonIds = listOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_add, R.id.button_sub,
            R.id.button_mul, R.id.button_div, R.id.button_dec, R.id.button_back
        )

        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener { onButtonClick(it) }
        }
    }

    private fun onButtonClick(view: View) {
        val button = view as Button
        val buttonText = button.text.toString()

        when (buttonText) {
            "×" -> expression.append("*")
            "÷" -> expression.append("/")
            "," -> expression.append(".")
            "⌫" -> if (expression.isNotEmpty()) expression.deleteCharAt(expression.length - 1)
            "=" -> calculateResult()
            else -> expression.append(buttonText)
        }

        expressionTextView.text = expression.toString()
        if (buttonText != "=") calculateResult()
    }

    private fun calculateResult() {
        try {
            val result = evaluateExpression(expression.toString())
            resultTextView.text = result.toString()
        } catch (e: ArithmeticException) {
            resultTextView.text = "Ошибка деления на 0"
        } catch (e: Exception) {
            resultTextView.text = "Ошибка"
        }
    }

    private fun evaluateExpression(expr: String): Double {
        return ExpressionBuilder(expr).build().evaluate()
    }
}