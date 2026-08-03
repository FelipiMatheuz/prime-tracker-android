package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.Gold200
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

data class OrbitalCardData(
    val title: String,
    val value: String,
    val subValue: String? = null
)

@Composable
fun OrbitalCard(
    data: OrbitalCardData,
    scale: Float,
    alpha: Float,
    zIndex: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val borderTint = if (isSystemInDarkTheme()) {
        null
    } else {
        ColorFilter.tint(Gold200, blendMode = BlendMode.Modulate)
    }

    Box(
        modifier = modifier
            .size(OrbitalDimens.CardSize)
            .zIndex(zIndex)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    ) {

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable(onClick = onClick)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = data.value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (data.subValue != null) {
                    Text(
                        text = data.subValue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.void_border),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize().alpha(0.8f),
            colorFilter = borderTint
        )
    }
}

@Preview
@Composable
fun OrbitalCardPreview() {
    PrimeTrackerTheme {
        OrbitalCard(
            data = OrbitalCardData("Collection Progress", "78%", "132 / 170"),
            scale = 1f,
            alpha = 1f,
            zIndex = 0f,
            onClick = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OrbitalCardDarkPreview() {
    PrimeTrackerTheme {
        OrbitalCard(
            data = OrbitalCardData("Collection Progress", "78%", "132 / 170"),
            scale = 1f,
            alpha = 1f,
            zIndex = 0f,
            onClick = {}
        )
    }
}
