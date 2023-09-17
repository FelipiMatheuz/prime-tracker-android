package com.felipimatheuz.primehunt.business.state

import com.bumptech.glide.request.RequestCoordinator.RequestState
import com.felipimatheuz.primehunt.R

sealed class MenuDialogState(var icon: Int, var title: Int, var content: Int) {

    object Help : MenuDialogState(R.drawable.ic_help, R.string.help, R.string.help_content)
    object Info : MenuDialogState(R.drawable.ic_info, R.string.about, R.string.about_content)

    object Sync : MenuDialogState(R.drawable.ic_google, R.string.sync, R.string.sync_content)
    object None : MenuDialogState(0, 0, 0)
}