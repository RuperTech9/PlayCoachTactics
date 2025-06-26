package com.example.playcoachtactics.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.playcoachtactics.R
import com.example.playcoachtactics.data.models.PlayerInfo
import com.example.playcoachtactics.data.models.UiState
import com.example.playcoachtactics.ui.components.DraggablePlayerCard
import com.example.playcoachtactics.ui.components.PlayerCard
import com.example.playcoachtactics.ui.components.TopBar
import com.example.playcoachtactics.viewmodels.TacticalBoardViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UseOfNonLambdaOffsetOverload")
@Composable
fun TacticalBoardSimple(
    teamPlayers: List<PlayerInfo>,
    onBack: () -> Unit = {},
    onNavigateToSquad: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: TacticalBoardViewModel
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Formación",
                onBack = onBack,
                onNavigateToSquad = onNavigateToSquad,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        containerColor = Color(0xFF00205B)
    ) { innerPadding ->

        val formationState = viewModel.formationState.value
        val starters by remember { derivedStateOf { viewModel.playerPositions.map { it.first } } }
        val substitutes by remember(teamPlayers, starters) {
            derivedStateOf { teamPlayers.filter { it.number !in starters } }
        }

        var showSaveDialog by remember { mutableStateOf(false) }
        var showLoadDialog by remember { mutableStateOf(false) }
        var editableFormationName by remember { mutableStateOf("") }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            editableFormationName = ""
                            showSaveDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004AAD))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar", color = Color.White)
                    }

                    Button(
                        onClick = { showLoadDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004AAD))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Cargar")
                        Spacer(Modifier.width(8.dp))
                        Text("Cargar", color = Color.White)
                    }
                }
            }

            item {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color(0xFF699BB2))
                ) {
                    val fieldSize = remember { mutableStateOf(IntSize.Zero) }
                    val playerSize = maxWidth * 0.06f
                    val ballSize = playerSize * 0.25f

                    val density = LocalDensity.current
                    val widthDp = with(density) { fieldSize.value.width.toDp() }
                    val heightDp = with(density) { fieldSize.value.height.toDp() }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { layout ->
                                fieldSize.value = layout.size
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_campofutbol),
                            contentDescription = "Campo de fútbol",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.matchParentSize()
                        )

                        viewModel.playerPositions.forEach { (number, relOffset) ->
                            val player = teamPlayers.find { it.number == number } ?: return@forEach
                            val isSelected = viewModel.isSelected(number)

                            DraggablePlayerCard(
                                player = player,
                                number = number,
                                relOffset = relOffset,
                                widthDp = widthDp,
                                heightDp = heightDp,
                                playerSize = playerSize,
                                isSelected = isSelected,
                                onOffsetChange = { newOffset ->
                                    viewModel.updatePlayerOffset(number, newOffset)
                                },
                                onClick = { viewModel.onPlayerSelected(number) }
                            )
                        }


                        var ballOffset by remember(widthDp, heightDp) {
                            mutableStateOf(
                                DpOffset(
                                    widthDp / 2 - ballSize / 2,
                                    heightDp / 2 - ballSize / 2
                                )
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.ic_ball),
                            contentDescription = "Balón",
                            modifier = Modifier
                                .size(ballSize)
                                .offset(ballOffset.x, ballOffset.y)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        val dxDp = with(density) { dragAmount.x.toDp() }
                                        val dyDp = with(density) { dragAmount.y.toDp() }

                                        val newX = (ballOffset.x + dxDp).coerceIn(0.dp, widthDp - ballSize)
                                        val newY = (ballOffset.y + dyDp).coerceIn(0.dp, heightDp - ballSize)

                                        ballOffset = DpOffset(newX, newY)
                                    }
                                }
                        )
                    }
                }
            }
            item {
                BoxWithConstraints {
                    val playerSize = maxWidth * 0.058f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF00205B))
                            .horizontalScroll(rememberScrollState())
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        substitutes.forEach { player ->
                            val isSelected = viewModel.isSelected(player.number)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .background(if (isSelected) Color.LightGray else Color.Transparent)
                                    .clickable { viewModel.onPlayerSelected(player.number) }
                            ) {
                                PlayerCard(player = player, size = playerSize)
                            }
                        }
                    }
                }
            }

        }

        if (showSaveDialog) {
            val isValidName = editableFormationName.trim().isNotEmpty()

            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.saveFormationToFirestore(editableFormationName.trim())
                            showSaveDialog = false
                        },
                        enabled = isValidName,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004AAD))
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showSaveDialog = false },
                        border = BorderStroke(1.dp, Color(0xFF00205B)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF00205B)
                        )
                    ) {
                        Text("Cancelar")
                    }
                },
                title = {
                    Text(
                        "Guardar formación",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF00205B),
                        fontWeight = FontWeight.Bold)
                },

                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editableFormationName,
                            onValueChange = { editableFormationName = it },
                            label = { Text("Nombre de la formación") },
                            singleLine = true,
                            isError = !isValidName,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF004AAD),
                                unfocusedBorderColor = Color(0xFFAAC4E0),
                                cursorColor = Color(0xFF004AAD)
                            )
                        )
                        if (!isValidName) {
                            Text(
                                "El nombre no puede estar vacío.",
                                color = Color.Red,
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                containerColor = Color(0xFFE8F0FF), // mismo fondo azul celeste
                tonalElevation = 6.dp
            )
        }

        if (showLoadDialog) {
            LaunchedEffect(Unit) { viewModel.loadFormations() }

            AlertDialog(
                onDismissRequest = { showLoadDialog = false },
                confirmButton = {},
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLoadDialog = false },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF00205B)
                        )
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                },
                title = {
                    Text(
                        "Selecciona una formación",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF00205B),
                        fontWeight = FontWeight.Bold)
                },
                text = {
                    when (formationState) {
                        is UiState.Loading -> Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF004AAD))
                        }

                        is UiState.Error -> Text(
                            text = "Error: ${formationState.message}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )

                        is UiState.Success -> {
                            val savedFormations = formationState.data.sortedBy { it.first.lowercase() }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                savedFormations.forEach { (name, positions, documentId) ->
                                    Surface(
                                        color = Color(0xFFD6E6FF), // fondo celeste más oscuro
                                        shape = MaterialTheme.shapes.medium,
                                        shadowElevation = 1.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    viewModel.updatePositions(positions)
                                                    showLoadDialog = false
                                                },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = Color(0xFF00205B)
                                                )
                                            ) {
                                                Text(name, fontSize = 16.sp)
                                            }

                                            IconButton(onClick = {
                                                viewModel.deleteFormation(documentId)
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Eliminar formación",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = Color(0xFFE8F0FF), // fondo general azul celeste
                tonalElevation = 6.dp
            )
        }

    }
}