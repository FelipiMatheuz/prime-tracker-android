package com.felipimatheuz.primehunt.ui.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun PrimePanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val outlineColor = MaterialTheme.colorScheme.primary
    val variantColor = MaterialTheme.colorScheme.primaryContainer

    Box(
        modifier = modifier
            .padding(16.dp)
            .drawWithCache {
                val strokeWidthPx = 4.dp.toPx()
                val chamferSize = 16.dp.toPx()
                val detailSize = size.height * 0.05f
                
                val framePath = Path().apply {
                    moveTo(chamferSize, 0f)
                    lineTo(size.width - chamferSize, 0f)

                    lineTo(size.width, chamferSize)
                    lineTo(size.width, size.height - chamferSize)

                    lineTo(size.width - chamferSize, size.height)
                    
                    val notchWidth = 40.dp.toPx()
                    val centerX = size.width / 2f
                    
                    lineTo(centerX + notchWidth / 2f, size.height)
                    lineTo(centerX - notchWidth / 2f, size.height)

                    lineTo(chamferSize, size.height)
                    lineTo(0f, size.height - chamferSize)
                    lineTo(0f, chamferSize)
                    close()
                }

                val metallicBrush = Brush.linearGradient(
                    colors = listOf(variantColor, outlineColor, variantColor),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )

                onDrawWithContent {
                    drawPath(
                        path = framePath,
                        brush = metallicBrush,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )

                    val paddingSide = 4.dp.toPx()

                    drawLine(
                        color = outlineColor,
                        start = Offset(-paddingSide, size.height / 2f - detailSize / 2f),
                        end = Offset(-paddingSide, size.height / 2f + detailSize / 2f),
                        strokeWidth = strokeWidthPx / 1.5f,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = outlineColor,
                        start = Offset(size.width + paddingSide, size.height / 2f - detailSize / 2f),
                        end = Offset(size.width + paddingSide, size.height / 2f + detailSize / 2f),
                        strokeWidth = strokeWidthPx / 1.5f,
                        cap = StrokeCap.Round
                    )

                    drawContent()
                }
            }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Preview(showBackground = true, name = "Tema Claro")
@Composable
fun PrimePanelLightPreview() {
    PrimeTrackerTheme(theme = AppTheme.LIGHT) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Box(modifier = Modifier.padding(24.dp)) {
                PrimePanel {
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CONTEÚDO PRIME")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Tema Escuro", backgroundColor = 0xFF1C1B1F)
@Composable
fun PrimePanelDarkPreview() {
    PrimeTrackerTheme(theme = AppTheme.DARK) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Box(modifier = Modifier.padding(24.dp)) {
                PrimePanel {
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CONTEÚDO PRIME")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Retangular")
@Composable
fun PrimePanelRectangularPreview() {
    PrimeTrackerTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Box(modifier = Modifier.padding(24.dp)) {
                PrimePanel {
                    Box(
                        modifier = Modifier.size(width = 280.dp, height = 120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("BANNER PROMOCIONAL")
                    }
                }
            }
        }
    }
}
