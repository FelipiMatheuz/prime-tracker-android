package com.felipimatheuz.primehunt.factory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.felipimatheuz.primehunt.databinding.DialogRelicRewardsBinding
import com.felipimatheuz.primehunt.model.sets.ApiData
import com.felipimatheuz.primehunt.util.formatCardRelicText
import com.felipimatheuz.primehunt.util.formatRelicRewardsText

class RelicRewardsDialog(context: Context, relicData: ApiData) : Dialog(context) {

    private val binding: DialogRelicRewardsBinding by lazy {
        DialogRelicRewardsBinding.inflate(layoutInflater)
    }
    private val relic = relicData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setupView()
    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun setupView() {
        val relicName = relic.name.subSequence(0, relic.name.lastIndexOf(" ")).toString()
        formatCardRelicText(binding.tvInfoRelic, relicName)
        binding.ivInfoRelicClose.setOnClickListener {
            dismiss()
        }
        formatRelicRewardsText(binding.tvInfoRelicRewards, relic.rewards)
    }
}