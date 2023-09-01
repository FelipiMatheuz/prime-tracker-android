package com.felipimatheuz.primehunt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.ActivityHelpBinding
import com.felipimatheuz.primehunt.fragment.HelpFragment

class HelpActivity : AppCompatActivity() {

    private val binding: ActivityHelpBinding by lazy {
        ActivityHelpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupFragment(HelpFragment())
    }

    private fun setupFragment(fragment: Fragment) {
        val name = fragment::class.java.name
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_help, fragment, name)
            setReorderingAllowed(true)
            commit()
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.regal_aya)
        }
    }
}