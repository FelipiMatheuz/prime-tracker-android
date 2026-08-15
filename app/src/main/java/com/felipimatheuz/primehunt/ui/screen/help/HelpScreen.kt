package com.felipimatheuz.primehunt.ui.screen.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.screen.components.TipItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun HelpScreen(paddingValues: PaddingValues) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var scrollJob by remember { mutableStateOf<Job?>(null) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
    ) {
        HelpIndex(
            sections = HelpContentProvider.helpSections,
            onChipClick = { index ->
                scrollJob?.cancel()
                scrollJob = scope.launch {
                    listState.animateScrollToItem(index)
                }
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            itemsIndexed(HelpContentProvider.helpSections) { index, section ->
                HelpSectionItem(section)
                if (index < HelpContentProvider.helpSections.size - 1) {
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpIndex(
    sections: List<HelpSection>,
    onChipClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(sections) { index, section ->
            if (index == 0) {
                Spacer(modifier = Modifier.width(16.dp))
            }
            val chipInteraction = remember { MutableInteractionSource() }
            AssistChip(
                onClick = { onChipClick(index) },
                interactionSource = chipInteraction,
                label = { Text(stringResource(section.title)) },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                border = AssistChipDefaults.assistChipBorder(
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    enabled = true
                ),
                modifier = Modifier.pressScale(
                    interactionSource = chipInteraction,
                    intensity = PressIntensity.VERY_SUBTLE
                )
            )
            if (index == sections.size - 1) {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
private fun HelpSectionItem(section: HelpSection) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            section.icon?.let {
                Icon(
                    painter = painterResource(id = section.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = stringResource(section.title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        section.description?.let {
            Text(
                text = stringResource(it),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        section.subSections.forEach { subSection ->
            HelpSubSectionItem(subSection)
            Spacer(modifier = Modifier.height(16.dp))
        }

        section.tip?.let {
            TipItem(stringResource(it))
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (section.faqItems.isNotEmpty()) {
            FaqSectionItem(section.faqItems)
        }
    }
}

@Composable
private fun HelpSubSectionItem(subSection: HelpSubSection) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(subSection.title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(subSection.description),
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        subSection.tip?.let {
            Spacer(modifier = Modifier.height(12.dp))
            TipItem(stringResource(it))
        }
    }
}

@Composable
private fun FaqSectionItem(items: List<FaqItem>) {
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { index, item ->
            val isExpanded = expandedStates[index] ?: false
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expandedStates[index] = !isExpanded }
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(item.question),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "arrowRotation")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_show_hide),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.rotate(rotation)
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(item.answer),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            if (index < items.size - 1) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}
