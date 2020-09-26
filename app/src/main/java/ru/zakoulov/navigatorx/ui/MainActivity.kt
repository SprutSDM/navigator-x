package ru.zakoulov.navigatorx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.ui.map.MapFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MapFragment.instance)
            .addToBackStack(MapFragment.TAG)
            .commit()
    }
}
