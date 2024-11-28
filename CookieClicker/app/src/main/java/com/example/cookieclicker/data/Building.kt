package com.example.cookieclicker.data

data class Building(
    val id: Long,
    val name: String,
    val icon: Int,
    val count: Int,
    val cost: Int,
    val income: Double,
    val isAvailable: Boolean,
)
