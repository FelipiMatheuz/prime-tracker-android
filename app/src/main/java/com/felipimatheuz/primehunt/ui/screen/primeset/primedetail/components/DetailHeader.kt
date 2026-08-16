package com.felipimatheuz.primehunt.ui.screen.primeset.primedetail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.screen.components.PrimePanel
import com.felipimatheuz.primehunt.ui.theme.Completed
import com.felipimatheuz.primehunt.ui.theme.InProgress
import com.felipimatheuz.primehunt.ui.theme.NotStarted

@Composable
fun DetailHeader(primeSet: PrimeSetDomain) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        PrimePanel(modifier = Modifier.size(200.dp)) {
            AsyncImage(
                model = primeSet.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_orokin),
                error = painterResource(R.drawable.ic_orokin),
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

        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            label = "progressAnimation"
        )

        val targetColor = if (progress == 1f) Completed else if (progress > 0) InProgress else NotStarted
        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(300),
            label = "colorAnimation"
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(8.dp)
                .clip(CircleShape),
            color = animatedColor
        )
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(
                    R.string.detail_pieces_owned_template,
                    primeSet.ownedPieces,
                    primeSet.totalPieces
                ),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            AnimatedVisibility(targetColor == Completed) {
                Image(
                    painter = painterResource(id = R.drawable.mastery_rank),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
