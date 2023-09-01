package com.felipimatheuz.primehunt.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.ActivityRelicListBinding
import com.felipimatheuz.primehunt.fragment.RelicListFragment

class RelicListActivity : AppCompatActivity() {

    private val binding: ActivityRelicListBinding by lazy {
        ActivityRelicListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupFragment(RelicListFragment())
    }

    private fun setupFragment(fragment: Fragment) {
        val name = fragment::class.java.name
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_relic_list, fragment, name)
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