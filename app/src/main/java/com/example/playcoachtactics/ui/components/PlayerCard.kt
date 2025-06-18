package com.example.playcoachtactics.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playcoachtactics.R
import com.example.playcoachtactics.data.models.PlayerInfo

@Composable
fun PlayerCard(
    player: PlayerInfo,
    size: Dp = 100.dp
) {
    val backgroundRes = if (player.position.lowercase() == "portero") {
        R.drawable.card_gk
    } else {
        R.drawable.card_player
    }

    val backgroundPainter = painterResource(id = backgroundRes)

    val numberFontSize = remember(size) { (size.value * 0.12f).sp }
    val nameFontSize = remember(size) { (size.value * 0.15f).sp }
    val imageSize = remember(size) { size * 0.8f }
    val imageOffset = remember(size) { size * 0.16f }
    val nameBottomPadding = remember(size) { size * 0.34f }

    Box(
        modifier = Modifier
            .size(size)
            .aspectRatio(0.68f)
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = "Fondo carta",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = player.number.toString(),
            color = Color.White,
            fontSize = numberFontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 0.dp, top = 1.dp)
                .border(1.dp, Color(0xFFFCF6D7), CircleShape)
                .background(Color(0xFF512A00), CircleShape)
                .clip(CircleShape)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )

        PlayerImage(
            imageResId = player.imageResId,
            modifier = Modifier
                .size(imageSize)
                .align(Alignment.TopCenter)
                .offset(y = imageOffset)
        )


        Text(
            text = player.nickname.ifBlank { player.firstName },
            color = Color(0xFF004AAD),
            fontSize = nameFontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = nameBottomPadding)
        )
    }
}
