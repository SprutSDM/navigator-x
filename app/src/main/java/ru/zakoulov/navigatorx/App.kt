package ru.zakoulov.navigatorx

import android.app.Activity
import android.app.Application
import io.realm.Realm
import io.realm.log.LogLevel
import io.realm.log.RealmLog
import ru.zakoulov.navigatorx.data.realm.RealmRepository

class App : Application() {

    val realmRepository: RealmRepository by lazy {
        RealmRepository(getString(R.string.realm_app_id))
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(LogLevel.ALL)
        }
    }
}

fun Activity.getApp(): App {
    return application as App
}
