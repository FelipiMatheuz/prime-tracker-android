package com.felipimatheuz.primehunt.factory

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.felipimatheuz.primehunt.databinding.ItemOtherListBinding
import com.felipimatheuz.primehunt.model.sets.ItemPart
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.util.*

class OtherPrimeAdapter(private val context: Context) :
    RecyclerView.Adapter<OtherPrimeAdapter.OtherPrimeHolder>() {
    private var otherPrimeList: List<PrimeItem> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setOtherPrimeList(otherPrimeList: List<PrimeItem>) {
        this.otherPrimeList = otherPrimeList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherPrimeHolder {
        val binding = ItemOtherListBinding.inflate(LayoutInflater.from(context), parent, false)
        return OtherPrimeHolder(binding.root)
    }

    override fun getItemCount() = otherPrimeList.size

    override fun onBindViewHolder(holder: OtherPrimeHolder, position: Int) {
        holder.bind(otherPrimeList[position])
    }

    class OtherPrimeHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemOtherListBinding.bind(view)

        fun bind(primeItem: PrimeItem) {
            with(binding) {
                formatCardText(tvOtherName, primeItem.name, false)
                val hasPrimeItemAsComponent =
                    primeItem.components.any { it.part == ItemPart.VASTO || it.part == ItemPart.LEX || it.part == ItemPart.BRONCO }
                ivLinkComp.visibility = if (hasPrimeItemAsComponent) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                updateOtherInfo(primeItem)

                setupListeners(btnOtherFarm, primeItem)
            }
        }

        private fun updateOtherInfo(primeItem: PrimeItem) {
            with(binding) {
                formatPercentText(tvOtherQuantity, primeItem)
                val statusColor =
                    updateStatusColor(tvOtherQuantity.text)
                updateCardBackground(cvOtherStatus, statusColor)
            }
        }

        private fun setupListeners(btnInfoFarm: ConstraintLayout, primeItem: PrimeItem) {
            btnInfoFarm.setOnClickListener {
                val dialog = OtherDetailDialog(it.context, primeItem)
                dialog.setOnDismissListener {
                    updateOtherInfo(primeItem)
                }
                dialog.show()
            }
            btnInfoFarm.setOnLongClickListener {
                togglePrimeItem(it.context, primeItem)
                updateOtherInfo(primeItem)
                true
            }
        }
    }
}