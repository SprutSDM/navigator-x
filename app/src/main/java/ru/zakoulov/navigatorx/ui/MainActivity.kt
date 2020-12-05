package ru.zakoulov.navigatorx.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.getApp
import ru.zakoulov.navigatorx.viewmodel.Event
import ru.zakoulov.navigatorx.ui.map.MapFragment
import ru.zakoulov.navigatorx.viewmodel.MainViewModel
import ru.zakoulov.navigatorx.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(getApp().realmRepository, getApp().sharedPreferencesManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MapFragment())
            .commit()
        lifecycleScope.launch {
            mainViewModel
        }
    }

    override fun onBackPressed() {
        if (!mainViewModel.onBackPressed()) {
            finish()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
