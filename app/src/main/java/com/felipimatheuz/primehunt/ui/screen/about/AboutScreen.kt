package com.felipimatheuz.primehunt.ui.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.BuildConfig
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.ui.util.IntentUtils
import com.felipimatheuz.primehunt.ui.viewmodel.about.AboutViewModel

@Composable
fun AboutScreen(
    paddingValues: PaddingValues,
    snackBarHostState: SnackbarHostState,
    viewModel: AboutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDonationDialog by remember { mutableStateOf(false) }
    val isUpdateAvailable by viewModel.updateState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    )
    {
        item {
            AboutHeader(isUpdateAvailable, snackBarHostState)
        }

        item {
            AboutSection(
                title = stringResource(R.string.about_developer_title),
                content = {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.cs_logo),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = stringResource(R.string.about_developer_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.about_developer_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            )
        }

        item {
            val contactEmail = stringResource(R.string.about_contact_email)
            val emailSubject = stringResource(R.string.about_email_subject)
            val bugReportUrl = stringResource(R.string.about_bug_report_url)

            AboutSection(
                title = stringResource(R.string.about_support_title),
                content = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val emailButtonInteraction = remember { MutableInteractionSource() }
                        val bugButtonInteraction = remember { MutableInteractionSource() }
                        OutlinedButton(
                            onClick = {
                                IntentUtils.contactEmail(
                                    context,
                                    contactEmail,
                                    emailSubject,
                                    snackBarHostState,
                                    scope
                                )
                            },
                            interactionSource = emailButtonInteraction,
                            modifier = Modifier.pressScale(
                                emailButtonInteraction,
                                PressIntensity.VERY_SUBTLE
                            )
                        ) {
                            Text(stringResource(R.string.about_contact))
                        }
                        OutlinedButton(
                            onClick = {
                                IntentUtils.openUrl(context, bugReportUrl, snackBarHostState, scope)
                            },
                            interactionSource = bugButtonInteraction,
                            modifier = Modifier.pressScale(
                                bugButtonInteraction,
                                PressIntensity.VERY_SUBTLE
                            )
                        ) {
                            Text(stringResource(R.string.about_report_bug))
                        }
                    }
                }
            )
        }

        item {
            val repoUrl = stringResource(R.string.about_repo_url)
            AboutSection(
                title = stringResource(R.string.about_open_source_title),
                content = {
                    Column {
                        Text(
                            text = stringResource(R.string.about_license),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val repositoryButtonInteraction = remember { MutableInteractionSource() }
                        Button(
                            onClick = {
                                IntentUtils.openUrl(context, repoUrl, snackBarHostState, scope)
                            },
                            interactionSource = repositoryButtonInteraction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(
                                    repositoryButtonInteraction,
                                    PressIntensity.VERY_SUBTLE
                                )
                        ) {
                            Text(stringResource(R.string.about_repository))
                        }
                    }
                }
            )
        }

        item {
            AboutSection(
                title = stringResource(R.string.about_support_dev_title),
                content = {
                    Column {
                        Text(
                            text = stringResource(R.string.about_support_dev_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val supportButtonInteraction = remember { MutableInteractionSource() }
                        Button(
                            onClick = { showDonationDialog = true },
                            interactionSource = supportButtonInteraction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScale(
                                    supportButtonInteraction,
                                    PressIntensity.VERY_SUBTLE
                                )
                        ) {
                            Text(stringResource(R.string.about_support_dev_button))
                        }
                    }
                }
            )
        }

        item {
            AboutSection(
                title = stringResource(R.string.about_credits_title),
                showDivider = false,
                content = {
                    Text(
                        text = stringResource(R.string.about_credits_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 18.sp
                    )
                }
            )
        }

        item {
            Text(
                text = stringResource(R.string.about_footer_thanks),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showDonationDialog) {
        val donationUrl = stringResource(R.string.about_donation_url)
        DonationDialog(
            onDismiss = { showDonationDialog = false },
            onConfirm = {
                IntentUtils.openUrl(context, donationUrl, snackBarHostState, scope)
                showDonationDialog = false
            }
        )
    }
}

@Composable
private fun AboutHeader(
    isUpdateAvailable: Boolean,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val playStoreUrl = stringResource(R.string.about_play_store_url)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cephalon_ehiza),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.about_slogan),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.about_version_label, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        if (isUpdateAvailable) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { IntentUtils.openUrl(context, playStoreUrl, snackBarHostState, scope) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.about_update_button),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(R.string.about_update_available),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AboutSection(
    title: String,
    showDivider: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
        if (showDivider) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun DonationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.about_support_dev_dialog_title)) },
        text = { Text(stringResource(R.string.about_support_dev_dialog_desc)) },
        confirmButton = {
            val confirmButtonInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = onConfirm,
                interactionSource = confirmButtonInteraction,
                modifier = Modifier.pressScale(
                    confirmButtonInteraction,
                    PressIntensity.VERY_SUBTLE
                )
            ) {
                Text(stringResource(R.string.about_support_dev_dialog_action))
            }
        },
        dismissButton = {
            val dismissButtonInteraction = remember { MutableInteractionSource() }
            TextButton(
                onClick = onDismiss,
                interactionSource = dismissButtonInteraction,
                modifier = Modifier.pressScale(
                    dismissButtonInteraction,
                    PressIntensity.VERY_SUBTLE
                )
            ) {
                Text(stringResource(R.string.manage_goal_cancel))
            }
        }
    )
}

@Preview
@Composable
fun AboutScreenPreview() {
    PrimeTrackerTheme {
        AboutScreen(PaddingValues(0.dp), SnackbarHostState())
    }
}
