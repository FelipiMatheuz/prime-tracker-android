package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

object NavTransitions {
    private const val ENTER_DURATION = 330
    private const val EXIT_DURATION = 300
    private const val SCALE_SUBTLE = 0.9f

    fun AnimatedContentTransitionScope<Scene<NavKey>>.calculateTransition(): ContentTransform {
        val fromKey = initialState.metadata["route"]
        val toKey = targetState.metadata["route"]

        return if (fromKey is PrimeSetsKey && toKey is PrimeDetailKey) {
            (fadeIn(animationSpec = tween(ENTER_DURATION, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = SCALE_SUBTLE,
                        animationSpec = tween(ENTER_DURATION, easing = FastOutSlowInEasing)
                    )) togetherWith
                    fadeOut(animationSpec = tween(EXIT_DURATION, easing = FastOutSlowInEasing))
        } else {
            fadeIn(animationSpec = tween(ENTER_DURATION)) togetherWith
                    fadeOut(animationSpec = tween(EXIT_DURATION))
        }
    }

    fun AnimatedContentTransitionScope<Scene<NavKey>>.calculatePopTransition(): ContentTransform {
        val fromKey = initialState.metadata["route"]
        val toKey = targetState.metadata["route"]

        return if (fromKey is PrimeDetailKey && toKey is PrimeSetsKey) {
            fadeIn(animationSpec = tween(ENTER_DURATION, easing = EaseOutSine)) togetherWith
                    (fadeOut(animationSpec = tween(EXIT_DURATION, easing = EaseOutSine)) +
                            scaleOut(
                                targetScale = SCALE_SUBTLE,
                                animationSpec = tween(EXIT_DURATION, easing = EaseOutSine)
                            ))
        } else {
            fadeIn(animationSpec = tween(ENTER_DURATION)) togetherWith
                    fadeOut(animationSpec = tween(EXIT_DURATION))
        }
    }
}
