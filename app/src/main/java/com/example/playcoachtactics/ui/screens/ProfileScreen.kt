package com.example.playcoachtactics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoachtactics.ui.components.TopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit = {},
    onNavigateToBoard: () -> Unit = {},
    onNavigateToSquad: () -> Unit = {}
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        uid?.let {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(it).get()
                .addOnSuccessListener { userDoc ->
                    firstname = userDoc.getString("firstname") ?: ""
                    lastname = userDoc.getString("lastname") ?: ""
                    val teamId = userDoc.getString("teamId") ?: ""

                    // Ahora obtenemos el nombre del equipo real desde la colección teams
                    db.collection("teams").document(teamId).get()
                        .addOnSuccessListener { teamDoc ->
                            teamName = teamDoc.getString("name") ?: teamId
                            isLoading = false
                        }
                        .addOnFailureListener {
                            teamName = teamId // fallback
                            isLoading = false
                        }
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Mi perfil",
                onBack = onBack,
                onNavigateToBoard = onNavigateToBoard,
                onNavigateToSquad = onNavigateToSquad
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00205B))
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Text("Nombre completo:", fontSize = 18.sp, color = Color.LightGray)
                Text("$firstname $lastname", fontSize = 22.sp, color = Color.White)

                Spacer(Modifier.height(8.dp))

                Text("Equipo que entrena:", fontSize = 18.sp, color = Color.LightGray)
                Text(teamName, fontSize = 22.sp, color = Color.White)

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            }
        }
    }
}
