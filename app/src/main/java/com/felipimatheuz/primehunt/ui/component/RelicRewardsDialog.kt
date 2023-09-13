package com.felipimatheuz.primehunt.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.core.Relic
import com.felipimatheuz.primehunt.model.core.RelicTier
import com.felipimatheuz.primehunt.viewmodel.RelicViewModel

@Composable
fun RelicRewardsDialog(viewModel: RelicViewModel, relicTier: RelicTier, relic: Relic, dismiss: () -> Unit) {
    val context = LocalContext.current
    val relicName = "$relicTier ${relic.name}"
    val relicApi = viewModel.getRelic(relicName)
    AlertDialog(
        icon = {
            Image(
                painter = painterResource(viewModel.getImageTier(relicTier)), "",
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(text = stringResource(R.string.relic_template, relicName))
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                relicApi.rewards.forEach { reward ->
                    Text(
                        viewModel.formatRelicItemReward(context, reward.item.name),
                        color = viewModel.getColor(reward.chance)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { dismiss() }) {
                Text(text = stringResource(R.string.close))
            }
        },
        onDismissRequest = {},
        shape = RoundedCornerShape(10.dp)
    )
}