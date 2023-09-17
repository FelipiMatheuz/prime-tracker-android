package com.felipimatheuz.primehunt.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.ui.theme.High
import com.felipimatheuz.primehunt.ui.theme.Low
import com.felipimatheuz.primehunt.business.util.getPercent
import com.felipimatheuz.primehunt.business.util.updateStatusColor
import com.felipimatheuz.primehunt.viewmodel.PrimeSetViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PrimeSetCard(primeSet: PrimeSet, viewModel: PrimeSetViewModel, goToDetails: () -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val backAnim by animateDpAsState(targetValue = if (offsetX != 0f) offsetX.dp else 0.dp, label = "")

    Box {
        Image(
            painter = painterResource(R.drawable.ic_check), contentDescription = null,
            colorFilter = ColorFilter.tint(High),
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp)
        )
        Image(
            painter = painterResource(R.drawable.ic_cross), contentDescription = null,
            colorFilter = ColorFilter.tint(Low),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)
        )
        Box(modifier = Modifier.pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDrag = { change, offset ->
                    change.consume()
                    if (offsetX <= 100 && offsetX >= -100) {
                        offsetX += offset.x
                    }
                },
                onDragEnd = {
                    if (offsetX >= 100) {
                        viewModel.togglePrimeSet(primeSet, true)
                    } else if (offsetX <= -100) {
                        viewModel.togglePrimeSet(primeSet, false)
                    }
                    offsetX = 0f
                }
            )
        }.offset(x = backAnim).clickable {
            goToDetails()
        }) {
            val statusColorAnim = animateColorAsState(updateStatusColor(primeSet), label = "", animationSpec = tween(1000))
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    statusColorAnim.value,
                    shape = RoundedCornerShape(10.dp)
                ).padding(8.dp)
            ) {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(5.dp))
                ) {
                    val (layoutSet, layoutItems) = createRefs()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.constrainAs(layoutSet) {
                            top.linkTo(parent.top, 8.dp)
                            start.linkTo(parent.start, 8.dp)
                            bottom.linkTo(parent.bottom, 8.dp)
                        }) {
                        GlideImage(
                            model = primeSet.imgLink,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            loading = placeholder(R.drawable.ic_excalibur_prime),
                            failure = placeholder(R.drawable.ic_cross),
                            modifier = Modifier.size(100.dp)
                        )
                        Text(text = stringResource(R.string.prime_set_template, primeSet.setName))
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.constrainAs(layoutItems) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(layoutSet.end)
                            end.linkTo(parent.end)
                        }) {
                        primeSet.primeItems.forEach {
                            Row {
                                Text(stringResource(R.string.prime_template, it.name))
                                Text(
                                    getPercent(it).toString().plus("%"),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        Text(
                            stringResource(viewModel.getStatusTextRes(primeSet.status)),
                            modifier = Modifier.padding(top = 16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}