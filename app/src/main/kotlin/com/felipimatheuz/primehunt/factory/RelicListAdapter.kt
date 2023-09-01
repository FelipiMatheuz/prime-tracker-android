package com.felipimatheuz.primehunt.factory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.felipimatheuz.primehunt.databinding.ItemRelicListBinding
import com.felipimatheuz.primehunt.model.sets.ApiData
import com.felipimatheuz.primehunt.model.sets.Relic
import com.felipimatheuz.primehunt.model.sets.RelicTier
import com.felipimatheuz.primehunt.util.apiRelic
import com.felipimatheuz.primehunt.util.formatRelicText

class RelicListAdapter(private val context: Context, relics: List<Relic>, relicTier: RelicTier) :
    RecyclerView.Adapter<RelicListAdapter.RelicHolder>() {
    private var relicList: List<Relic> = relics
    private val tier = relicTier

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelicHolder {
        val binding = ItemRelicListBinding.inflate(LayoutInflater.from(context), parent, false)
        return RelicHolder(binding.root)
    }

    override fun getItemCount() = relicList.size

    override fun onBindViewHolder(holder: RelicHolder, position: Int) {
        holder.bind(relicList[position], tier)
    }

    class RelicHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemRelicListBinding.bind(view)

        fun bind(relic: Relic, tier: RelicTier) {
            with(binding) {
                tvRelicQuantity.text = relic.quantity.toString()
                if (relic.quantity > 0) {
                    tvRelicQuantity.visibility = View.VISIBLE
                } else {
                    tvRelicQuantity.visibility = View.GONE
                }
                ivFormaBp.visibility = if (relic.hasForma) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                formatRelicText(tvRelicName, relic.name, relic.quantity, relic.vaulted)
                btnRelicRewards.setOnClickListener {
                    val relicData = getRelicData(relic.name, tier)
                    RelicRewardsDialog(it.context, relicData).show()
                }
            }
        }

        private fun getRelicData(relicName: String, tier: RelicTier): ApiData {
            return apiRelic.getRelicData().first {
                it.name.contains("$tier $relicName", true)
            }
        }
    }
}