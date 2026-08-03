package com.felipimatheuz.primehunt.ui.screen.relic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.ui.screen.components.shimmer
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@Composable
fun RelicSkeleton(
    paddingValues: PaddingValues = PaddingValues()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Search Bar Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(28.dp)
                )
                .shimmer(RoundedCornerShape(28.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .shimmer(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .shimmer(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .shimmer(CircleShape)
                )
            }
        }

        // Segmented Control Skeleton (3 buttons)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
                    .shimmer(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RectangleShape
                    )
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    )
                    .shimmer(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
            )
        }

        // Era Sections
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(3) {
                EraSectionSkeleton()
            }
        }
    }
}

@Composable
private fun EraSectionSkeleton() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Era Title Placeholder
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .width(80.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                .shimmer(RoundedCornerShape(4.dp))
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false // Prevent scrolling in skeleton
        ) {
            items(3) {
                RelicCardSkeleton()
            }
        }
    }
}

@Composable
private fun RelicCardSkeleton() {
    Box(
        modifier = Modifier
            .width(120.dp)
            .padding(top = 20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Relic Name
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                            .shimmer(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Forma Icon Placeholder
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .shimmer(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Missing Indicator
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                        .shimmer(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Goal Tags Icon + Count
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .shimmer(CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(14.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                            .shimmer(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        // Era Icon Placeholder (Offset half outside)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .shimmer(CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RelicSkeletonPreview() {
    PrimeTrackerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RelicSkeleton()
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RelicSkeletonDarkPreview() {
    PrimeTrackerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RelicSkeleton()
        }
    }
}
