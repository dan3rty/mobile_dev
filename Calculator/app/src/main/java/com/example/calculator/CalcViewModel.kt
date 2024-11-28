package com.example.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import net.objecthunter.exp4j.ExpressionBuilder

data class CalculatorState(val expression: String, val result: String)

sealed class CalculatorEvent {
    data class ButtonClick(val buttonText: String) : CalculatorEvent()
}

class CalcViewModel : ViewModel() {
    val state = MutableStateFlow(CalculatorState("", ""))

    fun handle(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.ButtonClick -> onButtonClick(event.buttonText)
        }
    }

    private fun onButtonClick(buttonText: String) {
        when (buttonText) {
            "×" -> appendToExpression("*")
            "÷" -> appendToExpression("/")
            "," -> appendToExpression(".")
            "⌫" -> deleteLastChar()
            "=" -> calculateResult()
            else -> appendToExpression(buttonText)
        }
    }

    private fun appendToExpression(value: String) {
        state.value = state.value.copy(expression = state.value.expression + value)
        calculateResult()
    }

    private fun deleteLastChar() {
        val currentExpression = state.value.expression
        if (currentExpression.isNotEmpty()) {
            state.value = state.value.copy(expression = currentExpression.dropLast(1))
        }
    }

    private fun calculateResult() {
        val resultText = try {
            val result = evaluateExpression(state.value.expression)
            result.toString()
        } catch (e: ArithmeticException) {
            "Ошибка деления на 0"
        } catch (e: Exception) {
            "Ошибка"
        }
        state.value = state.value.copy(result = resultText)
    }

    private fun evaluateExpression(expr: String): Double {
        return ExpressionBuilder(expr).build().evaluate()
    }
}