package com.example.playcoachtactics.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoachtactics.R

@Composable
fun TopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onNavigateToSquad: (() -> Unit)? = null,
    onNavigateToBoard: (() -> Unit)? = null,
    onNavigateToProfile: (() -> Unit)? = null
) {
    val height = 64.dp
    val iconSize = 28.dp
    val darkBlue = Color(0xFF00205B)
    val navyBackground = Color(0xFF001F3F)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        color = navyBackground,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                onBack?.let {
                    IconButton(onClick = it) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.escudo_sln),
                    contentDescription = "Logo SLN",
                    modifier = Modifier
                        .padding(start = if (onBack != null) 0.dp else 8.dp)
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconBackground = Modifier
                    .size(iconSize + 10.dp)
                    .clip(CircleShape)
                    .background(darkBlue)
                    .padding(6.dp)

                onNavigateToBoard?.let {
                    IconButton(onClick = it) {
                        Box(modifier = iconBackground) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = "Formación",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                onNavigateToSquad?.let {
                    IconButton(onClick = it) {
                        Box(modifier = iconBackground) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Plantilla",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                onNavigateToProfile?.let {
                    IconButton(onClick = it) {
                        Box(modifier = iconBackground) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
