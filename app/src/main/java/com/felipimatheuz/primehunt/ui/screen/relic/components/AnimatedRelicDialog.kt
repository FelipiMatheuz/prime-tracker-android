package com.felipimatheuz.primehunt.ui.screen.relic.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AnimatedRelicDialog(
    relicEraIcon: Int,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    var showScrim by remember { mutableStateOf(false) }
    var scaleIcon by remember { mutableStateOf(false) }
    var expandDialog by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        showScrim = true

        delay(120.milliseconds)

        scaleIcon = true

        delay(260.milliseconds)

        expandDialog = true

        delay(260.milliseconds)

        showContent = true
    }

    fun dismiss() {
        if (closing) return

        closing = true

        scope.launch {

            showContent = false

            delay(180.milliseconds)

            expandDialog = false

            delay(220.milliseconds)

            scaleIcon = false

            delay(180.milliseconds)

            showScrim = false

            delay(250.milliseconds)

            onDismiss()
        }
    }

    BackHandler { dismiss() }

    val scrimAlpha by animateFloatAsState(
        targetValue = if (showScrim) .6f else 0f,
        animationSpec = tween(250),
        label = ""
    )

    val iconScale by animateFloatAsState(
        targetValue = if (scaleIcon) 2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = ""
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(180),
        label = ""
    )

    Dialog(
        onDismissRequest = ::dismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = scrimAlpha)),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(.9f),
                contentAlignment = Alignment.TopCenter
            ) {

                AnimatedVisibility(
                    visible = expandDialog,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(
                            durationMillis = 260,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(
                            durationMillis = 220,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp),
                        shape = RoundedCornerShape(24.dp),
                        tonalElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(contentAlpha)
                        ) {

                            Column(
                                modifier = Modifier.padding(
                                    top = 36.dp,
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 20.dp
                                )
                            ) {
                                content()
                            }

                        }

                    }

                }

                Image(
                    painter = painterResource(relicEraIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .zIndex(2f)
                        .size(56.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                )

            }

        }

    }
}