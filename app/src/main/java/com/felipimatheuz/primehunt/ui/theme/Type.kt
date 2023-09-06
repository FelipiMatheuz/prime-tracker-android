package com.felipimatheuz.primehunt.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.felipimatheuz.primehunt.R

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    displayMedium = Typography().displayMedium.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    displaySmall = Typography().displaySmall.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),

    headlineLarge = Typography().headlineLarge.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),

    titleLarge = Typography().titleLarge.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    titleMedium = Typography().titleMedium.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    titleSmall = Typography().titleSmall.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),

    bodyLarge = Typography().bodyLarge.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    bodySmall = Typography().bodySmall.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),

    labelLarge = Typography().labelLarge.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    labelMedium = Typography().labelMedium.copy(fontFamily = FontFamily(Font(R.font.andika_regular))),
    labelSmall = Typography().labelSmall.copy(fontFamily = FontFamily(Font(R.font.andika_regular)))
)