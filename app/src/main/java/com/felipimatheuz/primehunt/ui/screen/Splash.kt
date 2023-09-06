package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.state.LoadState
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.viewmodel.SplashViewModel

@Composable
fun SplashScreen(onReady: () -> Unit) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (loadItem) = createRefs()
        Column(modifier = Modifier.constrainAs(loadItem) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, horizontalAlignment = Alignment.CenterHorizontally) {
            val viewModel: SplashViewModel = viewModel()
            val loadState = viewModel.loadState.collectAsState()

            when (loadState.value) {
                LoadState.LoadRelic -> ShowLoading(R.string.loading_content, viewModel)
                LoadState.LoadSet -> ShowLoading(R.string.check_updates, viewModel)
                is LoadState.Error -> ShowError(viewModel, (loadState.value as LoadState.Error).lastTextRes)
                LoadState.Ready -> onReady()
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ShowLoading(textRes: Int, viewModel: SplashViewModel) {
    GlideImage(model = R.drawable.fx_loading, contentDescription = null)
    Text(text = stringResource(textRes), style = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.onSurface
    ))
    viewModel.loadResource(textRes)
}

@Composable
private fun ShowError(viewModel: SplashViewModel, textRes: Int) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Row {
                Image(
                    painter = painterResource(R.drawable.wifi_off), "",
                    modifier = Modifier.size(32.dp)
                )
                Text(text = stringResource(R.string.connection_failed), modifier = Modifier.padding(start = 8.dp))
            }
        },
        text = {
            Text(stringResource(R.string.connection_failed_message), color = MaterialTheme.colorScheme.onSurface)
        },
        confirmButton = {
            Button(onClick = {
                viewModel.loadResource(textRes)
            }, content = {
                Text(text = stringResource(R.string.connection_retry))
            })
        },
        shape = RoundedCornerShape(10.dp)
    )
}

@Preview
@Composable
fun SplashScreenPreview() {
    WarframeprimehuntTheme {
        SplashScreen {}
    }
}