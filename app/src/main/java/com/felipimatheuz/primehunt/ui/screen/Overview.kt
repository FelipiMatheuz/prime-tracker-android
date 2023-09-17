package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.PrimeType
import com.felipimatheuz.primehunt.ui.component.PrimeChart
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.viewmodel.OverViewModel


@Composable
fun OverviewScreen(padding: PaddingValues, update: Boolean, changeUpdate: () -> Unit) {
    ConstraintLayout(modifier = Modifier.padding(padding).fillMaxSize()) {
        val viewModel: OverViewModel = viewModel()
        var showDetail by rememberSaveable { mutableStateOf(false) }
        val (overallChart, warframeChart, primaryChart, secondaryChart, meleeChart, otherChart, toggleOverview) = createRefs()
        val setPrimeItems = viewModel.loadSet(LocalContext.current, update)
        val otherPrimeItems = viewModel.loadOther(LocalContext.current, update)
        changeUpdate()
        if (!showDetail) {
            PrimeChart(
                labelRes = R.string.overall,
                data = setPrimeItems + otherPrimeItems,
                modifier = Modifier.constrainAs(overallChart) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        } else {
            PrimeChart(
                labelRes = R.string.overview_warframes,
                data = (setPrimeItems + otherPrimeItems).filter { it.type == PrimeType.WARFRAME },
                modifier = Modifier.constrainAs(warframeChart) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                scaleSize = 0.5f
            )

            PrimeChart(
                labelRes = R.string.overview_primary,
                data = (setPrimeItems + otherPrimeItems).filter { it.type == PrimeType.PRIMARY },
                modifier = Modifier.constrainAs(primaryChart) {
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(parent.start, 16.dp)
                },
                scaleSize = 0.5f
            )
            PrimeChart(
                labelRes = R.string.overview_secondary,
                data = (setPrimeItems + otherPrimeItems).filter { it.type == PrimeType.SECONDARY },
                modifier = Modifier.constrainAs(secondaryChart) {
                    top.linkTo(parent.top, 8.dp)
                    end.linkTo(parent.end, 16.dp)
                },
                scaleSize = 0.5f
            )
            PrimeChart(
                labelRes = R.string.overview_melee,
                data = (setPrimeItems + otherPrimeItems).filter { it.type == PrimeType.MELEE },
                modifier = Modifier.constrainAs(meleeChart) {
                    start.linkTo(parent.start, 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                },
                scaleSize = 0.5f
            )
            PrimeChart(
                labelRes = R.string.overview_other,
                data = (setPrimeItems + otherPrimeItems).filter { it.type == PrimeType.OTHER },
                modifier = Modifier.constrainAs(otherChart) {
                    end.linkTo(parent.end, 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                },
                scaleSize = 0.5f
            )
        }
        OutlinedButton(onClick = { showDetail = !showDetail },
            modifier = Modifier.constrainAs(toggleOverview) {
                bottom.linkTo(parent.bottom, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text(
                text = stringResource(
                    if (showDetail) {
                        R.string.detailed
                    } else {
                        R.string.overall
                    }
                ), style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.onSecondary,
                        offset = Offset(2.0f, 2.0f),
                        blurRadius = 2f
                    )
                )
            )
        }
    }
}

@Preview
@Composable
fun OverviewScreenPreview() {
    WarframeprimehuntTheme {
        OverviewScreen(PaddingValues(10.dp), false) { }
    }
}