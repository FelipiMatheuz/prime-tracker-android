package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

class OrbitalState(
    val scope: CoroutineScope,
    initialAngle: Float = INITIAL_ANGLE_DEGREES,
    private val reduceMotion: Boolean = false
) {

    val angle = Animatable(initialAngle)

    var currentVelocity by mutableFloatStateOf(IDLE_VELOCITY_DEG_PER_SEC)
        private set

    var isDragging by mutableStateOf(false)
    private var isFlinging by mutableStateOf(false)

    val normalizedRotationSpeed: Float
        get() = (abs(currentVelocity) / 200f).coerceIn(0f, 1f)

    var focusedIndex by mutableStateOf<Int?>(null)

    private var focusReturnJob: Job? = null

    companion object {
        const val INITIAL_ANGLE_DEGREES = 90f
        const val REVOLUTION_DURATION_MS = 75_000L

        const val IDLE_VELOCITY_DEG_PER_SEC = -360f / (REVOLUTION_DURATION_MS / 1000f)

        const val PIXELS_PER_DEGREE = 8f
        const val FOCUS_DURATION_MS = 5_000L

        const val FRICTION_PER_FRAME = 0.98f
        const val VELOCITY_APPROACH_FACTOR = 0.05f
    }

    init {
        startIntegrationLoop()
    }

    private fun startIntegrationLoop() {
        scope.launch {
            var lastFrameTime = withFrameNanos { it }
            while (true) {
                val currentFrameTime = withFrameNanos { it }
                val deltaTimeSeconds = (currentFrameTime - lastFrameTime) / 1_000_000_000f
                lastFrameTime = currentFrameTime

                if (!isDragging && !reduceMotion) {
                    if (isFlinging) {
                        currentVelocity *= FRICTION_PER_FRAME

                        if (abs(currentVelocity) < abs(IDLE_VELOCITY_DEG_PER_SEC) * 1.5f) {
                            isFlinging = false
                        }
                    } else {
                        currentVelocity = lerp(
                            currentVelocity,
                            IDLE_VELOCITY_DEG_PER_SEC,
                            VELOCITY_APPROACH_FACTOR
                        )
                    }

                    val newAngle = angle.value + (currentVelocity * deltaTimeSeconds)
                    angle.snapTo(newAngle)
                }
            }
        }
    }

    fun onDragStart() {
        isDragging = true
        isFlinging = false
    }

    fun onDrag(deltaX: Float) {
        val deltaAngle = deltaX / PIXELS_PER_DEGREE
        scope.launch {
            angle.snapTo(angle.value + deltaAngle)
        }
    }

    fun onFling(pixelVelocityX: Float) {
        isDragging = false
        isFlinging = true
        currentVelocity = pixelVelocityX / PIXELS_PER_DEGREE
    }

    fun onDragCancel() {
        isDragging = false
        isFlinging = false
    }

    fun focusCard(index: Int) {
        if (focusedIndex != null) return
        focusedIndex = index

        focusReturnJob?.cancel()
        focusReturnJob = scope.launch {
            delay(FOCUS_DURATION_MS.milliseconds)
            focusedIndex = null
        }
    }
}

@Composable
fun rememberOrbitalState(
    initialAngle: Float = OrbitalState.INITIAL_ANGLE_DEGREES,
    scope: CoroutineScope = rememberCoroutineScope()
): OrbitalState {
    val context = LocalContext.current
    val reduceMotion = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }
    return remember { OrbitalState(scope, initialAngle, reduceMotion) }
}
