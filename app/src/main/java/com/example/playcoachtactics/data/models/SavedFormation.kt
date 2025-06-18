package com.example.playcoachtactics.data.models

data class SavedFormation(
    val name: String = "",
    val positions: List<PlayerPosition> = emptyList()
)

data class PlayerPosition(
    val number: Int = 0,
    val offset: RelativeOffset = RelativeOffset()
)
