package com.felipimatheuz.primehunt.ui.screen.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GoalTagChip(
    modifier: Modifier = Modifier,
    text: String? = null,
    @DrawableRes iconRes: Int? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    isNeutral: Boolean = false,
    isFaded: Boolean = !isNeutral
) {
    val backgroundColor = if (isFaded) color.copy(alpha = 0.2f) else color
    val contentColor = if (isFaded) {
        color
    } else {
        if (color.luminance() > 0.5f) Color.Black else Color.White
    }

    val shape = if (text == null) CircleShape else RoundedCornerShape(16.dp)

    Surface(
        color = backgroundColor,
        shape = shape,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (text == null) 4.dp else 8.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = contentColor
                )
            }
            
            if (text != null) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
