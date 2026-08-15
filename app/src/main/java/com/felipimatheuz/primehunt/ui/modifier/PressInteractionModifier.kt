package com.felipimatheuz.primehunt.ui.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Reusable press interaction modifiers for visual feedback in Prime Tracker.
 *
 * These modifiers are purely visual decorators and do not handle click events,
 * gestures, or selection states. They observe an external [MutableInteractionSource]
 * provided by the component and apply a temporary scale transformation during a [PressInteraction].
 *
 * CONCEPT:
 * - Decorator: Only affects the visual representation.
 * - Non-intrusive: Does not create or own the [InteractionSource].
 * - Decoupled: `enabled` only controls the visual feedback and does not enable or
 *   disable the underlying interaction.
 *
 * PERFORMANCE:
 * - Uses the modern [Modifier.Node] API.
 * - Uses [DrawModifierNode] for efficient visual transformation.
 * - Minimal state and no continuous recomposition.
 *
 * WARNING:
 * Do not use with OrbitalCarousel or components with complex physical interactions
 * (swipe, momentum, snap) as this might conflict with their specific interaction models.
 */

enum class PressIntensity(val scale: Float) {
    VERY_SUBTLE(0.975f),
    SUBTLE(0.95f),
    MODERATE(0.925f),
    INTENSE(0.9f)
}

/**
 * Modifier for press action feedback.
 *
 * Intensity:
 * - Very subtle (0.975 - e.g. large buttons, cards);
 * - Subtle (0.95 - e.g. primary and secondary action buttons);
 * - Moderate (0.925 - e.g. Toolbar icons, small buttons);
 * - Intense (0.9 - e.g. highlight visual press)
 *
 * @param interactionSource The source of interactions to observe.
 * @param intensity The intensity of the visual feedback.
 * @param enabled Whether the visual feedback is applied. Defaults to true.
 */
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    intensity: PressIntensity,
    enabled: Boolean = true
): Modifier = this.then(
    PressInteractionElement(
        interactionSource = interactionSource,
        enabled = enabled,
        pressedScale = intensity.scale
    )
)

/**
 * Internal element that handles the creation and update of the [PressInteractionNode].
 */
private data class PressInteractionElement(
    val interactionSource: InteractionSource,
    val enabled: Boolean,
    val pressedScale: Float
) : ModifierNodeElement<PressInteractionNode>() {
    override fun create(): PressInteractionNode = PressInteractionNode(
        interactionSource = interactionSource,
        enabled = enabled,
        pressedScale = pressedScale
    )

    override fun update(node: PressInteractionNode) {
        node.update(
            interactionSource = interactionSource,
            enabled = enabled,
            pressedScale = pressedScale
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "pressScale"
        properties["interactionSource"] = interactionSource
        properties["enabled"] = enabled
        properties["pressedScale"] = pressedScale
    }
}

/**
 * Modern Modifier Node that implements the visual feedback logic.
 * It observes the [InteractionSource] and animates a scale value.
 */
private class PressInteractionNode(
    private var interactionSource: InteractionSource,
    private var enabled: Boolean,
    private var pressedScale: Float
) : Modifier.Node(), DrawModifierNode {

    private var interactionJob: Job? = null
    private var returnJob: Job? = null
    private val scale = Animatable(1f)

    override fun onAttach() {
        super.onAttach()
        observeInteractions()
    }

    override fun onDetach() {
        super.onDetach()
        interactionJob?.cancel()
        returnJob?.cancel()
    }

    /**
     * Updates the node parameters when the [Modifier] is recomposed.
     */
    fun update(
        interactionSource: InteractionSource,
        enabled: Boolean,
        pressedScale: Float
    ) {
        val oldSource = this.interactionSource
        val oldEnabled = this.enabled
        
        this.interactionSource = interactionSource
        this.enabled = enabled
        this.pressedScale = pressedScale

        if (oldSource != interactionSource) {
            observeInteractions()
        }

        if (oldEnabled && !enabled) {
            returnJob?.cancel()
            coroutineScope.launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            }
        }
    }

    /**
     * Observes the interaction stream and triggers animations.
     * Uses collect to preserve temporal sequence.
     */
    private fun observeInteractions() {
        interactionJob?.cancel()
        interactionJob = coroutineScope.launch {
            var activePressCount = 0

            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        activePressCount++
                        if (enabled) {
                            returnJob?.cancel()
                            scale.animateTo(pressedScale, tween(durationMillis = 40))
                        }
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        activePressCount = (activePressCount - 1).coerceAtLeast(0)
                        if (activePressCount == 0) {
                            returnJob?.cancel()
                            returnJob = launch {
                                scale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Applies the scale transformation during the draw phase.
     */
    override fun ContentDrawScope.draw() {
        scale(scale.value) {
            this@draw.drawContent()
        }
    }
}
