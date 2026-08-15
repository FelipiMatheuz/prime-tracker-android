package com.felipimatheuz.primehunt.ui.screen.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PrimeSegmentedSelector(
    options: List<T>,
    selectedOption: T,
    onOptionClick: (T) -> Unit,
    labelProvider: (T) -> Int,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val interactionSource = remember(option) { MutableInteractionSource() }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onOptionClick(option) },
                selected = selectedOption == option,
                interactionSource = interactionSource,
                modifier = Modifier.pressScale(
                    interactionSource = interactionSource,
                    intensity = PressIntensity.VERY_SUBTLE,
                    enabled = selectedOption != option
                )
            ) {
                Text(stringResource(labelProvider(option)))
            }
        }
    }
}
