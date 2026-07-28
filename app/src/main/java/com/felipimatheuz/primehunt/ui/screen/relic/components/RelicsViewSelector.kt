package com.felipimatheuz.primehunt.ui.screen.relic.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.ui.viewmodel.relic.RelicsView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelicsViewSelector(
    selectedView: RelicsView,
    onViewChange: (RelicsView) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        RelicsView.entries.forEachIndexed { index, view ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = RelicsView.entries.size
                ),
                onClick = { onViewChange(view) },
                selected = selectedView == view
            ) {
                Text(stringResource(view.displayNameRes))
            }
        }
    }
}
