package com.felipimatheuz.primehunt.business.state

import com.felipimatheuz.primehunt.R

sealed class MenuDialogState(var icon: Int, var title: Int, var content: Int) {

    object Help : MenuDialogState(R.drawable.ic_help, R.string.menu_help, R.string.help_content)
    object Info : MenuDialogState(R.drawable.ic_info, R.string.menu_about, R.string.about_content)

    object Sync : MenuDialogState(R.drawable.ic_google, R.string.menu_sync, R.string.sync_content)
}