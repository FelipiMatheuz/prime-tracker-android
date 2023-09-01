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
import com.felipimatheuz.primehunt.databinding.FragmentOtherListBinding
import com.felipimatheuz.primehunt.factory.OtherPrimeAdapter
import com.felipimatheuz.primehunt.model.resources.AppSettings
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.model.sets.PrimeItem
import com.felipimatheuz.primehunt.util.AppFilter

class OtherListFragment : Fragment() {

    private val binding: FragmentOtherListBinding by lazy {
        FragmentOtherListBinding.inflate(layoutInflater)
    }
    private lateinit var otherPrimeAdapter: OtherPrimeAdapter

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
        otherPrimeAdapter = OtherPrimeAdapter(requireContext())
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
                menuInflater.inflate(R.menu.menu_other_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.submenu_show_all -> filterOtherList(AppFilter.SHOW_ALL, "", false)
                    R.id.submenu_available -> filterOtherList(AppFilter.AVAILABLE, "", false)
                    R.id.submenu_unavailable -> filterOtherList(AppFilter.UNAVAILABLE, "", false)
                    R.id.submenu_incomplete -> filterOtherList(AppFilter.INCOMPLETE, "", false)
                    R.id.submenu_complete -> filterOtherList(AppFilter.COMPLETE, "", false)
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupViews() {
        binding.rvOtherItemFarm.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val settings = AppSettings(requireContext())
        filterOtherList(settings.getOtherFilter(), "", true)
        binding.rvOtherItemFarm.adapter = otherPrimeAdapter

        binding.etSearchItemOther.doAfterTextChanged {
            filterOtherList(settings.getOtherFilter(), it.toString().trim(), false)
        }
    }

    private fun filterOtherList(filter: AppFilter, searchText: String, checkUpdate: Boolean) {
        val otherPrimeList = if (checkUpdate) {
            OtherPrimeData(requireContext()).updateListData()
        } else {
            OtherPrimeData(requireContext()).getListOtherData()
        }

        AppSettings(requireContext()).setPrimeFilter(filter)
        val appFiltered = when (filter) {
            AppFilter.INCOMPLETE -> checkListIsComplete(otherPrimeList, false)
            AppFilter.COMPLETE -> checkListIsComplete(otherPrimeList, true)
            else -> otherPrimeList
        }
        otherPrimeAdapter.setOtherPrimeList(
            if (searchText.isEmpty()) {
                appFiltered
            } else {
                appFiltered.filter { it.name.contains(searchText, true) }
            }
        )
    }

    private fun checkListIsComplete(primeItemList: List<PrimeItem>, completed: Boolean): List<PrimeItem> {
        val primeItemFiltered = primeItemList.filter {
            val isComplete = it.blueprint && it.components.any { itemComponent -> itemComponent.obtained }
            isComplete == completed
        }
        return primeItemFiltered
    }
}