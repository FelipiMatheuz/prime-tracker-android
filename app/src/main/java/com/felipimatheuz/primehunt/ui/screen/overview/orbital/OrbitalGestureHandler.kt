package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker

/**
 * Custom modifier to handle orbital drag gestures and momentum.
 * Hooks into the velocity-based engine in OrbitalState.
 */
fun Modifier.orbitalGestureHandler(
    state: OrbitalState
): Modifier = this.pointerInput(Unit) {
    val velocityTracker = VelocityTracker()
    
    detectDragGestures(
        onDragStart = {
            velocityTracker.resetTracking()
            state.onDragStart()
        },
        onDrag = { change: PointerInputChange, dragAmount ->
            velocityTracker.addPosition(change.uptimeMillis, change.position)
            state.onDrag(-dragAmount.x)
        },
        onDragEnd = {
            val velocity = velocityTracker.calculateVelocity()
            state.onFling(-velocity.x)
        },
        onDragCancel = {
            state.onDragCancel()
        }
    )
}
