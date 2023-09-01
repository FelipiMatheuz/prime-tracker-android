package com.felipimatheuz.primehunt.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.FragmentRelicListBinding
import com.felipimatheuz.primehunt.factory.RelicListAdapter
import com.felipimatheuz.primehunt.model.resources.AppSettings
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.model.sets.*
import com.felipimatheuz.primehunt.util.AppFilter
import com.felipimatheuz.primehunt.util.apiRelic

class RelicListFragment : Fragment() {

    private val binding: FragmentRelicListBinding by lazy {
        FragmentRelicListBinding.inflate(layoutInflater)
    }
    private lateinit var listRemaining: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(android.R.transition.fade)
        exitTransition = inflater.inflateTransition(android.R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupViews()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (menu is MenuBuilder) {
                    menu.setOptionalIconsVisible(true)
                }
                menuInflater.inflate(R.menu.menu_info_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.submenu_show_all -> filterRelicList(AppFilter.SHOW_ALL)
                    R.id.submenu_available -> filterRelicList(AppFilter.AVAILABLE)
                    R.id.submenu_unavailable -> filterRelicList(AppFilter.UNAVAILABLE)
                    R.id.submenu_incomplete -> filterRelicList(AppFilter.INCOMPLETE)
                    R.id.submenu_complete -> filterRelicList(AppFilter.COMPLETE)
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun filterRelicList(filter: AppFilter) {
        AppSettings(requireContext()).setRelicFilter(filter)
        fillList(binding.rvLithList, RelicTier.LITH, filter)
        fillList(binding.rvMesoList, RelicTier.MESO, filter)
        fillList(binding.rvNeoList, RelicTier.NEO, filter)
        fillList(binding.rvAxiList, RelicTier.AXI, filter)
    }

    private fun setupViews() {
        listRemaining = searchList()
        val filter = AppSettings(requireContext()).getRelicFilter()
        fillList(binding.rvLithList, RelicTier.LITH, filter)
        fillList(binding.rvMesoList, RelicTier.MESO, filter)
        fillList(binding.rvNeoList, RelicTier.NEO, filter)
        fillList(binding.rvAxiList, RelicTier.AXI, filter)
    }

    private fun fillList(rvList: RecyclerView, relicTier: RelicTier, filter: AppFilter) {
        val relics = apiRelic.getRelics(relicTier, listRemaining)
        val relicFiltered = when (filter) {
            AppFilter.AVAILABLE -> relics.filter { !it.vaulted }
            AppFilter.UNAVAILABLE -> relics.filter { it.vaulted }
            AppFilter.INCOMPLETE -> relics.filter { it.quantity > 0 }
            AppFilter.COMPLETE -> relics.filter { it.quantity <= 0 }
            else -> relics
        }
        val relicListAdapter = RelicListAdapter(requireContext(), relicFiltered, relicTier)
        rvList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvList.adapter = relicListAdapter
    }

    private fun searchList(): List<String> {
        val remainingList: MutableList<String> = mutableListOf()
        for (primeSet in PrimeSetData(requireContext()).getListSetData()) {
            remainingList.addAll(formatSearchText(primeSet.warframe))
            remainingList.addAll(formatSearchText(primeSet.primeItem1))
            if (primeSet.primeItem2 != null) {
                remainingList.addAll(formatSearchText(primeSet.primeItem2!!))
            }
        }
        for (primeItem in OtherPrimeData(requireContext()).getListOtherData()) {
            remainingList.addAll(formatSearchText(primeItem))
        }

        return remainingList
    }

    private fun formatSearchText(primeItem: PrimeItem): List<String> {
        val remainingList: MutableList<String> = mutableListOf()
        if (!primeItem.blueprint) {
            val itemComp =
                if (primeItem.name == "Kavasa") {
                    "Kubrow Collar BLUEPRINT"
                } else {
                    "BLUEPRINT"
                }
            val searchText = primeItem.name.plus(" Prime ").plus(itemComp)
            remainingList.add(searchText)
        }
        for (component in primeItem.components) {
            if (!component.obtained) {
                val itemComp = when (component.part) {
                    ItemPart.CIRCUIT -> ItemPart.SYSTEMS.toString()
                    ItemPart.ULIMB -> "UPPER LIMB"
                    ItemPart.LLIMB -> "LOWER LIMB"
                    else -> component.part.toString()
                }
                val searchText = primeItem.name.plus(" Prime ").plus(itemComp)
                remainingList.add(searchText)
            }
        }
        return remainingList
    }
}