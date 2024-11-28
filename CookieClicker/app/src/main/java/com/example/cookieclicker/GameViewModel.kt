package com.example.cookieclicker

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookieclicker.data.Building
import com.example.cookieclicker.data.ClickerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private var startTime: Long = 0
    private val _clickerUiState = MutableStateFlow(ClickerState())
    private val _buildings = MutableStateFlow<List<Building>>(emptyList())
    private val _toastMessage = MutableSharedFlow<String>()

    val clickerUiState: StateFlow<ClickerState> = _clickerUiState
    val buildings: StateFlow<List<Building>> get() = _buildings
    val toastMessage: SharedFlow<String> = _toastMessage

    init {
        initializeBuildings()
        startCookieProduction()
        initTimer()
    }

    private fun initTimer() {
        startTime = System.currentTimeMillis()

        startTimer()
    }

    private fun startTimer() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                val seconds = (elapsedTime / 1000) % 60
                val minutes = (elapsedTime / (1000 * 60)) % 60

                val newState = _clickerUiState.value.copy(
                    elapsedTime = String.format(
                        "%02d:%02d",
                        minutes,
                        seconds
                    )
                )

                _clickerUiState.value = newState

                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun startCookieProduction() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                updateCookieCount(_clickerUiState.value.cookiesPerSecond)
            }
        }
    }

    private fun updateCookieCount(amount: Double) {
        _clickerUiState.value = _clickerUiState.value.copy(
            cookieCount = _clickerUiState.value.cookieCount + amount
        )
    }

    fun onCookieClicked() {
        updateCookieCount(1.0)
    }

    private fun reduceCookies(amount: Int) {
        if (_clickerUiState.value.cookieCount >= amount) {
            updateCookieCount(-amount.toDouble())
        }
    }

    private fun increaseIncome(amount: Double) {
        val updatedState = _clickerUiState.value.copy(
            cookiesPerSecond = _clickerUiState.value.cookiesPerSecond + amount,
            averageSpeed = (_clickerUiState.value.cookiesPerSecond + amount) * 60.0
        )
        _clickerUiState.value = updatedState
    }

    private fun initializeBuildings() {
        _buildings.value = listOf(
            Building(0, "Клик", R.drawable.item_background, 0, 15, 0.1, false),
            Building(1, "Бабуля", R.drawable.item_background, 0, 100, 1.0, false),
            Building(2, "Ферма", R.drawable.item_background, 0, 1100, 8.0, false),
            Building(3, "Шахта", R.drawable.item_background, 0, 12000, 47.0, false),
            Building(4, "Фабрика", R.drawable.item_background, 0, 130000, 260.0, false),
            Building(5, "Банк", R.drawable.item_background, 0, 1400000, 1400.0, false),
            Building(6, "Храм", R.drawable.item_background, 0, 20000000, 7800.0, false),
            Building(
                7,
                "Башня волшебника",
                R.drawable.item_background,
                0,
                330000000,
                44000.0,
                false
            ),
            Building(
                8,
                "Космический корабль",
                R.drawable.item_background,
                0,
                510000000,
                260000.0,
                false
            )
        )
    }

    fun buyBuilding(building: Building): Boolean {
        val currentCookies = clickerUiState.value.cookieCount

        return if (currentCookies >= building.cost) {
            reduceCookies(building.cost)
            increaseIncome(building.income)

            val updatedBuildings = _buildings.value.map { build ->
                if (building.id == build.id) {
                    build.copy(
                        count = build.count + 1,
                        cost = (build.cost * 1.15).toInt()
                    )
                } else {
                    build
                }
            }
            _buildings.value = updatedBuildings

            viewModelScope.launch {
                _toastMessage.emit("Вы купили \"${building.name}\"!")
            }
            true
        } else {
            viewModelScope.launch {
                _toastMessage.emit("Недостаточно печенек для покупки \"${building.name}\"")
            }
            false
        }
    }

    fun updateBuildingsAvailability() {
        val currentCookies = _clickerUiState.value.cookieCount

        val updatedBuildings = _buildings.value.map { building ->
            building.copy(isAvailable = currentCookies >= building.cost)
        }
        _buildings.value = updatedBuildings
    }
}