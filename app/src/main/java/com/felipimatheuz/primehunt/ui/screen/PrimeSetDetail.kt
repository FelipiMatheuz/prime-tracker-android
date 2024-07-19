package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.util.*
import com.felipimatheuz.primehunt.model.ItemComponent
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.ui.theme.Black
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.viewmodel.PrimeSetDetailViewModel

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrimeSetDetailScreen(setName: String?, onBack: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onBack, sheetState = sheetState) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val viewModel = PrimeSetDetailViewModel(LocalContext.current, setName)
            val primeSet by viewModel.primeSet.collectAsState()
            val (header, lcPrimeSet) = createRefs()

            Box(modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top, 8.dp)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                width = Dimension.fillToConstraints
            }) {
                GlideImage(
                    model = primeSet.imgLink,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(200.dp)
                )
                Text(
                    text = stringResource(R.string.prime_set_template, primeSet.setName),
                    style = MaterialTheme.typography.titleLarge
                        .copy(
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.onPrimary,
                                offset = Offset(2.0f, 2.0f),
                                blurRadius = 2f
                            )
                        ),
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            Box(modifier = Modifier.constrainAs(lcPrimeSet) {
                top.linkTo(header.bottom)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(primeSet.primeItems) { index, it ->
                        if (index == 0)
                            Spacer(modifier = Modifier.padding(bottom = 8.dp))
                        PrimeItemUI(it, viewModel)
                        if (index == primeSet.primeItems.size - 1)
                            Spacer(modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PrimeItemUI(primeItem: PrimeItem, viewModel: PrimeSetDetailViewModel) {
    val compCount = getCompCount(primeItem)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.prime_template, primeItem.name),
            style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item(primeItem.blueprint) {
                PrimeComponentsUI(viewModel, primeItem, null, compCount)
            }
            items(primeItem.components.distinctBy { it.part }) { comp ->
                PrimeComponentsUI(viewModel, primeItem, comp, compCount)
            }
        }
    }
}

@Composable
private fun PrimeComponentsUI(
    viewModel: PrimeSetDetailViewModel,
    primeItem: PrimeItem,
    comp: ItemComponent?,
    compCount: Map<ItemPart?, Int>
) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val statusRes = rememberSaveable {
            mutableIntStateOf(viewModel.updateIconStatus(primeItem.name, comp?.part))
        }
        ConstraintLayout(modifier = Modifier.clickable {
            viewModel.togglePrimeItem(primeItem.name, comp?.part)
            statusRes.intValue = viewModel.updateIconStatus(primeItem.name, comp?.part)
        }) {
            val (imgStatus, imgComp, txtComp) = createRefs()
            if (statusRes.intValue != 0) {
                Image(
                    painter = painterResource(statusRes.intValue),
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
        Column(modifier = Modifier.background(Black.copy(0.1f)).padding(8.dp)) {
            val searchText = getCompName(context, primeItem, comp)
            val relicList = getRelicList(searchText)
            relicList.forEach { relic ->
                val colorRes = getColorForeground(relic.rewards, searchText)
                Text(
                    text = relic.name,
                    color = colorRes ?: Color.Unspecified,
                    textDecoration = if (relic.vaulted) TextDecoration.LineThrough else null
                )
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