package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated background for the Overview Screen (Void Theme).
 * Reacts to focus state and rotation speed from OrbitalState.
 */
@Composable
fun OverviewBackground(
    state: OrbitalState,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val focusAlpha by animateFloatAsState(
        targetValue = if (state.focusedIndex != null) 0.3f else 1f, // valor assumido, ajustar
        animationSpec = tween(400),
        label = "FocusFade"
    )

    val baseColor = if (darkTheme) VoidBaseDark else VoidBaseLight
    val gradientCenter = if (darkTheme) VoidGradientCenterDark else VoidGradientCenterLight

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
            .graphicsLayer { alpha = focusAlpha }
    ) {
        // Layer 2 — Radial Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val brush = Brush.radialGradient(
                        colors = listOf(gradientCenter, baseColor),
                        center = Offset.Unspecified,
                        radius = size.minDimension * 0.75f // proporcional à tela, ajustável
                    )
                    onDrawBehind { drawRect(brush) }
                }
        )
        
        // Layer 3: Void Energy
        VoidEnergyLayer(darkTheme)

        // Layer 4: Center Glow
        CenterGlowLayer(state, darkTheme)

        // Layer 5: Particles
        ParticleLayer(darkTheme)
    }
}

@Composable
private fun VoidEnergyLayer(darkTheme: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "EnergyTransition")
    val energyColor1 = if (darkTheme) VoidEnergyDark1 else VoidEnergyLight1
    val energyColor2 = if (darkTheme) VoidEnergyDark2 else VoidEnergyLight2

    val density = LocalDensity.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Energy Blob 1
        val blob1Offset by infiniteTransition.animateValue(
            initialValue = Offset(widthPx * 0.1f, heightPx * 0.2f),
            targetValue = Offset(widthPx * 0.2f, heightPx * 0.1f),
            typeConverter = Offset.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(30000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Blob1Pos"
        )
        
        EnergyBlob(
            color = energyColor1,
            offset = blob1Offset,
            scale = 1.2f
        )

        // Energy Blob 2
        val blob2Offset by infiniteTransition.animateValue(
            initialValue = Offset(widthPx * 0.8f, heightPx * 0.7f),
            targetValue = Offset(widthPx * 0.7f, heightPx * 0.8f),
            typeConverter = Offset.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(25000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Blob2Pos"
        )

        EnergyBlob(
            color = energyColor2,
            offset = blob2Offset,
            scale = 1.5f
        )
    }
}

@Composable
private fun EnergyBlob(color: Color, offset: Offset, scale: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationX = offset.x; translationY = offset.y; scaleX = scale; scaleY = scale }
            .drawWithCache {
                val brush = Brush.radialGradient(
                    0f to color, 1f to Color.Transparent,
                    radius = size.minDimension * 0.3f // proporcional, ajustável
                )
                onDrawBehind { drawRect(brush) }
            }
    )
}

@Composable
private fun CenterGlowLayer(state: OrbitalState, darkTheme: Boolean) {
    val glowColor = if (darkTheme) VoidGlowDark else VoidGlowLight
    
    // Smooth reaction to rotation speed
    val reactiveIntensity by animateFloatAsState(
        targetValue = 1f + (state.normalizedRotationSpeed * 0.5f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "GlowReaction"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { scaleX = reactiveIntensity; scaleY = reactiveIntensity }
            .drawWithCache {
                val brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
                    center = Offset.Unspecified,
                    radius = size.minDimension * 0.35f // ajustável
                )
                onDrawBehind { drawRect(brush) }
            }
    )
}

@Composable
private fun ParticleLayer(darkTheme: Boolean) {
    val particleColor = if (darkTheme) VoidParticleDark else VoidParticleLight
    val infiniteTransition = rememberInfiniteTransition(label = "Particles")
    
    // Period of 3 minutes (180,000 ms)
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(180000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticlePhase"
    )

    val particles = remember {
        val random = Random(42)
        List(20) {
            ParticleData(
                radiusFraction = random.nextFloat() * 0.4f + 0.15f,
                speedMultiplier = random.nextFloat() * 0.5f + 0.5f,
                initialAngle = random.nextFloat() * 2f * Math.PI.toFloat(),
                size = random.nextFloat() * 4f + 2f,
                alpha = random.nextFloat() * 0.25f + 0.15f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val minDim = size.minDimension
        val center = Offset(size.width / 2f, size.height / 2f)

        particles.forEach { p ->
            val angle = p.initialAngle + (phase * p.speedMultiplier)
            val actualRadius = p.radiusFraction * minDim
            val x = center.x + actualRadius * cos(angle)
            val y = center.y + actualRadius * sin(angle)
            drawCircle(color = particleColor.copy(alpha = p.alpha), radius = p.size.dp.toPx(), center = Offset(x, y))
        }
    }
}

private data class ParticleData(
    val radiusFraction: Float,
    val speedMultiplier: Float,
    val initialAngle: Float,
    val size: Float,
    val alpha: Float
)
