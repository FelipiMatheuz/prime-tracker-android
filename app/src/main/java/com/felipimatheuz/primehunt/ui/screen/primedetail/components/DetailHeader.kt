package com.felipimatheuz.primehunt.ui.screen.primedetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.theme.High
import com.felipimatheuz.primehunt.ui.theme.Low
import com.felipimatheuz.primehunt.ui.theme.Zero

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailHeader(primeSet: PrimeSetDomain) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimePanel(modifier = Modifier.size(200.dp)) {
            GlideImage(
                model = primeSet.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = primeSet.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(primeSet.type.displayNameRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        val progress =
            if (primeSet.totalPieces > 0) primeSet.ownedPieces.toFloat() / primeSet.totalPieces else 0f

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(8.dp)
                .clip(CircleShape),
            color = if (progress == 1f) High else if (progress > 0) Low else Zero
        )
        Text(
            text = stringResource(
                R.string.detail_pieces_owned_template,
                primeSet.ownedPieces,
                primeSet.totalPieces
            ),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
