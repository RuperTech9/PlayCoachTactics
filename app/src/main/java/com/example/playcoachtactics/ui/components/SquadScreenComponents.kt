package com.example.playcoachtactics.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoachtactics.R
import com.example.playcoachtactics.data.models.PlayerInfo

@Composable
fun PlayerCard(player: PlayerInfo, onClick: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .clickable { onClick() }
    ) {
        val imageOffsetY = maxHeight * 0.103f
        val imageSize = maxHeight * 0.55f

        val cardRes = if (player.position.lowercase() == "portero") {
            R.drawable.card_gk
        } else {
            R.drawable.card_player
        }

        Image(
            painter = painterResource(id = cardRes),
            contentDescription = "Fondo carta",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFCF6D7))
                    .padding(1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF512A00))
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFCF6D7))
                            .padding(1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF00205A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player.number.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        PlayerImage(
            imageResId = player.imageResId,
            modifier = Modifier
                .size(imageSize)
                .offset(y = imageOffsetY)
                .align(Alignment.TopCenter)
        )


        Text(
            text = player.nickname.ifBlank { player.firstName },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004AAD),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = maxHeight * 0.25f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlayerDialog(
    player: PlayerInfo,
    newNickname: String,
    newNumber: String,
    newPosition: String,
    showError: Boolean,
    onNicknameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onPositionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit
) {
    val darkBlue = Color(0xFF00205A)
    var editPositionExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = darkBlue
                ),
                border = BorderStroke(1.dp, darkBlue)
            ) {
                Text("Cancelar")
            }
        },
        title = {
            Text("Editar jugador", color = darkBlue, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                OutlinedTextField(
                    value = newNickname,
                    onValueChange = onNicknameChange,
                    label = { Text("Apodo", color = darkBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = darkBlue,
                        unfocusedBorderColor = darkBlue,
                        focusedLabelColor = darkBlue,
                        unfocusedLabelColor = darkBlue
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newNumber,
                    onValueChange = { onNumberChange(it.filter { c -> c.isDigit() }) },
                    label = { Text("Dorsal", color = darkBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = darkBlue,
                        unfocusedBorderColor = darkBlue,
                        focusedLabelColor = darkBlue,
                        unfocusedLabelColor = darkBlue
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = editPositionExpanded,
                    onExpandedChange = { editPositionExpanded = !editPositionExpanded }
                ) {
                    OutlinedTextField(
                        value = newPosition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Posición", color = darkBlue) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = editPositionExpanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = darkBlue,
                            unfocusedBorderColor = darkBlue,
                            focusedLabelColor = darkBlue,
                            unfocusedLabelColor = darkBlue
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = editPositionExpanded,
                        onDismissRequest = { editPositionExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Jugador") },
                            onClick = {
                                onPositionChange("Jugador")
                                editPositionExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Portero") },
                            onClick = {
                                onPositionChange("Portero")
                                editPositionExpanded = false
                            }
                        )
                    }
                }

                if (showError) {
                    Text(
                        "Ese dorsal ya está en uso",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Eliminar jugador")
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit, darkBlue: Color) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Sí, eliminar", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = darkBlue
                ),
                border = BorderStroke(1.dp, darkBlue)
            ) {
                Text("Cancelar")
            }
        },
        title = {
            Text("Confirmar eliminación", color = darkBlue, fontWeight = FontWeight.Bold)
        },
        text = {
            Text("¿Estás seguro de que deseas eliminar a este jugador? Esta acción no se puede deshacer.")
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerDialog(
    addName: String,
    addLastName: String,
    addNickname: String,
    addNumber: String,
    addPosition: String,
    showError: Boolean,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onNicknameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onPositionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val darkBlue = Color(0xFF00205A)
    var addPositionExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss(); },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
            ) {
                Text("Añadir", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = darkBlue
                ),
                border = BorderStroke(1.dp, darkBlue)
            ) {
                Text("Cancelar")
            }
        }
        ,
        title = {
            Text("Nuevo jugador", color = darkBlue, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                OutlinedTextField(
                    value = addName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = darkBlue,
                        unfocusedBorderColor = darkBlue,
                        focusedLabelColor = darkBlue,
                        unfocusedLabelColor = darkBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = addLastName,
                    onValueChange = onLastNameChange,
                    label = { Text("Apellidos", color = darkBlue) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = darkBlue,
                        unfocusedBorderColor = darkBlue,
                        focusedLabelColor = darkBlue,
                        unfocusedLabelColor = darkBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = addNickname,
                    onValueChange = onNicknameChange,
                    label = { Text("Apodo", color = darkBlue) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = darkBlue,
                        unfocusedBorderColor = darkBlue,
                        focusedLabelColor = darkBlue,
                        unfocusedLabelColor = darkBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = addNumber,
                    onValueChange = { onNumberChange(it.filter { c -> c.isDigit() }) },
                    label = { Text("Dorsal", color = darkBlue) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = darkBlue,
                        unfocusedBorderColor = darkBlue,
                        focusedLabelColor = darkBlue,
                        unfocusedLabelColor = darkBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = addPositionExpanded,
                    onExpandedChange = { addPositionExpanded = !addPositionExpanded }
                ) {
                    OutlinedTextField(
                        value = addPosition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Posición", color = darkBlue) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = addPositionExpanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = darkBlue,
                            unfocusedBorderColor = darkBlue,
                            focusedLabelColor = darkBlue,
                            unfocusedLabelColor = darkBlue
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = addPositionExpanded,
                        onDismissRequest = { addPositionExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Jugador") },
                            onClick = {
                                onPositionChange("Jugador")
                                addPositionExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Portero") },
                            onClick = {
                                onPositionChange("Portero")
                                addPositionExpanded = false
                            }
                        )
                    }
                }

                if (showError) {
                    Text(
                        "Ese dorsal ya está en uso",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
