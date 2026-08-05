package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun OrbitalCard(
    index: Int,
    totalCards: Int,
    state: OrbitalState,
    radiusX: Float,
    radiusY: Float,
    centerX: Float,
    centerY: Float,
    cardWidthPx: Float,
    cardHeightPx: Float,
    fadeInAlpha: () -> Float,
    onClick: () -> Unit,
    page: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val isFocused = state.focusedIndex == index
    val focusProgress by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(600),
        label = "FocusProgress"
    )

    Box(
        modifier = modifier
            .size(OrbitalDimens.CardSize)
            .graphicsLayer {
                val baseAngle = (state.angle.value + (360f / totalCards) * index) % 360f
                val rad = Math.toRadians(baseAngle.toDouble()).toFloat()

                val depthFactor = kotlin.math.cos(rad - Math.toRadians(90.0).toFloat())

                val orbitScale = lerp(0.25f, 0.75f, (depthFactor + 1f) / 2f)
                val orbitAlpha = lerp(0.2f, 1.0f, (depthFactor + 1f) / 2f) * fadeInAlpha()

                val orbitX = centerX + radiusX * kotlin.math.cos(rad)
                val orbitY = centerY + radiusY * kotlin.math.sin(rad)

                val focusScale = 1.2f
                val focusAlpha = 1.0f

                // Final interpolated values
                val finalX = lerp(orbitX, centerX, focusProgress)
                val finalY = lerp(orbitY, centerY, focusProgress)
                val finalScale = lerp(orbitScale, focusScale, focusProgress)
                val finalAlpha = lerp(orbitAlpha, focusAlpha, focusProgress)

                translationX = finalX - cardWidthPx / 2f
                translationY = finalY - cardHeightPx / 2f
                scaleX = finalScale
                scaleY = finalScale
                this.alpha = finalAlpha
                // Z-index sorting using graphicsLayer's shadow/elevation logic is limited, 
                // but we can use the zIndex modifier on the Box itself if needed.
                // However, graphicsLayer also has a 'shadowElevation' but that's for shadows.
            }
            .zIndex(if (isFocused) 10f else {
                // We still need a way to calculate depthFactor for zIndex without recomposing too much.
                // Unfortunately, zIndex modifier itself is NOT a lambda-based one.
                // But we can approximate it or keep it as is if it's the only thing recomposing.
                val rad = Math.toRadians(((state.angle.value + (360f / totalCards) * index) % 360f).toDouble()).toFloat()
                kotlin.math.cos(rad - Math.toRadians(90.0).toFloat())
            })
    ) {

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clickable(onClick = onClick)
        ) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                content = page
            )
        }

        Image(
            painter = painterResource(id = R.drawable.void_border),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@Preview
@Composable
fun OrbitalCardPreview() {
    PrimeTrackerTheme {
        OrbitalCard(
            index = 0,
            totalCards = 5,
            state = rememberOrbitalState(),
            radiusX = 100f,
            radiusY = 100f,
            centerX = 0f,
            centerY = 0f,
            cardWidthPx = 200f,
            cardHeightPx = 300f,
            fadeInAlpha = { 1f },
            onClick = {},
            page = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OrbitalCardDarkPreview() {
    PrimeTrackerTheme {
        OrbitalCard(
            index = 0,
            totalCards = 5,
            state = rememberOrbitalState(),
            radiusX = 100f,
            radiusY = 100f,
            centerX = 0f,
            centerY = 0f,
            cardWidthPx = 200f,
            cardHeightPx = 300f,
            fadeInAlpha = { 1f },
            onClick = {},
            page = {}
        )
    }
}
