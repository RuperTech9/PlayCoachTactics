package com.example.playcoachtactics.ui.components

import com.example.playcoachtactics.data.models.PlayerInfo
import com.example.playcoachtactics.data.models.RelativeOffset

fun getPredefinedFormationRelativeOffsets(
    name: String,
    teamPlayers: List<PlayerInfo>
): List<Pair<Int, RelativeOffset>> {
    return when (name) {
        "1-3-4-3" -> {
            val portero = teamPlayers.firstOrNull()
            val jugadoresCampo = teamPlayers.drop(1).take(10)

            val posiciones = listOf(
                18f to 70f,
                45f to 70f,
                69f to 70f,
                10f to 45f,
                30f to 45f,
                59f to 45f,
                78f to 45f,
                16f to 22f,
                45f to 20f,
                73f to 22f
            )

            val campo = jugadoresCampo.mapIndexed { index, player ->
                player.number to RelativeOffset(posiciones[index].first, posiciones[index].second)
            }

            val porteroOffset = portero?.let {
                it.number to RelativeOffset(45f, 85f)
            }

            if (porteroOffset != null) listOf(porteroOffset) + campo else campo
        }

        "1-3-3-1" -> {
            val portero = teamPlayers.firstOrNull()
            val jugadoresCampo = teamPlayers.drop(1).take(7)

            val posiciones = listOf(
                20f to 68f,
                45f to 68f,
                68f to 68f,
                20f to 45f,
                45f to 45f,
                68f to 45f,
                45f to 24f
            )

            val campo = jugadoresCampo.mapIndexed { index, player ->
                player.number to RelativeOffset(posiciones[index].first, posiciones[index].second)
            }

            val porteroOffset = portero?.let {
                it.number to RelativeOffset(45f, 85f)
            }

            if (porteroOffset != null) listOf(porteroOffset) + campo else campo
        }

        else -> teamPlayers.mapIndexed { index, player ->
            player.number to RelativeOffset((10f + index * 10f), 50f)
        }
    }
}