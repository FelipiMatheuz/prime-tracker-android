package com.felipimatheuz.primehunt.ui.screen.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.domain.model.enums.BgIcons

@Composable
fun PrimeBackgroundPattern(
    icon: BgIcons,
    modifier: Modifier = Modifier
) {
    val tintColor = MaterialTheme.colorScheme.onBackground

    val painter = painterResource(id = icon.icon)
    val iconSize = 40.dp
    val density = LocalDensity.current

    val (sizePx, spacingX, spacingY) = remember(density) {
        val sPx = with(density) { iconSize.toPx() }
        Triple(sPx, sPx * 2.5f, sPx * 1.5f)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val rows = (size.height / spacingY).toInt() + 2
        val cols = (size.width / spacingX).toInt() + 2

        for (row in 0 until rows) {
            val offsetX = if (row % 2 == 0) 0f else spacingX / 2f
            for (col in -1 until cols) {
                translate(left = col * spacingX + offsetX, top = row * spacingY) {
                    with(painter) {
                        draw(
                            size = Size(sizePx, sizePx),
                            alpha = 0.05f,
                            colorFilter = ColorFilter.tint(tintColor)
                        )
                    }
                }
            }
        }
    }
}
