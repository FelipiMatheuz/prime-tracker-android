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
import androidx.compose.ui.res.stringResource
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.TargetDomain
import com.felipimatheuz.primehunt.domain.model.enums.GoalTargetType
import com.felipimatheuz.primehunt.domain.model.enums.PrimePartType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<TargetDomain>,
    onSuggestionSelected: (TargetDomain) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(suggestions) {
        expanded = suggestions.isNotEmpty() && enabled
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it && suggestions.isNotEmpty() && enabled }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                enabled = enabled,
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
                        val displayName = internationalizeTargetName(suggestion)
                        DropdownMenuItem(
                            text = { Text(displayName) },
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

@Composable
private fun internationalizeTargetName(target: TargetDomain): String {
    return when (target.type) {
        GoalTargetType.PRIME_SET, GoalTargetType.RELIC -> target.name
        GoalTargetType.OTHER -> {
            if (target.id == "forma_blueprint") {
                stringResource(R.string.forma_blueprint)
            } else {
                target.name
            }
        }
        GoalTargetType.PRIME_PART -> {
            normalizeRewardName(target.name)
        }
    }
}

@Composable
private fun normalizeRewardName(name: String): String {
    val primeIndex = name.indexOf("Prime")
    if (primeIndex == -1) {
        return name
    }

    val immutablePart = name.substring(0, primeIndex + "Prime".length).trim()
    val translatablePart = name.substring(primeIndex + "Prime".length).trim()
    val primePart = PrimePartType.fromString(translatablePart)
    if (primePart == PrimePartType.PRIME_SET) {
        return "$immutablePart: $translatablePart"
    }

    return buildString {
        append(immutablePart.replaceFirstChar { it.uppercase() })
        append(": ")
        append(stringResource(primePart.text))
    }
}
