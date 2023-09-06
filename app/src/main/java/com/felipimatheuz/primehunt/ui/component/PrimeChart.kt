package com.felipimatheuz.primehunt.ui.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.ui.theme.Complete
import com.felipimatheuz.primehunt.ui.theme.High
import com.felipimatheuz.primehunt.ui.theme.Zero
import com.felipimatheuz.primehunt.util.isItemCompleted

@Composable
fun PrimeChart(
    labelRes: Int,
    data: List<PrimeItem?>,
    modifier: Modifier = Modifier,
    scaleSize: Float = 1f,
    animDuration: Int = 1000
) {

    val totalItems = data.size
    val completed = data.count { it?.let { it1 -> isItemCompleted(it1) } == 2 }
    val inProgress = data.count { it?.let { it1 -> isItemCompleted(it1) } == 1 }
    val floatValue = mutableListOf<Float>()
    floatValue.add(360 * completed.toFloat() / totalItems.toFloat())
    floatValue.add(360 * inProgress.toFloat() / totalItems.toFloat())
    floatValue.add(360 * (totalItems - completed - inProgress).toFloat() / totalItems.toFloat())

    val colors = listOf(
        Complete,
        High,
        Zero
    )

    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f

    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) 320.dp.value * scaleSize else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ), label = ""
    )
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp * scaleSize),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(320.dp * scaleSize)
                    .rotate(animateRotation)
            ) {
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke((35.dp * scaleSize).toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
            Text(
                text = stringResource(R.string.overall_stats, totalItems, inProgress, completed),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp * scaleSize)
            )

        }
    }
}