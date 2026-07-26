package com.felipimatheuz.primehunt.ui.screen.splash.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R

@Composable
fun CephalonScene(
    state: CephalonAnimationState,
    modifier: Modifier = Modifier
) {

    val piecePositions = listOf(
        PiecePosition(Alignment.TopStart, -1, -1),
        PiecePosition(Alignment.TopEnd, 1, -1),
        PiecePosition(Alignment.BottomStart, -1, 1),
        PiecePosition(Alignment.BottomEnd, 1, 1)
    )

    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {

            CephalonBody(state)

            piecePositions.forEach {
                CephalonPiece(
                    modifier = Modifier.align(it.alignment),
                    xDirection = it.x,
                    yDirection = it.y,
                    state = state
                )
            }
        }
    }
}

@Composable
private fun CephalonBody(
    state: CephalonAnimationState
) {

    Box {

        Image(
            painter = painterResource(R.drawable.cephalon_body),
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer {
                    alpha = state.bodyAlpha
                    scaleX = state.bodyScale
                    scaleY = state.bodyScale
                }
        )
        Image(
            painter = painterResource(R.drawable.cephalon_body),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                state.bodyTint,
                BlendMode.Modulate
            ),
            modifier = Modifier.graphicsLayer {
                alpha = state.bodyAlpha
                scaleX = state.bodyScale
                scaleY = state.bodyScale
            }
        )
    }
}

@Composable
private fun CephalonPiece(
    modifier: Modifier,
    xDirection: Int,
    yDirection: Int,
    state: CephalonAnimationState
) {

    val offset = state.piecesOffset
    val piecePadding = 4.dp

    Image(
        painter = painterResource(R.drawable.cephalon_piece),
        contentDescription = null,

        colorFilter = ColorFilter.tint(
            state.piecesTint,
            BlendMode.Modulate
        ),

        modifier = modifier
            .offset(
                x = offset * xDirection + piecePadding * xDirection,
                y = offset * yDirection + piecePadding * yDirection
            )
            .graphicsLayer {
                alpha = state.piecesAlpha
                rotationZ = state.piecesRotation
                scaleX = state.piecesScale
                scaleY = state.piecesScale

            }
    )
}