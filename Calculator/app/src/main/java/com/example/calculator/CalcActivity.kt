package com.example.calculator

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.calculator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class CalcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalcViewModel by lazy {
        ViewModelProvider(this)[CalcViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButtons()

        lifecycleScope.launch {
            viewModel.state.collect { state -> render(state) }
        }
    }

    private fun initButtons() {
        val buttons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6, binding.button7,
            binding.button8, binding.button9, binding.buttonAdd, binding.buttonSub,
            binding.buttonMul, binding.buttonDiv, binding.buttonDec, binding.buttonBack
        )

        buttons.forEach { button ->
            button.setOnClickListener { handle(CalculatorEvent.ButtonClick(button.text.toString())) }
        }
    }

    private fun handle(event: CalculatorEvent) {
        viewModel.handle(event)
    }

    private fun render(state: CalculatorState) {
        binding.expression.text = state.expression
        binding.result.text = state.result
        binding.result.setTextColor(if (state.result.startsWith("Ошибка")) Color.RED else Color.BLACK)
    }
}