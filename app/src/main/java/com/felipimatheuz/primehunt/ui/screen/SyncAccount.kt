package com.felipimatheuz.primehunt.ui.screen

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.external.GoogleAuth
import com.felipimatheuz.primehunt.business.state.MenuDialogState
import com.felipimatheuz.primehunt.business.state.SyncState
import com.felipimatheuz.primehunt.ui.theme.Low
import com.felipimatheuz.primehunt.viewmodel.SyncViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SyncAccountScreen(info: MenuDialogState, onBack: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        val context = LocalContext.current
        val googleAuth by lazy {
            GoogleAuth(Identity.getSignInClient(context))
        }
        val viewModel = SyncViewModel()
        val launcher = resultSignInLauncher(googleAuth, viewModel)
        var error by remember { mutableStateOf(false) }
        val state by viewModel.state.collectAsState()

        ConstraintLayout(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            ).fillMaxWidth().padding(16.dp)
        ) {
            val (imgHeader, txtHeader, btnClose, txtContent, txtState) = createRefs()
            Image(
                painter = painterResource(info.icon),
                contentDescription = null,
                modifier = Modifier.constrainAs(imgHeader) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                })
            Text(
                stringResource(info.title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.constrainAs(txtHeader) {
                    top.linkTo(imgHeader.top)
                    bottom.linkTo(imgHeader.bottom)
                    start.linkTo(imgHeader.end, 8.dp)
                })
            IconButton(
                onClick = { onBack() },
                modifier = Modifier.constrainAs(btnClose) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(
                    painterResource(R.drawable.ic_cross),
                    contentDescription = stringResource(R.string.close),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = stringResource(info.content),
                modifier = Modifier.constrainAs(txtContent) {
                    top.linkTo(btnClose.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                })

            Box(modifier = Modifier.constrainAs(txtState) {
                top.linkTo(txtContent.bottom, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }.background(MaterialTheme.colorScheme.onSecondary.copy(0.3f), RoundedCornerShape(5.dp))) {
                val notification =
                    getNotificationState(state, { error = false }, { error = true }, { viewModel.resetState() })
                Text(
                    if (notification != null) stringResource(notification) else "",
                    color = if (error) Low else MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(8.dp)
                )
            }
            if (googleAuth.getSignedInUser() != null) {
                val (txtLogged, btnImport, btnExport, btnSignOut) = createRefs()
                Text(
                    text = stringResource(R.string.logged_user, googleAuth.getSignedInUser()!!.name ?: ""),
                    modifier = Modifier.constrainAs(txtLogged) {
                        top.linkTo(txtState.bottom, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    })
                OutlinedButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.importCheckList(context, googleAuth.getSignedInUser()!!.userId)
                    }
                }, modifier = Modifier.constrainAs(btnImport) {
                    top.linkTo(txtLogged.bottom, 8.dp)
                    start.linkTo(parent.start)
                }) {
                    Text(text = stringResource(R.string.sync_import))
                }
                OutlinedButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.exportCheckList(context, googleAuth.getSignedInUser()!!.userId)
                    }
                }, modifier = Modifier.constrainAs(btnExport) {
                    top.linkTo(txtLogged.bottom, 8.dp)
                    end.linkTo(parent.end)
                }) {
                    Text(text = stringResource(R.string.sync_export))
                }
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        googleAuth.signOut()
                        viewModel.logOut()
                    }
                }, modifier = Modifier.constrainAs(btnSignOut) {
                    top.linkTo(btnExport.bottom, 8.dp)
                    end.linkTo(parent.end)
                }) {
                    Text(text = stringResource(R.string.sync_sign_out))
                }
            } else {
                val btnSignIn = createRef()
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val signInIntentSender = googleAuth.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }, modifier = Modifier.constrainAs(btnSignIn) {
                    top.linkTo(txtState.bottom, 16.dp)
                    end.linkTo(parent.end)
                }) {
                    Text(text = stringResource(R.string.sync_sign_in))
                }
            }
        }
    }
}

@Composable
private fun resultSignInLauncher(
    googleAuth: GoogleAuth,
    viewModel: SyncViewModel
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult(),
    onResult = { result ->
        if (result.resultCode == RESULT_OK) {
            CoroutineScope(Dispatchers.IO).launch {
                val signInResult = googleAuth.signInWithIntent(
                    intent = result.data ?: return@launch
                )
                viewModel.onSignInResult(signInResult)
            }
        }
    }
)

fun getNotificationState(state: SyncState, onSuccess: () -> Unit, onError: () -> Unit, reset: () -> Unit): Int? {
    CoroutineScope(Dispatchers.IO).launch {
        delay(1500)
        reset()
    }
    return when (state) {
        SyncState.SuccessSignIn -> {
            onSuccess()
            R.string.sync_signin_success
        }

        SyncState.SuccessSignOut -> {
            onSuccess()
            R.string.sync_signout_success
        }

        SyncState.SuccessImport -> {
            onSuccess()
            R.string.sync_imported
        }

        SyncState.SuccessExport -> {
            onSuccess()
            R.string.sync_exported
        }

        is SyncState.Error -> {
            onError()
            R.string.sync_sign_error
        }

        SyncState.None -> null
    }
}