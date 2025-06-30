package com.example.playcoachtactics.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.playcoachtactics.data.models.PlayerInfo
import com.example.playcoachtactics.data.models.RelativeOffset
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    // Convert widthDp and heightDp to px for calculations
    val widthPx = with(density) { widthDp.toPx() }
    val heightPx = with(density) { heightDp.toPx() }

    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }

    // Snap to initial position at first composition and when relOffset changes
    LaunchedEffect(widthPx, heightPx, relOffset) {
        animX.snapTo((relOffset.xPercent / 100f) * widthPx)
        animY.snapTo((relOffset.yPercent / 100f) * heightPx)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset { IntOffset(animX.value.toInt(), animY.value.toInt()) }
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .pointerInput(number) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newX = (animX.value + dragAmount.x).coerceIn(0f, widthPx - with(density) { playerSize.toPx() })
                    val newY = (animY.value + dragAmount.y).coerceIn(0f, heightPx - with(density) { playerSize.toPx() })

                    scope.launch {
                        animX.snapTo(newX)
                        animY.snapTo(newY)
                    }

                    val newRelativeOffset = RelativeOffset(
                        xPercent = newX / widthPx * 100f,
                        yPercent = newY / heightPx * 100f
                    )

                    onOffsetChange(newRelativeOffset)
                }
            }
            .clickable { onClick() }
    ) {
        PlayerCard(player = player, size = playerSize)
    }
}
