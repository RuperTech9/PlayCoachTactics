package com.example.playcoachtactics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoachtactics.data.models.PlayerInfo
import com.example.playcoachtactics.ui.components.AddPlayerDialog
import com.example.playcoachtactics.ui.components.ConfirmDeleteDialog
import com.example.playcoachtactics.ui.components.EditPlayerDialog
import com.example.playcoachtactics.ui.components.PlayerCard
import com.example.playcoachtactics.ui.components.TopBar
import com.example.playcoachtactics.viewmodels.TeamViewModel

@Composable
fun SquadScreen(
    players: List<PlayerInfo>,
    viewModel: TeamViewModel,
    onBack: () -> Unit = {},
    onNavigateToBoard: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedPlayer by remember { mutableStateOf<PlayerInfo?>(null) }
    var newNickname by remember { mutableStateOf("") }
    var newNumber by remember { mutableStateOf("") }
    var newPosition by remember { mutableStateOf("Jugador") }
    var showError by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var addName by remember { mutableStateOf("") }
    var addLastName by remember { mutableStateOf("") }
    var addNickname by remember { mutableStateOf("") }
    var addNumber by remember { mutableStateOf("") }
    var addPosition by remember { mutableStateOf("Jugador") }

    val darkBlue = Color(0xFF00205A)

    Scaffold(
        topBar = {
            TopBar(
                title = "Plantilla",
                onBack = onBack,
                onNavigateToBoard = onNavigateToBoard,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF004AAD),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir jugador")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF00205B))
        ) {
            Text(
                text = "Jugadores del equipo",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp,
                color = Color.White
            )

            val sortedPlayers = players.sortedWith(
                compareBy<PlayerInfo> { it.position.lowercase() != "portero" }.thenBy { it.number }
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedPlayers) { player ->
                    PlayerCard(player = player) {
                        selectedPlayer = player
                        newNickname = player.nickname
                        newNumber = player.number.toString()
                        newPosition = player.position
                    }
                }
            }
        }
    }

    selectedPlayer?.let { player ->
        EditPlayerDialog(
            player = player,
            newNickname = newNickname,
            newNumber = newNumber,
            newPosition = newPosition,
            showError = showError,
            onNicknameChange = { newNickname = it },
            onNumberChange = { newNumber = it },
            onPositionChange = { newPosition = it },
            onDismiss = { selectedPlayer = null; showError = false },
            onDelete = { showDeleteConfirm = true },
            onSave = {
                if (players.any { it.number.toString() == newNumber && it.id != player.id }) {
                    showError = true
                } else {
                    val updated = player.copy(
                        nickname = newNickname,
                        number = newNumber.toIntOrNull() ?: player.number,
                        position = newPosition
                    )
                    viewModel.updatePlayerInfo(updated) {
                        selectedPlayer = null
                        showError = false
                    }
                }
            }
        )
    }

    if (showDeleteConfirm) {
        ConfirmDeleteDialog(
            onConfirm = {
                selectedPlayer?.let {
                    viewModel.deletePlayer(it.id) {
                        showDeleteConfirm = false
                        selectedPlayer = null
                    }
                }
            },
            onDismiss = { showDeleteConfirm = false },
            darkBlue = darkBlue
        )
    }

    if (showAddDialog) {
        AddPlayerDialog(
            addName = addName,
            addLastName = addLastName, // <--- NUEVO CAMPO
            addNickname = addNickname,
            addNumber = addNumber,
            addPosition = addPosition,
            showError = showError,
            onNameChange = { addName = it },
            onLastNameChange = { addLastName = it }, // <--- NUEVA CALLBACK
            onNicknameChange = { addNickname = it },
            onNumberChange = { addNumber = it },
            onPositionChange = { addPosition = it },
            onDismiss = { showAddDialog = false; showError = false },
            onConfirm = {
                if (players.any { it.number.toString() == addNumber }) {
                    showError = true
                } else {
                    val newPlayer = PlayerInfo(
                        firstName = addName,
                        lastName = addLastName,
                        nickname = addNickname,
                        number = addNumber.toIntOrNull() ?: 0,
                        position = addPosition,
                        imageResName = "ic_jugador"
                    )
                    viewModel.addNewPlayer(newPlayer) {
                        showAddDialog = false
                        showError = false
                    }
                }
            }
        )
    }

}
