package com.felipimatheuz.primehunt.ui.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

@Composable
fun AnimatedLoad() {

    val transition = rememberInfiniteTransition(label = "color_change")
    val colorAnimation = transition.animateColor(
        Color.White,
        Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val colorFilter = ColorFilter.tint(colorAnimation.value, BlendMode.Modulate)

    ConstraintLayout(modifier = Modifier.wrapContentSize()) {
        val (body, body2, piece1, piece2, piece3, piece4) = createRefs()
        Image(
            painterResource(R.drawable.cephalon_body),
            contentDescription = null,
            modifier = Modifier.constrainAs(body) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }.padding(8.dp)
        )
        Image(
            painterResource(R.drawable.cephalon_body),
            contentDescription = null,
            modifier = Modifier.constrainAs(body2) {
                top.linkTo(body.top)
                bottom.linkTo(body.bottom)
                start.linkTo(body.start)
                end.linkTo(body.end)
            }, colorFilter = colorFilter
        )
        PieceImage(
            modifier = Modifier.constrainAs(piece1) {
                top.linkTo(body.top)
                start.linkTo(body.start)
            }, colorFilter = colorFilter)
        PieceImage(
            modifier = Modifier.constrainAs(piece2) {
                top.linkTo(body.top)
                end.linkTo(body.end)
            }, colorFilter = colorFilter)
        PieceImage(
            modifier = Modifier.constrainAs(piece3) {
                bottom.linkTo(body.bottom)
                start.linkTo(body.start)
            }, colorFilter = colorFilter)
        PieceImage(
            modifier = Modifier.constrainAs(piece4) {
                bottom.linkTo(body.bottom)
                end.linkTo(body.end)
            }, colorFilter = colorFilter)
    }
}

@Composable
private fun PieceImage(modifier: Modifier, colorFilter: ColorFilter) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotateAnim by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing)
        ), label = ""
    )

    Image(
        painterResource(R.drawable.cephalon_piece),
        contentDescription = null,
        modifier = modifier.rotate(rotateAnim),
        colorFilter = colorFilter
    )
}

@Preview(widthDp = 150, heightDp = 150)
@Composable
fun AnimatedLoadPreview() {
    WarframeprimehuntTheme {
        AnimatedLoad()
    }
}