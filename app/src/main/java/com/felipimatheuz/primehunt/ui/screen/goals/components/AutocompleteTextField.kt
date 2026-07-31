package com.felipimatheuz.primehunt.ui.screen.goals.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.felipimatheuz.primehunt.domain.model.TargetDomain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<TargetDomain>,
    onSuggestionSelected: (TargetDomain) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(suggestions) {
        expanded = suggestions.isNotEmpty()
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it && suggestions.isNotEmpty() }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                singleLine = true,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions
            )

            if (suggestions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    suggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            text = { Text(suggestion.name) },
                            onClick = {
                                onSuggestionSelected(suggestion)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
