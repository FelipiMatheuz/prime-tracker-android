package com.felipimatheuz.primehunt.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.felipimatheuz.primehunt.BuildConfig
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.databinding.ActivityMainBinding
import com.felipimatheuz.primehunt.factory.UnityBanner
import com.felipimatheuz.primehunt.model.resources.OtherPrimeData
import com.felipimatheuz.primehunt.model.resources.PrimeRelicApi
import com.felipimatheuz.primehunt.model.resources.PrimeSetApi
import com.felipimatheuz.primehunt.model.resources.PrimeSetData
import com.felipimatheuz.primehunt.util.apiData
import com.felipimatheuz.primehunt.util.apiRelic
import com.felipimatheuz.primehunt.util.isItemCompleted
import com.felipimatheuz.primehunt.util.toggleLoading
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var currentStep: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupLoading()
        loadAds()
        loadMessage()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun loadMessage() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setIcon(android.R.drawable.stat_sys_warning)
            setTitle(R.string.warning_v3_title)
            setMessage(R.string.warning_v3_message)
            setCancelable(false)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                loadContent()
            }
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateProgress()
    }

    private fun loadAds() {
        UnityBanner.getBanner(binding.bannerMain, this)
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.mipmap.ic_launcher_adaptive_fore)
        }
    }

    private fun setupLoading() {
        Glide.with(this).asGif().load(R.drawable.fx_loading).into(binding.ivLoadMain)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadContent() {

        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (currentStep == 1) {
                    GlobalScope.async {
                        apiRelic = PrimeRelicApi.singleInstance()
                    }.await()
                    currentStep = 2
                }
                binding.tvLoadMain.text = getString(R.string.check_updates)
                GlobalScope.async {
                    apiData = PrimeSetApi.singleInstance()
                }.await()
                showMenu()
            } catch (e: Exception) {
                if (!this@MainActivity.isFinishing) {
                    showConnectionAlert()
                }
            }
        }
    }

    private fun updateProgress() {
        val otherData = OtherPrimeData(this).getListOtherData()
        var totalPrimeItems = otherData.size
        var totalCompleted = 0
        var totalInProgress = 0
        for (primeSet in PrimeSetData(this).getListSetData()) {
            totalPrimeItems += 2 + if (primeSet.primeItem2 != null) {
                1
            } else {
                0
            }
            when (isItemCompleted(primeSet.warframe)) {
                2 -> totalCompleted++
                1 -> totalInProgress++
            }
            when (isItemCompleted(primeSet.primeItem1)) {
                2 -> totalCompleted++
                1 -> totalInProgress++
            }
            when (primeSet.primeItem2?.let { isItemCompleted(it) }) {
                2 -> totalCompleted++
                1 -> totalInProgress++
            }
        }

        for (primeItem in otherData) {
            when (isItemCompleted(primeItem)) {
                2 -> totalCompleted++
                1 -> totalInProgress++
            }
        }

        binding.tvProgressOverall.text = getString(R.string.overall_stats)
            .replace("[T]", totalPrimeItems.toString())
            .replace("[P]", totalInProgress.toString())
            .replace("[C]", totalCompleted.toString())
        binding.pbOverallCompleted.max = totalPrimeItems
        binding.pbOverallCompleted.progress = totalCompleted
        binding.pbOverallProgress.max = totalPrimeItems
        binding.pbOverallProgress.progress = totalCompleted + totalInProgress
    }

    private fun showMenu() {
        toggleLoading(binding.tvLoadMain, false)
        toggleLoading(binding.tvOwner, false)
        toggleLoading(binding.ivLoadMain, false)
        binding.clMainMenu.visibility = View.VISIBLE

        binding.tvAppVersion.text = "v${BuildConfig.VERSION_NAME}"

        binding.btnMenuHelp.setOnClickListener {
            openActivity(HelpActivity())
        }
        binding.btnMenuSet.setOnClickListener {
            openActivity(SetListActivity())
        }
        binding.btnMenuOther.setOnClickListener {
            openActivity(OtherListActivity())
        }
        binding.btnMenuRelic.setOnClickListener {
            openActivity(RelicListActivity())
        }
    }

    private fun openActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
    }

    private fun showConnectionAlert() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setIcon(R.drawable.wifi_off)
            setTitle(R.string.connection_failed)
            setMessage(R.string.connection_failed_message)
            setCancelable(false)
            setPositiveButton(R.string.connection_retry) { dialog, _ ->
                dialog.dismiss()
                loadContent()
            }
            show()
        }
    }
}