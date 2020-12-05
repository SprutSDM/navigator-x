package ru.zakoulov.navigatorx

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.gson.Gson
import io.realm.Realm
import io.realm.log.LogLevel
import io.realm.log.RealmLog
import ru.zakoulov.navigatorx.data.SharedPreferencesManager
import ru.zakoulov.navigatorx.data.realm.RealmMapper
import ru.zakoulov.navigatorx.data.realm.RealmRepository
import ru.zakoulov.navigatorx.data.realm.RealmRoomInfoMapper

class App : Application() {

    val realmRepository: RealmRepository by lazy {
        RealmRepository(
            realmAppId = getString(R.string.realm_app_id),
            realmMapper = RealmMapper(
                gson = Gson(),
                realmRoomInfoMapper = RealmRoomInfoMapper()
            )
        )
    }

    lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(LogLevel.ALL)
        }
        sharedPreferencesManager = SharedPreferencesManager(
            getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "NavigatorXSharedPreferences"
    }
}

fun Activity.getApp(): App {
    return application as App
}
