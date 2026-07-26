package com.felipimatheuz.primehunt.ui.screen.splash.components

import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Stable
data class CephalonAnimationState(

    val bodyAlpha: Float,

    val bodyScale: Float,

    val bodyTint: Color,

    val piecesRotation: Float,

    val piecesAlpha: Float,

    val piecesTint: Color,

    val piecesOffset: Dp,

    val piecesScale: Float,
)

data class PiecePosition(

    val alignment: Alignment,

    val x: Int,

    val y: Int

)