package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        val cardWidthPx = with(density) { OrbitalDimens.CardSize.width.toPx() }
        val cardHeightPx = with(density) { OrbitalDimens.CardSize.height.toPx() }

        Box(Modifier.fillMaxSize()) {
            pages.forEachIndexed { i, page ->
                OrbitalCard(
                    index = i,
                    totalCards = pages.size,
                    state = state,
                    radiusX = radiusX,
                    radiusY = radiusY,
                    centerX = centerX,
                    centerY = centerY,
                    cardWidthPx = cardWidthPx,
                    cardHeightPx = cardHeightPx,
                    fadeInAlpha = { cardFadeInAlphas[i].value },
                    onClick = { state.focusCard(i) },
                    page = page
                )
            }
        }
    }
}
