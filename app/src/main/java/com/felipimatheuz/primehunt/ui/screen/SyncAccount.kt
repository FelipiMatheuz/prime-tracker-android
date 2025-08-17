package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.MenuDialogState
import com.felipimatheuz.primehunt.ui.theme.Low
import com.felipimatheuz.primehunt.viewmodel.SyncViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SyncAccountScreen(
    info: MenuDialogState,
    onBack: () -> Unit,
    viewModel: SyncViewModel = hiltViewModel()
) {
    Dialog(onDismissRequest = {}) {
        val googleCredential = viewModel.getCredentials()
        var error by remember { mutableStateOf(false) }
        val state by viewModel.state.collectAsState()

        ConstraintLayout(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxWidth()
                .padding(16.dp)
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

            Box(
                modifier = Modifier
                    .constrainAs(txtState) {
                        top.linkTo(txtContent.bottom, 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .background(
                        MaterialTheme.colorScheme.onSecondary.copy(0.3f),
                        RoundedCornerShape(5.dp)
                    )
            ) {
                Text(
                    text = viewModel.getPromptMessage(
                        state,
                        { error = false },
                        { error = true }),
                    color = if (error) Low else MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(8.dp)
                )
            }
            if (googleCredential.getSignedInUser() != null) {
                val (txtLogged, btnImport, btnExport, btnSignOut) = createRefs()
                Text(
                    text = stringResource(
                        R.string.logged_user,
                        googleCredential.getSignedInUser()!!.name ?: ""
                    ),
                    modifier = Modifier.constrainAs(txtLogged) {
                        top.linkTo(txtState.bottom, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    })
                OutlinedButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.importCheckList(googleCredential.getSignedInUser()!!.userId)
                    }
                }, modifier = Modifier.constrainAs(btnImport) {
                    top.linkTo(txtLogged.bottom, 8.dp)
                    start.linkTo(parent.start)
                }) {
                    Text(text = stringResource(R.string.sync_import))
                }
                OutlinedButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.exportCheckList(googleCredential.getSignedInUser()!!.userId)
                    }
                }, modifier = Modifier.constrainAs(btnExport) {
                    top.linkTo(txtLogged.bottom, 8.dp)
                    end.linkTo(parent.end)
                }) {
                    Text(text = stringResource(R.string.sync_export))
                }
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        googleCredential.signOut()
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
                        viewModel.onSignInResult(googleCredential.signUpCredentialManager())
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