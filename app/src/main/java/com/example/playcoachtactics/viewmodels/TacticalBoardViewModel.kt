package com.example.playcoachtactics.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoachtactics.data.models.PlayerPosition
import com.example.playcoachtactics.data.models.RelativeOffset
import com.example.playcoachtactics.data.models.SavedFormation
import com.example.playcoachtactics.data.models.UiState
import com.example.playcoachtactics.data.repositories.FormationRepository
import com.example.playcoachtactics.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TacticalBoardViewModel @Inject constructor(
    private val formationRepository: FormationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var playerPositions = mutableStateListOf<Pair<Int, RelativeOffset>>()
        private set

    var targetPositions = mutableStateMapOf<Int, RelativeOffset>()
        private set

    var isRecording = mutableStateOf(false)
        private set

    private val recordedFrames = mutableListOf<List<Pair<Int, RelativeOffset>>>()

    var formationState = mutableStateOf<UiState<List<Triple<String, List<Pair<Int, RelativeOffset>>, String>>>>(UiState.Loading)
        private set

    private var selectedPlayerId: Int? = null

    fun updatePositions(newPositions: List<Pair<Int, RelativeOffset>>) {
        playerPositions.clear()
        playerPositions.addAll(newPositions)
    }

    fun isSelected(number: Int): Boolean = selectedPlayerId == number

    fun onPlayerSelected(number: Int) {
        if (selectedPlayerId == null) {
            selectedPlayerId = number
        } else {
            if (selectedPlayerId != number) {
                swapPlayers(selectedPlayerId!!, number)
            }
            selectedPlayerId = null
        }
    }

    fun updatePlayerOffset(number: Int, newOffset: RelativeOffset) {
        val index = playerPositions.indexOfFirst { it.first == number }
        if (index != -1) {
            playerPositions[index] = number to newOffset

            // Registrar frame si estamos grabando
            if (isRecording.value) {
                recordedFrames.add(playerPositions.map { it.copy() })
            }
        }
    }

    private fun swapPlayers(playerA: Int, playerB: Int) {
        val indexA = playerPositions.indexOfFirst { it.first == playerA }
        val indexB = playerPositions.indexOfFirst { it.first == playerB }

        if (indexA == -1 && indexB != -1) {
            val offsetB = playerPositions[indexB].second
            playerPositions[indexB] = playerA to offsetB
        } else if (indexB == -1 && indexA != -1) {
            val offsetA = playerPositions[indexA].second
            playerPositions[indexA] = playerB to offsetA
        } else if (indexA != -1 && indexB != -1) {
            val offsetA = playerPositions[indexA].second
            val offsetB = playerPositions[indexB].second
            playerPositions[indexA] = playerB to offsetA
            playerPositions[indexB] = playerA to offsetB
        }
    }

    fun saveFormationToFirestore(formationName: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            val teamId = userRepository.getCurrentUserTeamId() ?: return@launch

            val formation = SavedFormation(
                name = formationName,
                positions = playerPositions.map { (number, offset) ->
                    PlayerPosition(number, offset)
                }
            )

            formationRepository.saveFormationAsModel(formation, uid, teamId)
            loadFormations()
        }
    }

    fun loadFormations() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            formationState.value = UiState.Loading
            try {
                val teamId = userRepository.getCurrentUserTeamId() ?: return@launch
                val loaded = formationRepository.loadFormations(uid, teamId)
                formationState.value = UiState.Success(loaded)
            } catch (e: Exception) {
                formationState.value = UiState.Error(e.message ?: "Error al cargar formaciones")
            }
        }
    }

    fun deleteFormation(documentId: String) {
        viewModelScope.launch {
            formationRepository.deleteFormation(documentId)
            val current = (formationState.value as? UiState.Success)?.data ?: return@launch
            formationState.value = UiState.Success(current.filterNot { it.third == documentId })
        }
    }

    fun setDemoTargetPositions() {
        targetPositions.clear()
        playerPositions.forEach { (number, current) ->
            targetPositions[number] = RelativeOffset(
                xPercent = (current.xPercent + 10f).coerceAtMost(100f),
                yPercent = (current.yPercent + 5f).coerceAtMost(100f)
            )
        }
    }

    fun startRecording() {
        isRecording.value = true
        recordedFrames.clear()
        recordedFrames.add(playerPositions.map { it.copy() }) // primer frame
    }

    fun stopRecording() {
        isRecording.value = false
    }

    fun playRecordedAnimation() {
        viewModelScope.launch {
            val delayPerFrame = 33L
            for (frame in recordedFrames) {
                updatePositions(frame)
                kotlinx.coroutines.delay(delayPerFrame)
            }
        }
    }

    fun clearSelection() {
        selectedPlayerId = null
    }
}
