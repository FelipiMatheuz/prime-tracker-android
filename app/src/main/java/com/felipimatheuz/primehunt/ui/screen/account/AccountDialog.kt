package com.felipimatheuz.primehunt.ui.screen.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.AuthState
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.viewmodel.account.AccountIntent
import com.felipimatheuz.primehunt.ui.viewmodel.account.AccountViewModel

@Composable
fun AccountDialog(
    onDismissRequest: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val authState = state.authState
    val user = authState.currentUser

    if (state.showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(AccountIntent.CancelSignOut) },
            title = { Text(stringResource(R.string.cloud_disconnect)) },
            text = { Text(stringResource(R.string.cloud_sign_out_announcement_warning)) },
            confirmButton = {
                val confirmInteraction = remember { MutableInteractionSource() }
                TextButton(
                    onClick = { viewModel.onIntent(AccountIntent.ConfirmSignOut) },
                    interactionSource = confirmInteraction,
                    modifier = Modifier.pressScale(confirmInteraction, PressIntensity.VERY_SUBTLE)
                ) {
                    Text(
                        stringResource(R.string.cloud_disconnect),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                val cancelInteraction = remember { MutableInteractionSource() }
                TextButton(
                    onClick = { viewModel.onIntent(AccountIntent.CancelSignOut) },
                    interactionSource = cancelInteraction,
                    modifier = Modifier.pressScale(cancelInteraction, PressIntensity.VERY_SUBTLE)
                ) {
                    Text(stringResource(R.string.cloud_danger_zone_cancel))
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_google),
                            contentDescription = stringResource(R.string.cloud_google_logo_description),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.cloud_account),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val dismissInteraction = remember { MutableInteractionSource() }
                    IconButton(
                        onClick = onDismissRequest,
                        interactionSource = dismissInteraction,
                        modifier = Modifier
                            .size(28.dp)
                            .pressScale(dismissInteraction, PressIntensity.SUBTLE)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.cloud_danger_zone_cancel),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val errorText = state.errorMessage ?: (authState as? AuthState.AuthError)?.message
                AnimatedVisibility(visible = errorText != null) {
                    if (errorText != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .background(
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = errorText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                if (authState.isAuthenticated && user != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = user.name ?: stringResource(R.string.cloud_status_connected),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!user.email.isNullOrEmpty()) {
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val signOutInteraction = remember { MutableInteractionSource() }
                        OutlinedButton(
                            onClick = { viewModel.onIntent(AccountIntent.RequestSignOut) },
                            enabled = !state.isLoading,
                            interactionSource = signOutInteraction,
                            modifier = Modifier
                                .align(Alignment.End)
                                .pressScale(
                                    signOutInteraction,
                                    PressIntensity.VERY_SUBTLE,
                                    enabled = !state.isLoading
                                )
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(stringResource(R.string.cloud_disconnect))
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.cloud_account_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val signInInteraction = remember { MutableInteractionSource() }
                        Button(
                            onClick = { viewModel.onIntent(AccountIntent.SignIn) },
                            enabled = !state.isLoading,
                            interactionSource = signInInteraction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(
                                    signInInteraction,
                                    PressIntensity.VERY_SUBTLE,
                                    enabled = !state.isLoading
                                )
                        ) {
                            if (state.isLoading || authState is AuthState.Authenticating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(stringResource(R.string.cloud_sign_in_google))
                            }
                        }
                    }
                }
            }
        }
    }
}
