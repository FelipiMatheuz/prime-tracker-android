package com.felipimatheuz.primehunt.ui.screen.overview.orbital

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun OrbitalCard(
    page: @Composable BoxScope.() -> Unit,
    scale: Float,
    alpha: Float,
    zIndex: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

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
            Box(
                modifier = Modifier
                    .padding(16.dp, 24.dp)
                    .fillMaxSize(),
                content = page
            )
        }

        Image(
            painter = painterResource(id = R.drawable.void_border),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@Preview
@Composable
fun OrbitalCardPreview() {
    PrimeTrackerTheme {
        OrbitalCard(
            scale = 1f,
            alpha = 1f,
            zIndex = 0f,
            onClick = {},
            page = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OrbitalCardDarkPreview() {
    PrimeTrackerTheme {
        OrbitalCard(
            scale = 1f,
            alpha = 1f,
            zIndex = 0f,
            onClick = {},
            page = {}
        )
    }
}
