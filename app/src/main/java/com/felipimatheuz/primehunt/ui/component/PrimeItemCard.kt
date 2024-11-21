package com.felipimatheuz.primehunt.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.util.*
import com.felipimatheuz.primehunt.model.ItemComponent
import com.felipimatheuz.primehunt.model.ItemPart
import com.felipimatheuz.primehunt.model.PrimeItem
import com.felipimatheuz.primehunt.ui.theme.Black
import com.felipimatheuz.primehunt.viewmodel.OtherPrimeViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PrimeItemCard(primeItem: PrimeItem, viewModel: OtherPrimeViewModel) {
    val statusColorAnim =
        animateColorAsState(updateStatusColor(primeItem), label = "", animationSpec = tween(1000))
    var expanded by remember { mutableStateOf(false) }
    val boxBottomCorner = animateDpAsState(targetValue = if (expanded) 0.dp else 10.dp, label = "")
    Box(
        modifier = Modifier.fillMaxWidth().background(
            statusColorAnim.value,
            shape = RoundedCornerShape(10.dp, 10.dp, boxBottomCorner.value, boxBottomCorner.value)
        ).padding(8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(5.dp))
        ) {
            val (imageItem, textItem, imageDropBox, imgPrimeItemComp) = createRefs()
            GlideImage(
                model = urlPrimeItem[primeItem.name],
                contentDescription = null,
                contentScale = ContentScale.Fit,
                loading = placeholder(R.drawable.ic_lato_prime),
                failure = placeholder(R.drawable.ic_cross),
                modifier = Modifier.constrainAs(imageItem) {
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(parent.start, 8.dp)
                    bottom.linkTo(parent.bottom, 8.dp)
                }.size(60.dp)
            )
            Column(
                modifier = Modifier.constrainAs(textItem) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(imageItem.end, 8.dp)
                }) {
                Text(
                    stringResource(R.string.prime_template, primeItem.name),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    getPercent(primeItem).toString().plus("%")
                )
            }

            if (viewModel.hasAnotherPrimeItem(primeItem)) {
                Image(painter = painterResource(R.drawable.link_comp),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.constrainAs(imgPrimeItemComp) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(textItem.end, 16.dp)
                    })
            }

            Image(painter = painterResource(if (expanded) R.drawable.ic_up_arrow else R.drawable.ic_down_arrow),
                contentDescription = null,
                colorFilter = ColorFilter.tint(statusColorAnim.value),
                modifier = Modifier.constrainAs(imageDropBox) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, 8.dp)
                }.clickable {
                    expanded = !expanded
                })
        }
    }
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().background(
                MaterialTheme.colorScheme.onSecondary.copy(0.1f),
                shape = RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp)
            ).padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
        ) {
            val compCount = getCompCount(primeItem)
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
}

@Composable
private fun PrimeComponentsUI(
    viewModel: OtherPrimeViewModel,
    primeItem: PrimeItem,
    comp: ItemComponent?,
    compCount: Map<ItemPart?, Int>
) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val statusRes = rememberSaveable {
            mutableIntStateOf(viewModel.updateIconStatus(primeItem, comp?.part))
        }
        ConstraintLayout(modifier = Modifier.clickable {
            viewModel.togglePrimeItemComp(primeItem, comp)
            statusRes.intValue = viewModel.updateIconStatus(primeItem, comp?.part)
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
                    comp?.part?.icon ?: R.drawable.prime_blueprint
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

        val compName = getCompName(context, primeItem, comp)
        val relicList = getRelicList(compName)
        if (relicList.isNotEmpty()) {
            Column(modifier = Modifier.background(Black.copy(0.1f)).padding(8.dp)) {
                relicList.forEach { relic ->
                    val relicName = relic.name
                    val colorRes = getColorForeground(relic.rewards, compName)
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