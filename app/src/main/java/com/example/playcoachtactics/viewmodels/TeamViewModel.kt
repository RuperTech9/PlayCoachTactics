package com.example.playcoachtactics.viewmodels

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoachtactics.data.models.PlayerInfo
import com.example.playcoachtactics.data.models.RelativeOffset
import com.example.playcoachtactics.data.repositories.TeamRepository
import com.example.playcoachtactics.data.repositories.UserRepository
import com.example.playcoachtactics.ui.components.getPredefinedFormationRelativeOffsets
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val teamRepository: TeamRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var players by mutableStateOf<List<PlayerInfo>>(emptyList())
        private set

    var teamName by mutableStateOf("")
        private set

    fun loadTeamAndPlayers(onReady: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val uid = userRepository.auth.currentUser?.uid ?: return@launch
                val userSnap = userRepository.firestore.collection("users").document(uid).get().await()
                val teamId = userSnap.getString("teamId") ?: return@launch
                teamName = teamId

                players = teamRepository.getPlayersForTeam(teamId)
                onReady()
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun getInitialFormationRelativeOffsets(): List<Pair<Int, RelativeOffset>> {
        val isFutbol8 = teamName.lowercase() in listOf("alevin", "benjamin")
        val formationName = if (isFutbol8) "1-3-3-1" else "1-3-4-3"

        val portero = players.firstOrNull { it.position.equals("Portero", ignoreCase = true) }
        val restantes = players
            .filter { it.number != portero?.number }
            .sortedBy { it.number }

        val titulares = listOfNotNull(portero) + restantes.take(if (isFutbol8) 7 else 10)

        return getPredefinedFormationRelativeOffsets(formationName, titulares)
    }

    fun updatePlayerInfo(player: PlayerInfo, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                teamRepository.updatePlayer(teamName, player)
                players = players.map { if (it.id == player.id) player else it }
                onDone()
            } catch (_: Exception) {}
        }
    }

    fun deletePlayer(playerId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                teamRepository.deletePlayer(teamName, playerId)
                players = players.filterNot { it.id == playerId }
                onDone()
            } catch (_: Exception) {}
        }
    }

    fun addNewPlayer(player: PlayerInfo, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                teamRepository.addPlayer(teamName, player)
                // Necesitamos recargar para obtener el nuevo ID generado por Firestore
                loadTeamAndPlayers(onDone)
            } catch (_: Exception) {}
        }
    }
}
