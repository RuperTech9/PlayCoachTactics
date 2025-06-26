package com.example.playcoachtactics.ui.components

import com.example.playcoachtactics.data.models.PlayerInfo
import com.example.playcoachtactics.data.models.RelativeOffset

fun getPredefinedFormationRelativeOffsets(
    name: String,
    teamPlayers: List<PlayerInfo>
): List<Pair<Int, RelativeOffset>> {
    return when (name) {
        "1-4-3-3" -> {
            val portero = teamPlayers.firstOrNull()
            val jugadoresCampo = teamPlayers.drop(1).take(10)

            val posiciones = listOf(
                16f to 22f, // DFI
                16f to 70f, // DFD
                23f to 10f, // LI
                23f to 80f, // LD
                34f to 24f, // MI
                34f to 66f, // MD
                42f to 45f, // MCO
                57f to 20f, // EI
                60f to 45f, // DC
                57f to 70f  // ED
            )

            val campo = jugadoresCampo.mapIndexed { index, player ->
                player.number to RelativeOffset(posiciones[index].first, posiciones[index].second)
            }

            val porteroOffset = portero?.let {
                it.number to RelativeOffset(10f, 45f)
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