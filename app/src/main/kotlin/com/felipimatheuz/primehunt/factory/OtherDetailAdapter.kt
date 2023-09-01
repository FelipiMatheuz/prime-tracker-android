package com.felipimatheuz.primehunt.factory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.OtherCompDetailBinding
import com.felipimatheuz.primehunt.model.sets.ItemComponent
import com.felipimatheuz.primehunt.model.sets.ItemPart
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.util.formatItemPartText
import com.felipimatheuz.primehunt.util.formatRelicListText
import com.felipimatheuz.primehunt.util.togglePrimeItemComp
import com.felipimatheuz.primehunt.util.updateCompStatus


class OtherDetailAdapter(
    private val context: Context,
    primeItemObj: PrimeItem,
    primeItemComponents: List<ItemComponent?>
) :
    RecyclerView.Adapter<OtherDetailAdapter.OtherPrimeHolder>() {
    private val primeItem = primeItemObj
    private val listComponent = primeItemComponents

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherPrimeHolder {
        val binding = OtherCompDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        return OtherPrimeHolder(binding.root)
    }

    override fun getItemCount() = listComponent.size

    override fun onBindViewHolder(holder: OtherPrimeHolder, position: Int) {
        holder.bind(listComponent[position], primeItem)
    }

    class OtherPrimeHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = OtherCompDetailBinding.bind(view)

        fun bind(itemComponentGrouped: ItemComponent?, primeItem: PrimeItem) {
            with(binding) {
                setupItem(tvOtherComp, tvOtherCompRelics, ivOtherComp, itemComponentGrouped, primeItem)
                tvOtherComp.setOnClickListener {
                    togglePrimeItemComp(primeItem, tvOtherComp, itemComponentGrouped)

                    val imgStatus = updateCompStatus(
                        if (itemComponentGrouped == null) {
                            listOf(primeItem.blueprint)
                        } else {
                            primeItem.components.filter { it.part == itemComponentGrouped.part }
                                .map { it.obtained }
                        }
                    )
                    ivOtherComp.setImageResource(imgStatus)
                }
            }
        }

        private fun setupItem(
            tvCompItem: TextView,
            tvCompRelicsItem: TextView,
            ivCompStatusItem: ImageView,
            compItem: ItemComponent?,
            primeItem: PrimeItem
        ) {
            if (compItem == null) {
                tvCompItem.text = tvCompItem.context.getString(R.string.comp_blueprint)
                tvCompItem.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(tvCompItem.context, R.drawable.prime_blueprint),
                    null,
                    null
                )
            } else {
                val compCount = primeItem.components.groupingBy { it.part }.eachCount().filter { it.value > 1 }
                formatItemPartText(tvCompItem, compItem.part, compCount[compItem.part])
            }
            val itemCompPart =
                if (compItem == null) {
                    "BLUEPRINT"
                } else {
                    when (compItem.part) {
                        ItemPart.CIRCUIT -> {
                            ItemPart.SYSTEMS.toString()
                        }

                        ItemPart.ULIMB -> {
                            "UPPER LIMB"
                        }

                        ItemPart.LLIMB -> {
                            "LOWER LIMB"
                        }

                        else -> {
                            compItem.part.toString()
                        }
                    }
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