package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.core.PrimeItem
import com.felipimatheuz.primehunt.ui.animation.AnimatedTransitionDialog
import com.felipimatheuz.primehunt.ui.theme.RelicBackground
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.util.*
import com.felipimatheuz.primehunt.viewmodel.PrimeSetDetailViewModel

@Composable
fun PrimeSetDetailScreen(setName: String?, onBack: () -> Unit) {

    AnimatedTransitionDialog(onDismissRequest = onBack) { dialogHelper ->
        ConstraintLayout(
            modifier = Modifier.fillMaxSize().background(
                color = MaterialTheme.colorScheme.surface
            )
        ) {
            val viewModel = PrimeSetDetailViewModel(LocalContext.current, setName)
            val primeSet = viewModel.primeSet.collectAsState()
            val (header, lcPrimeSet) = createRefs()

            Box(modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }.padding(8.dp)) {
                Text(
                    text = stringResource(R.string.prime_set_template, primeSet.value.warframe.name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                IconButton(
                    onClick = { dialogHelper::triggerAnimatedDismiss.invoke() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_cross),
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
            LazyColumn(modifier = Modifier.constrainAs(lcPrimeSet) {
                top.linkTo(header.bottom, 8.dp)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }, horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    PrimeItemUI(primeSet.value.warframe)
                    Spacer(Modifier.padding(16.dp))
                    PrimeItemUI(primeSet.value.primeItem1)
                    if (primeSet.value.primeItem2 != null) {
                        Spacer(Modifier.padding(16.dp))
                        PrimeItemUI(primeSet.value.primeItem2!!)
                    }
                    Spacer(Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun PrimeItemUI(primeItem: PrimeItem) {
    val context = LocalContext.current

    val compCount = getCompCount(primeItem)
    val compGroup = getCompGroup(primeItem)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.prime_template, primeItem.name),
            style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(compGroup) { comp ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ConstraintLayout {
                        val (imgStatus, imgComp, txtComp) = createRefs()
                        val statusRes = updateCompStatus(
                            if (comp != null) {
                                primeItem.components.filter { it.part == comp.part }
                                    .map { it.obtained }
                            } else
                                listOf(primeItem.blueprint))
                        if (statusRes != 0) {
                            Image(
                                painter = painterResource(statusRes),
                                contentDescription = null,
                                modifier = Modifier.constrainAs(imgStatus) {
                                    top.linkTo(imgComp.top, (-8).dp)
                                    end.linkTo(imgComp.end, (-8).dp)
                                }
                            )
                        }
                        Image(
                            painter = painterResource(
                                if (comp != null) {
                                    getItemPartIcon(comp.part)
                                } else {
                                    R.drawable.prime_blueprint
                                }
                            ),
                            contentDescription = if (comp != null) {
                                formatItemPartText(context, comp.part, compCount[comp.part])
                            } else {
                                stringResource(R.string.comp_blueprint)
                            },
                            modifier = Modifier.constrainAs(imgComp) {
                                top.linkTo(parent.top, 10.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                        )
                        Text(text = if (comp != null) {
                            formatItemPartText(context, comp.part, compCount[comp.part])
                        } else {
                            stringResource(R.string.comp_blueprint)
                        },
                            modifier = Modifier.constrainAs(txtComp) {
                                top.linkTo(imgComp.bottom, 8.dp)
                                start.linkTo(parent.start, 8.dp)
                                end.linkTo(parent.end, 8.dp)
                            })
                    }
                    Column(modifier = Modifier.background(RelicBackground).padding(8.dp)) {
                        val searchText = getCompName(context, primeItem, comp)
                        val relicList = getRelicList(searchText)
                        relicList.forEach { relic ->
                            val relicName = relic.name.substring(0, relic.name.lastIndexOf(" "))
                            val colorRes = getColorForeground(relic.rewards, searchText)
                            Text(
                                text = relicName,
                                color = colorRes ?: Color.Unspecified,
                                textDecoration = if (relic.vaulted) TextDecoration.LineThrough else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PrimeSetDetailScreenPreview() {
    WarframeprimehuntTheme {
        PrimeSetDetailScreen("Wisp") {}
    }
}