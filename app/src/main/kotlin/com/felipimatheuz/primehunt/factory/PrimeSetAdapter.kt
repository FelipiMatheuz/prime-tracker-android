package com.felipimatheuz.primehunt.factory

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.felipimatheuz.primehunt.databinding.ItemInfoListBinding
import com.felipimatheuz.primehunt.model.sets.PrimeSet
import com.felipimatheuz.primehunt.util.*

class PrimeSetAdapter(private val context: Context) :
    RecyclerView.Adapter<PrimeSetAdapter.PrimeSetHolder>() {
    private var primeSetList: List<PrimeSet> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setPrimeSetList(primeSetList: List<PrimeSet>) {
        this.primeSetList = primeSetList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrimeSetHolder {
        val binding = ItemInfoListBinding.inflate(LayoutInflater.from(context), parent, false)
        return PrimeSetHolder(binding.root)
    }

    override fun getItemCount() = primeSetList.size

    override fun onBindViewHolder(holder: PrimeSetHolder, position: Int) {
        holder.bind(primeSetList[position])
    }

    class PrimeSetHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemInfoListBinding.bind(view)

        fun bind(primeSet: PrimeSet) {
            with(binding) {
                formatCardText(tvItemSetName, primeSet.warframe.name, true)
                formatCardText(tvWarframe, primeSet.warframe.name, false)
                formatCardText(tvWeapon1, primeSet.primeItem1.name, false)
                if (primeSet.primeItem2 != null) {
                    formatCardText(tvWeapon2, primeSet.primeItem2!!.name, false)
                } else {
                    tvWeapon2.text = ""
                }

                getStatusText(tvStatus, primeSet.status)

                Glide.with(ivItemSet.context).load(primeSet.imgLink).sizeMultiplier(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivItemSet)

                updateSetInfo(primeSet)

                setupListeners(btnInfoFarm, primeSet)
            }
        }

        private fun updateSetInfo(primeSet: PrimeSet) {
            with(binding) {
                formatPercentText(tvWarframePercent, primeSet.warframe)
                formatPercentText(tvWeapon1Percent, primeSet.primeItem1)
                if (primeSet.primeItem2 != null) {
                    formatPercentText(tvWeapon2Percent, primeSet.primeItem2!!)
                } else {
                    tvWeapon2Percent.text = ""
                }

                val statusColor =
                    updateStatusColor(tvWarframePercent.text, tvWeapon1Percent.text, tvWeapon2Percent.text)
                updateCardBackground(cvItemStatus, statusColor)
            }
        }

        private fun setupListeners(btnInfoFarm: ConstraintLayout, primeSet: PrimeSet) {
            btnInfoFarm.setOnClickListener {
                val dialog = SetDetailDialog(it.context, primeSet)
                dialog.setOnDismissListener {
                    updateSetInfo(primeSet)
                }
                dialog.show()
            }
            btnInfoFarm.setOnLongClickListener {
                togglePrimeSet(it.context, primeSet)
                updateSetInfo(primeSet)
                true
            }
        }
    }
}