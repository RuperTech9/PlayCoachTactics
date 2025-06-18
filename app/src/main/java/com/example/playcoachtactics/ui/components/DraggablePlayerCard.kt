package com.example.playcoachtactics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.playcoachtactics.data.models.PlayerInfo
import com.example.playcoachtactics.data.models.RelativeOffset
import com.example.playcoachtactics.ui.components.PlayerCard

@Composable
fun DraggablePlayerCard(
    player: PlayerInfo,
    number: Int,
    relOffset: RelativeOffset,
    widthDp: Dp,
    heightDp: Dp,
    playerSize: Dp,
    isSelected: Boolean,
    onOffsetChange: (RelativeOffset) -> Unit,
    onClick: () -> Unit
) {
    val density = LocalDensity.current

    var offset by remember(widthDp, heightDp, relOffset) {
        mutableStateOf(
            DpOffset(
                x = (relOffset.xPercent / 100f) * widthDp,
                y = (relOffset.yPercent / 100f) * heightDp
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(offset.x, offset.y)
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .pointerInput(number) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val dxDp = with(density) { dragAmount.x.toDp() }
                    val dyDp = with(density) { dragAmount.y.toDp() }

                    val newX = (offset.x + dxDp).coerceIn(0.dp, widthDp - playerSize)
                    val newY = (offset.y + dyDp).coerceIn(0.dp, heightDp - playerSize)

                    offset = DpOffset(newX, newY)

                    val newRelativeOffset = RelativeOffset(
                        xPercent = (newX / widthDp) * 100f,
                        yPercent = (newY / heightDp) * 100f
                    )

                    onOffsetChange(newRelativeOffset)
                }
            }
            .clickable { onClick() }
    ) {
        PlayerCard(player = player, size = playerSize)
    }
}
