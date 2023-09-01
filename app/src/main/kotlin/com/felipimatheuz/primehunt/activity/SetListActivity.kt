package com.felipimatheuz.primehunt.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.ActivitySetListBinding
import com.felipimatheuz.primehunt.fragment.InfoListFragment

class SetListActivity : AppCompatActivity() {

    private val binding: ActivitySetListBinding by lazy {
        ActivitySetListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupFragment(InfoListFragment())
    }

    private fun setupFragment(fragment: Fragment) {
        val name = fragment::class.java.name
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_set_list, fragment, name)
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