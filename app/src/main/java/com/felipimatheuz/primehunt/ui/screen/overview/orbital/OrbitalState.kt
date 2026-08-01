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

/**
 * Holder for the orbital carousel state.
 * Uses a velocity-based engine for smooth transitions between manual interaction and idle rotation.
 */
class OrbitalState(
    val scope: CoroutineScope,
    initialAngle: Float = INITIAL_ANGLE_DEGREES,
    private val reduceMotion: Boolean = false
) {
    // Current base angle of the orbit.
    val angle = Animatable(initialAngle)
    
    // Angular velocity in degrees per second.
    var currentVelocity by mutableFloatStateOf(IDLE_VELOCITY_DEG_PER_SEC)
        private set

    // Interaction flags
    var isDragging by mutableStateOf(false)
    private var isFlinging by mutableStateOf(false)
    
    // Focused card index
    var focusedIndex by mutableStateOf<Int?>(null)

    private var focusReturnJob: Job? = null

    companion object {
        const val INITIAL_ANGLE_DEGREES = 90f // South / Closest point
        const val REVOLUTION_DURATION_MS = 75_000L
        
        // Anti-clockwise: angle decreases over time.
        const val IDLE_VELOCITY_DEG_PER_SEC = -360f / (REVOLUTION_DURATION_MS / 1000f)
        
        const val PIXELS_PER_DEGREE = 8f
        const val FOCUS_DURATION_MS = 5_000L
        
        // Calibration for 60fps integration
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
                        // 1. Apply friction during momentum
                        currentVelocity *= FRICTION_PER_FRAME
                        
                        // 2. Check transition to idle (when speed drops or direction stabilizes)
                        // If we are spinning fast in any direction, decay. 
                        // Once we are close to idle speed, blend into it.
                        if (abs(currentVelocity) < abs(IDLE_VELOCITY_DEG_PER_SEC) * 1.5f) {
                            isFlinging = false
                        }
                    } else {
                        // Smoothly approach idle velocity (Anti-clockwise)
                        currentVelocity = lerp(currentVelocity, IDLE_VELOCITY_DEG_PER_SEC, VELOCITY_APPROACH_FACTOR)
                    }

                    // 3. Integrate position
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
        // Set initial momentum velocity
        currentVelocity = pixelVelocityX / PIXELS_PER_DEGREE
    }

    fun onDragCancel() {
        isDragging = false
        isFlinging = false
        // Loop will automatically blend currentVelocity back to IDLE_VELOCITY
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
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    return remember { OrbitalState(scope, initialAngle, reduceMotion) }
}
