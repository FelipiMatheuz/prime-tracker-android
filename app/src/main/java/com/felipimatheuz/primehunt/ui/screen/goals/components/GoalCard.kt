package com.felipimatheuz.primehunt.ui.screen.goals.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.GoalIcons
import com.felipimatheuz.primehunt.domain.model.enums.GoalStatus
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.GoalDomain
import com.felipimatheuz.primehunt.domain.model.GoalTagDomain
import com.felipimatheuz.primehunt.ui.screen.components.GoalTagChip
import com.felipimatheuz.primehunt.ui.theme.Completed
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCard(
    goal: GoalDomain,
    modifier: Modifier = Modifier,
    isNew: Boolean = false,
    isSuccess: Boolean = false,
    onClick: () -> Unit
) {
    val isCompleted = goal.status == GoalStatus.COMPLETED
    val completionRatio = if (goal.desiredQuantity > 0) {
        goal.currentQuantity.toFloat() / goal.desiredQuantity
    } else 0f

    val progressColor = when {
        isCompleted || completionRatio >= 1f -> Completed
        completionRatio >= 0.5f -> MaterialTheme.colorScheme.secondary
        completionRatio > 0f -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }

    var isHighlighted by remember { mutableStateOf(false) }
    var showSuccessHighlight by remember { mutableStateOf(false) }

    LaunchedEffect(isNew) {
        if (isNew) {
            isHighlighted = true
            delay(1.seconds)
            isHighlighted = false
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            showSuccessHighlight = true
            delay(0.5.seconds)
            showSuccessHighlight = false
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            showSuccessHighlight -> Completed.copy(alpha = 0.3f)
            isHighlighted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        },
        animationSpec = tween(durationMillis = if (showSuccessHighlight) 200 else 500),
        label = "highlightAnimation"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 170.dp)
            .alpha(if (isCompleted) 0.5f else 1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            // 1. Tag (The Card Identity)
            GoalTagChip(
                text = goal.tag.name,
                iconRes = goal.tag.icon.icon,
                color = Color(goal.tag.color),
                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
            )

            // 2. Target Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(goal.targetType.icon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isCompleted) Completed else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = goal.targetName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Target Type
            Text(
                text = stringResource(goal.targetType.label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Progress
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(if (isCompleted) R.drawable.ic_check else R.drawable.ic_target),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = progressColor
                )
                Text(
                    text = if(isCompleted) stringResource(R.string.relic_completed) else "${goal.currentQuantity} / ${goal.desiredQuantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (completionRatio > 0f || isCompleted) progressColor else Color.Unspecified
                )
            }

            // 5. Note
            if (!goal.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = goal.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalCardPreview() {
    PrimeTrackerTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GoalCard(
                goal = GoalDomain(
                    id = 1,
                    targetId = "excalibur_prime",
                    targetName = "Excalibur Prime",
                    targetType = GoalTargetType.PRIME_SET,
                    currentQuantity = 0,
                    desiredQuantity = 1,
                    status = GoalStatus.ACTIVE,
                    note = "Need to farm the blueprint and all components.",
                    tag = GoalTagDomain(
                        id = 1,
                        name = "Warframe",
                        icon = GoalIcons.SLASH,
                        color = 0xFF673AB7.toInt()
                    )
                ),
                onClick = {}
            )
            
            GoalCard(
                goal = GoalDomain(
                    id = 2,
                    targetId = "braton_prime",
                    targetName = "Braton Prime",
                    targetType = GoalTargetType.PRIME_SET,
                    currentQuantity = 1,
                    desiredQuantity = 1,
                    status = GoalStatus.COMPLETED,
                    note = "Completed goal sample",
                    tag = GoalTagDomain(
                        id = 2,
                        name = "Primary",
                        icon = GoalIcons.VIRAL,
                        color = 0xFF2196F3.toInt()
                    )
                ),
                onClick = {}
            )
        }
    }
}
