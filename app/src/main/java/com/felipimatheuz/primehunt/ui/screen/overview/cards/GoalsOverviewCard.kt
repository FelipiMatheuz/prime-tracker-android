package com.felipimatheuz.primehunt.ui.screen.overview.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.local.entity.GoalTagEntity
import com.felipimatheuz.primehunt.data.local.enums.GoalIcons
import com.felipimatheuz.primehunt.data.local.enums.GoalStatus
import com.felipimatheuz.primehunt.ui.screen.components.GoalTagChip
import com.felipimatheuz.primehunt.ui.screen.overview.orbital.OrbitalDimens
import com.felipimatheuz.primehunt.ui.theme.Completed
import com.felipimatheuz.primehunt.ui.theme.InProgress
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.viewmodel.overview.GoalsOverviewUi

@Composable
fun GoalsOverviewCard(state: GoalsOverviewUi) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.menu_goals),
            style = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            GoalStat(GoalStatus.ACTIVE, state.activeGoals.toString())
            GoalStat(GoalStatus.COMPLETED, state.completedGoals.toString())
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.goals_most_used_tag).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (state.mainTag != null) {
                GoalTagChip(
                    text = state.mainTag.name,
                    iconRes = state.mainTag.icon.icon,
                    color = state.mainTag.color
                )
            } else {
                Text(stringResource(R.string.no_data_dash))
            }
        }
    }
}

@Composable
private fun GoalStat(status: GoalStatus, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val color = if (status == GoalStatus.COMPLETED) Completed else InProgress
        val icon =
            if (status == GoalStatus.COMPLETED) R.drawable.ic_check else R.drawable.progress_circle

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(color = color),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(status.displayNameRes),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GoalsOverviewCardPreview() {
    PrimeTrackerTheme {
        Box(
            modifier = Modifier
                .size(OrbitalDimens.CardSize)
                .padding(48.dp, 48.dp)
        ) {
            GoalsOverviewCard(
                state = GoalsOverviewUi(
                    activeGoals = 10,
                    completedGoals = 20,
                    mainTag = GoalTagEntity(
                        id = 1,
                        name = "Main Tag",
                        icon = GoalIcons.WARFRAME,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            )
        }
    }
}