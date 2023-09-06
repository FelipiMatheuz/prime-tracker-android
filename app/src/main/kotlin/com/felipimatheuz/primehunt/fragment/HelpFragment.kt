package com.felipimatheuz.primehunt.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private val binding: FragmentHelpBinding by lazy {
        FragmentHelpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replaceStringMenus()
    }

    private fun replaceStringMenus() {
        binding.helpBasicsContent.text = getString(R.string.basics_content)
            .replace("[MENU SET]",getString(R.string.menu_prime_sets))
            .replace("[MENU OTHER]",getString(R.string.menu_other_prime))
            .replace("[MENU RELIC]",getString(R.string.menu_relics))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root
}