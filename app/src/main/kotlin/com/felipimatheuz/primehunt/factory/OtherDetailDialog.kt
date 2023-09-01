package com.felipimatheuz.primehunt.factory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.felipimatheuz.primehunt.databinding.DialogItemDetailBinding
import com.felipimatheuz.primehunt.model.sets.ItemComponent
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.util.formatCardText

class OtherDetailDialog(context: Context, itemPrime: PrimeItem) : Dialog(context) {

    private val binding: DialogItemDetailBinding by lazy {
        DialogItemDetailBinding.inflate(layoutInflater)
    }
    private val primeItem = itemPrime

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
        formatCardText(binding.tvOtherName, primeItem.name, false)
        binding.ivOtherClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupListData() {
        binding.rvOtherFarm.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val otherDetailAdapter = OtherDetailAdapter(context, primeItem, getDistinctComponents())
        binding.rvOtherFarm.adapter = otherDetailAdapter
    }

    private fun getDistinctComponents(): List<ItemComponent?> {
        val primeItemComponent = mutableListOf<ItemComponent?>()
        primeItemComponent.add(null)
        primeItemComponent.addAll(primeItem.components.distinctBy { it.part })
        return primeItemComponent
    }
}