package com.felipimatheuz.primehunt.factory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.felipimatheuz.primehunt.databinding.ItemSetDetailBinding
import com.felipimatheuz.primehunt.model.sets.ItemComponent
import com.felipimatheuz.primehunt.model.sets.ItemPart
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.util.*


class ItemDetailAdapter(private val context: Context, setName: String) :
    RecyclerView.Adapter<ItemDetailAdapter.PrimeItemHolder>() {
    private var primeItemCompList = listOf<PrimeItem>()

    private val primeSetName = setName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrimeItemHolder {
        val binding = ItemSetDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return PrimeItemHolder(binding.root)
    }

    override fun getItemCount() = primeItemCompList.size

    override fun onBindViewHolder(holder: PrimeItemHolder, position: Int) {
        holder.bind(primeItemCompList[position], primeSetName)
    }

    fun setPrimeItemList(primeItemList: List<PrimeItem>) {
        primeItemCompList = primeItemList
    }

    class PrimeItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemSetDetailBinding.bind(view)

        fun bind(primeItem: PrimeItem, primeSetName: String) {
            with(binding) {
                formatCardText(tvCompName, primeItem.name, false)

                val compCount = primeItem.components.groupingBy { it.part }.eachCount().filter { it.value > 1 }
                val compGroup = primeItem.components.distinctBy { it.part }

                setupItem(tvBlueprint, tvBlueprintRelics, ivBlueprint, null, null, primeItem)
                tvBlueprint.setOnClickListener {
                    togglePrimeItem(primeSetName, tvBlueprint, primeItem, null)
                    val imgStatus = updateCompStatus(listOf(primeItem.blueprint))
                    ivBlueprint.setImageResource(imgStatus)
                }
                setupItem(tvComp1, tvComp1Relics, ivComp1, compGroup[0], compCount, primeItem)
                tvComp1.setOnClickListener {
                    togglePrimeItem(primeSetName, tvComp1, primeItem, compGroup[0].part)
                    val imgStatus = updateCompStatus(primeItem.components.filter { it.part == compGroup[0].part }
                        .map { it.obtained })
                    ivComp1.setImageResource(imgStatus)
                }
                setupItem(tvComp2, tvComp2Relics, ivComp2, compGroup[1], compCount, primeItem)
                tvComp2.setOnClickListener {
                    togglePrimeItem(primeSetName, tvComp2, primeItem, compGroup[1].part)
                    val imgStatus = updateCompStatus(primeItem.components.filter { it.part == compGroup[1].part }
                        .map { it.obtained })
                    ivComp2.setImageResource(imgStatus)
                }

                if (compGroup.size > 2) {
                    setupItem(tvComp3, tvComp3Relics, ivComp3, compGroup[2], compCount, primeItem)
                    tvComp3.setOnClickListener {
                        togglePrimeItem(primeSetName, tvComp3, primeItem, compGroup[2].part)
                        val imgStatus = updateCompStatus(primeItem.components.filter { it.part == compGroup[2].part }
                            .map { it.obtained })
                        ivComp3.setImageResource(imgStatus)
                    }
                    if (compGroup.size > 3) {
                        setupItem(tvComp4, tvComp4Relics, ivComp4, compGroup[3], compCount, primeItem)
                        tvComp4.setOnClickListener {
                            togglePrimeItem(primeSetName, tvComp4, primeItem, compGroup[3].part)
                            val imgStatus =
                                updateCompStatus(primeItem.components.filter { it.part == compGroup[3].part }
                                    .map { it.obtained })
                            ivComp4.setImageResource(imgStatus)
                        }
                    } else {
                        tvComp4.visibility = View.GONE
                        tvComp4Relics.visibility = View.GONE
                    }
                } else {
                    tvComp3.visibility = View.GONE
                    tvComp3Relics.visibility = View.GONE
                    tvComp4.visibility = View.GONE
                    tvComp4Relics.visibility = View.GONE
                }
            }
        }

        private fun setupItem(
            tvCompItem: TextView,
            tvCompRelicsItem: TextView,
            ivCompStatusItem: ImageView,
            compItem: ItemComponent?,
            compCount: Map<ItemPart, Int>?,
            primeItem: PrimeItem
        ) {
            if (compItem != null && compCount != null) {
                formatItemPartText(
                    tvCompItem,
                    compItem.part,
                    compCount[compItem.part]
                )
            }
            val itemCompPart = if (compItem == null) {
                if(primeItem.name == "Kavasa"){
                    "Kubrow Collar BLUEPRINT"
                } else {
                    "BLUEPRINT"
                }
            } else if (compItem.part == ItemPart.CIRCUIT) {
                ItemPart.SYSTEMS.toString()
            } else if (compItem.part == ItemPart.ULIMB) {
                "UPPER LIMB"
            } else if (compItem.part == ItemPart.LLIMB) {
                "LOWER LIMB"
            } else {
                compItem.part.toString()
            }
            formatRelicListText(tvCompRelicsItem, primeItem.name, itemCompPart)

            val obtainedList = if (compItem == null) {
                listOf(primeItem.blueprint)
            } else {
                primeItem.components.filter { it.part == compItem.part }.map { it.obtained }
            }
            ivCompStatusItem.setImageResource(updateCompStatus(obtainedList))
        }
    }
}