package com.felipimatheuz.primehunt.factory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.felipimatheuz.primehunt.databinding.DialogSetDetailBinding
import com.felipimatheuz.primehunt.model.sets.PrimeSet
import com.felipimatheuz.primehunt.util.formatCardText

class SetDetailDialog(context: Context, itemSet: PrimeSet) : Dialog(context) {

    private val binding: DialogSetDetailBinding by lazy {
        DialogSetDetailBinding.inflate(layoutInflater)
    }
    private val primeSet = itemSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setupHeader()
        setupListData()
    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun setupHeader() {
        formatCardText(binding.tvInfoFarmSet, primeSet.warframe.name, true)
        binding.ivInfoFarmClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupListData() {
        binding.rvInfoFarm.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val itemDetailAdapter = ItemDetailAdapter(context, primeSet.warframe.name)
        itemDetailAdapter.setPrimeItemList(
            if (primeSet.primeItem2 != null) {
                listOf(primeSet.warframe, primeSet.primeItem1, primeSet.primeItem2!!)
            } else {
                listOf(primeSet.warframe, primeSet.primeItem1)
            }
        )

        binding.rvInfoFarm.adapter = itemDetailAdapter
    }
}