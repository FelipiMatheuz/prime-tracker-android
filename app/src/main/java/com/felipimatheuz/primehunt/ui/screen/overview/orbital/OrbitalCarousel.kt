package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun OrbitalCarousel(
    state: OrbitalState,
    modifier: Modifier = Modifier,
    pages: List<@Composable BoxScope.() -> Unit>
) {
    val density = LocalDensity.current
    val cardFadeInDurationMs = 600L

    val cardFadeInAlphas = remember { List(pages.size) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        cardFadeInAlphas.forEachIndexed { index, animatable ->
            launch {
                delay(((index + 1) * cardFadeInDurationMs).milliseconds)
                animatable.animateTo(1f, tween(cardFadeInDurationMs.toInt()))
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .orbitalGestureHandler(state),
        contentAlignment = Alignment.Center
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        val isLandScape = widthPx > heightPx

        val toolbarHeightPx = if(isLandScape) with(density) { 56.dp.toPx() } else 0f

        val centerX = widthPx / 2f
        val centerY = heightPx / 2f + toolbarHeightPx

        val radiusY: Float
        val radiusX: Float

        val cardHeightHalfPx = with(density) { (OrbitalDimens.CardSize.height / 2f).toPx() }
        if (isLandScape) {
            radiusY = heightPx - cardHeightHalfPx
            radiusX = radiusY * (9f / 5f)
        } else {
            radiusY = heightPx/2f - cardHeightHalfPx
            radiusX = radiusY * 1.8f
        }

        val cardItems = pages.mapIndexed { i, page ->
            val isFocused = state.focusedIndex == i
            val focusProgress by animateFloatAsState(
                targetValue = if (isFocused) 1f else 0f,
                animationSpec = tween(600),
                label = "FocusProgress"
            )

            val baseAngle = (state.angle.value + (360f / 5f) * i) % 360f
            val rad = Math.toRadians(baseAngle.toDouble()).toFloat()

            val depthFactor = cos(rad - Math.toRadians(90.0).toFloat())

            val orbitScale = lerp(0.25f, 0.75f, (depthFactor + 1f) / 2f)
            val orbitAlpha = lerp(0.2f, 1.0f, (depthFactor + 1f) / 2f) * cardFadeInAlphas[i].value

            val orbitX = centerX + radiusX * cos(rad)
            val orbitY = centerY + radiusY * sin(rad)

            val focusScale = 1.2f
            val focusAlpha = 1.0f
            val focusZIndex = 10f

            // Final interpolated values
            val finalX = lerp(orbitX, centerX, focusProgress)
            val finalY = lerp(orbitY, centerY, focusProgress)
            val finalScale = lerp(orbitScale, focusScale, focusProgress)
            val finalAlpha = lerp(orbitAlpha, focusAlpha, focusProgress)
            val finalZIndex = if (isFocused) focusZIndex else depthFactor

            CardPositionInfo(
                page = page,
                index = i,
                x = finalX,
                y = finalY,
                scale = finalScale,
                alpha = finalAlpha,
                zIndex = finalZIndex
            )
        }

        val sortedCards = cardItems.sortedBy { it.zIndex }

        val cardWidthPx = with(density) { OrbitalDimens.CardSize.width.toPx() }
        val cardHeightPx = with(density) { OrbitalDimens.CardSize.height.toPx() }

        Box(Modifier.fillMaxSize()) {
            sortedCards.forEach { item ->
                OrbitalCard(
                    scale = item.scale,
                    alpha = item.alpha,
                    zIndex = item.zIndex,
                    onClick = { state.focusCard(item.index) },
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = item.x - cardWidthPx / 2f
                            translationY = item.y - cardHeightPx / 2f
                        },
                    page = item.page
                )
            }
        }
    }
}

private data class CardPositionInfo(
    val page: @Composable BoxScope.() -> Unit,
    val index: Int,
    val x: Float,
    val y: Float,
    val scale: Float,
    val alpha: Float,
    val zIndex: Float
)
