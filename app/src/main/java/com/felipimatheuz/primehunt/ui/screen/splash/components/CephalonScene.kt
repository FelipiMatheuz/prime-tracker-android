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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.White

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
            contentDescription = stringResource(R.string.splash_cephalon_ehiza_description),
            modifier = Modifier
                .graphicsLayer {
                    alpha = state.bodyAlpha
                    scaleX = state.bodyScale
                    scaleY = state.bodyScale
                }
        )
        Image(
            painter = painterResource(R.drawable.cephalon_body),
            contentDescription = stringResource(R.string.splash_cephalon_ehiza_description),
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
    val piecePaddingX = 16.dp
    val piecePaddingY = 12.dp

    Image(
        painter = painterResource(R.drawable.cephalon_piece),
        contentDescription = stringResource(R.string.splash_cephalon_ehiza_description),

        colorFilter = ColorFilter.tint(
            state.piecesTint,
            BlendMode.Modulate
        ),

        modifier = modifier
            .offset(
                x = offset * xDirection + piecePaddingX * xDirection,
                y = offset * yDirection + piecePaddingY * yDirection
            )
            .graphicsLayer {
                alpha = state.piecesAlpha
                rotationZ = state.piecesRotation
                scaleX = state.piecesScale
                scaleY = state.piecesScale

            }
    )
}

@Preview(widthDp = 300, heightDp = 300)
@Composable
fun CephalonScenePreview() {
    CephalonScene(
        state = CephalonAnimationState(
            bodyAlpha = 1f,
            bodyScale = 1f,
            bodyTint = White,
            piecesRotation = 0f,
            piecesAlpha = 1f,
            piecesTint = White,
            piecesOffset = 0.dp,
            piecesScale = 1f
        )
    )
}