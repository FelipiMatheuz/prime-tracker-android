package com.felipimatheuz.primehunt.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.FragmentInfoListBinding
import com.felipimatheuz.primehunt.factory.PrimeSetAdapter
import com.felipimatheuz.primehunt.model.resources.AppSettings
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.model.sets.PrimeSet
import com.felipimatheuz.primehunt.model.sets.PrimeStatus
import com.felipimatheuz.primehunt.util.AppFilter

class InfoListFragment : Fragment() {

    private val binding: FragmentInfoListBinding by lazy {
        FragmentInfoListBinding.inflate(layoutInflater)
    }
    private lateinit var primeSetAdapter: PrimeSetAdapter

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
        primeSetAdapter = PrimeSetAdapter(requireContext())
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
                    R.id.submenu_show_all -> filterPrimeSetList(AppFilter.SHOW_ALL, "", false)
                    R.id.submenu_available -> filterPrimeSetList(AppFilter.AVAILABLE, "", false)
                    R.id.submenu_unavailable -> filterPrimeSetList(AppFilter.UNAVAILABLE, "", false)
                    R.id.submenu_incomplete -> filterPrimeSetList(AppFilter.INCOMPLETE, "", false)
                    R.id.submenu_complete -> filterPrimeSetList(AppFilter.COMPLETE, "", false)
                }
                if (menuItem.itemId != R.id.menu_filter) {
                    binding.etSearchItem.setText("")
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupViews() {
        binding.rvInfoList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val settings = AppSettings(requireContext())
        filterPrimeSetList(settings.getPrimeFilter(), "", true)
        binding.rvInfoList.adapter = primeSetAdapter

        binding.etSearchItem.doAfterTextChanged {
            filterPrimeSetList(settings.getPrimeFilter(), it.toString().trim(), false)
        }
    }

    private fun filterPrimeSetList(filter: AppFilter, searchText: String, checkUpdate: Boolean) {
        val primeSetList = if (checkUpdate) {
            PrimeSetData(requireContext()).updateListData()
        } else {
            PrimeSetData(requireContext()).getListSetData()
        }

        AppSettings(requireContext()).setPrimeFilter(filter)
        val appFiltered = when (filter) {
            AppFilter.AVAILABLE -> primeSetList.filter { it.status != PrimeStatus.VAULT }
            AppFilter.UNAVAILABLE -> primeSetList.filter { it.status == PrimeStatus.VAULT }
            AppFilter.INCOMPLETE -> checkListIsComplete(primeSetList, false)
            AppFilter.COMPLETE -> checkListIsComplete(primeSetList, true)
            else -> primeSetList
        }
        primeSetAdapter.setPrimeSetList(
            if (searchText.isEmpty()) {
                appFiltered
            } else {
                appFiltered.filter {
                    it.warframe.name.contains(searchText, true)
                            || it.primeItem1.name.contains(searchText, true)
                            || it.primeItem2?.name?.contains(searchText, true) == true
                }
            }
        )
    }

    private fun checkListIsComplete(primeSetList: List<PrimeSet>, completed: Boolean): List<PrimeSet> {
        val primeSetFiltered = primeSetList.filter {
            val isComplete = it.warframe.blueprint &&
                    it.warframe.components.any { itemComponent -> itemComponent.obtained }
                    && it.primeItem1.blueprint &&
                    it.primeItem1.components.any { itemComponent -> itemComponent.obtained }
                    && (if (it.primeItem2 != null) {
                it.primeItem2!!.blueprint && it.primeItem2!!.components.any { itemComponent -> itemComponent.obtained }
            } else {
                true
            })
            isComplete == completed
        }
        return primeSetFiltered
    }
}