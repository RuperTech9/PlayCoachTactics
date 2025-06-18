package com.example.playcoachtactics.data.models

import androidx.annotation.DrawableRes

data class PlayerInfo(
    val id: String = "",  // ID de Firestore
    val number: Int,
    val firstName: String,
    val lastName: String,
    val nickname: String = "",
    val position: String = "Jugador",
    val imageResName: String = "",
    @DrawableRes
    val imageResId: Int = 0
)
