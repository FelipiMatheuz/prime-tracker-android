package com.felipimatheuz.primehunt.ui.screen.splash.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import com.felipimatheuz.primehunt.ui.theme.Error
import com.felipimatheuz.primehunt.ui.theme.White
import com.felipimatheuz.primehunt.domain.model.SyncEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CephalonAnimation(
    syncEvent: SyncEvent,
    onStart: () -> Unit,
    onReady: () -> Unit
) {
    val state = rememberCephalonAnimation(syncEvent, onStart, onReady)
    CephalonScene(state)
}

@Composable
private fun rememberCephalonAnimation(
    syncEvent: SyncEvent,
    onStart: () -> Unit,
    onReady: () -> Unit
): CephalonAnimationState {

    val density = LocalDensity.current

    val bodyAlpha = remember { Animatable(0f) }
    val bodyScale = remember { Animatable(.92f) }

    val piecesAlpha = remember { Animatable(0f) }
    val piecesScale = remember { Animatable(.8f) }
    val piecesRotation = remember { Animatable(0f) }
    val piecesOffset = remember { Animatable(12f) }

    val tint by animateColorAsState(
        targetValue = if (syncEvent is SyncEvent.Error) Error else White,
        animationSpec = tween(250),
        label = "cephalonTint"
    )

    LaunchedEffect(syncEvent) {

        when (syncEvent) {

            SyncEvent.Starting -> {

                playStartup(
                    bodyAlpha,
                    bodyScale,
                    piecesAlpha,
                    piecesScale,
                    piecesOffset,
                    onStart
                )

            }

            is SyncEvent.CheckingManifest,
            is SyncEvent.Downloading,
            is SyncEvent.Importing -> {

                processingLoop(
                    bodyAlpha,
                    piecesRotation
                )

            }

            is SyncEvent.Success,
            SyncEvent.AlreadyUpToDate -> {

                playSuccess(
                    bodyAlpha,
                    bodyScale,
                    piecesAlpha,
                    piecesScale,
                    piecesRotation,
                    piecesOffset,
                    onReady
                )

            }

            SyncEvent.Error -> {

                playError(
                    bodyAlpha,
                    piecesAlpha,
                    piecesRotation,
                    piecesOffset,
                    onReady
                )

            }

        }

    }

    return CephalonAnimationState(

        bodyAlpha = bodyAlpha.value,

        bodyScale = bodyScale.value,

        bodyTint = tint,

        piecesRotation = piecesRotation.value,

        piecesAlpha = piecesAlpha.value,

        piecesTint = tint,

        piecesOffset = with(density) {
            piecesOffset.value.toDp()
        },

        piecesScale = piecesScale.value

    )
}

private suspend fun playStartup(
    bodyAlpha: Animatable<Float, AnimationVector1D>,
    bodyScale: Animatable<Float, AnimationVector1D>,
    piecesAlpha: Animatable<Float, AnimationVector1D>,
    piecesScale: Animatable<Float, AnimationVector1D>,
    piecesOffset: Animatable<Float, AnimationVector1D>,
    onStart: () -> Unit
) = coroutineScope {

    launch {
        bodyAlpha.snapTo(0f)
        bodyAlpha.animateTo(
            1f,
            tween(125)
        )
    }

    launch {
        bodyScale.snapTo(.92f)
        bodyScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        )
    }

    launch {
        piecesAlpha.snapTo(0f)
        piecesAlpha.animateTo(
            1f,
            tween(125)
        )
    }

    launch {
        piecesScale.snapTo(.8f)
        piecesScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        )

        piecesScale.animateTo(
            1.05f,
            tween(45)
        )

        piecesScale.animateTo(
            1f,
            tween(45)
        )
    }

    launch {
        piecesOffset.snapTo(12f)
        piecesOffset.animateTo(
            0f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        )
        onStart()
    }
}

private suspend fun processingLoop(
    bodyAlpha: Animatable<Float, AnimationVector1D>,
    piecesRotation: Animatable<Float, AnimationVector1D>
) = coroutineScope {

    launch {

        while (isActive) {

            piecesRotation.animateTo(
                piecesRotation.value + 360f,
                tween(
                    durationMillis = 1200,
                    easing = LinearEasing
                )
            )

        }

    }

    launch {

        while (isActive) {

            bodyAlpha.animateTo(
                .92f,
                tween(500)
            )

            bodyAlpha.animateTo(
                1f,
                tween(500)
            )

        }

    }

}

private suspend fun playSuccess(
    bodyAlpha: Animatable<Float, AnimationVector1D>,
    bodyScale: Animatable<Float, AnimationVector1D>,
    piecesAlpha: Animatable<Float, AnimationVector1D>,
    piecesScale: Animatable<Float, AnimationVector1D>,
    piecesRotation: Animatable<Float, AnimationVector1D>,
    piecesOffset: Animatable<Float, AnimationVector1D>,
    onReady: () -> Unit
) = coroutineScope {

    launch {

        val current = piecesRotation.value % 360f

        piecesRotation.animateTo(
            current,
            tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        )

    }

    launch {

        bodyScale.animateTo(
            1.05f,
            tween(120)
        )

        bodyScale.animateTo(
            1f,
            tween(120)
        )

    }

    launch {

        piecesScale.animateTo(
            1.05f,
            tween(120)
        )

        piecesScale.animateTo(
            1f,
            tween(120)
        )

    }

    launch {

        delay(120.milliseconds)

        piecesOffset.animateTo(
            12f,
            tween(
                durationMillis = 220,
                easing = FastOutSlowInEasing
            )
        )

    }

    launch {

        delay(180.milliseconds)

        bodyAlpha.animateTo(
            0f,
            tween(250)
        )

        piecesAlpha.animateTo(
            0f,
            tween(250)
        )
        onReady()
    }

}

private suspend fun playError(
    bodyAlpha: Animatable<Float, AnimationVector1D>,
    piecesAlpha: Animatable<Float, AnimationVector1D>,
    piecesRotation: Animatable<Float, AnimationVector1D>,
    piecesOffset: Animatable<Float, AnimationVector1D>,
    onReady: () -> Unit
) = coroutineScope {

    launch {

        val current = piecesRotation.value % 360f

        piecesRotation.animateTo(
            current,
            tween(
                durationMillis = 150,
                easing = FastOutSlowInEasing
            )
        )

    }

    launch {

        repeat(3) {

            piecesOffset.animateTo(
                3f,
                tween(90)
            )

            piecesOffset.animateTo(
                0f,
                tween(90)
            )

        }

    }

    launch {

        delay(600.milliseconds)

        bodyAlpha.animateTo(
            0f,
            tween(250)
        )

        piecesAlpha.animateTo(
            0f,
            tween(250)
        )
        onReady()
    }

}