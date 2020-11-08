package ru.zakoulov.navigatorx.data.realm

import android.util.Log
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration

class RealmRepository(
    realmAppId: String
) {
    private val app = App(AppConfiguration.Builder(realmAppId).build())

    init {
        val user = app.currentUser()
        if (user == null) {
            app.loginAsync(Credentials.anonymous()) {
                onAuth(app.currentUser()!!)
            }
        } else {
            onAuth(user)
        }
    }

    private fun onAuth(user: User) {
        val config = SyncConfiguration.Builder(user, "nav").build()
        val realm = Realm.getInstance(config)
        val data = realm.where<MapPointModel>().findAllAsync()
        Log.d(TAG, "savedData: $data")
        data.addChangeListener { t, changeSet ->
            Log.d(TAG, "recievedData: $t")
        }
    }

    companion object {
        private const val TAG = "RealmRepository"
    }

    class Factory(
        private val realmAppId: String
    ) {
        fun create() = RealmRepository(
            realmAppId = realmAppId
        )
    }
}
