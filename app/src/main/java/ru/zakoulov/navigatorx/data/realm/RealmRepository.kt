package ru.zakoulov.navigatorx.data.realm

import android.util.Log
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.zakoulov.navigatorx.data.MapData

class RealmRepository(
    realmAppId: String,
    private val realmMapper: RealmMapper
) {
    private val _mapData = MutableStateFlow(MapData(emptyList(), emptyMap(), emptyMap()))
    val mapData: StateFlow<MapData> = _mapData

    private val app = App(AppConfiguration.Builder(realmAppId).build())

    init {
        val user = app.currentUser()
        if (user == null) {
            app.loginAsync(Credentials.anonymous()) {
                app.currentUser()?.let { currentUser ->
                    onAuth(currentUser)
                }
            }
        } else {
            onAuth(user)
        }
    }

    private fun onAuth(user: User) {
        val config = SyncConfiguration.Builder(user, "nav").build()
        val realm = Realm.getInstance(config)
        val data = realm.where<MapPointModel>().findAllAsync()
        _mapData.value = realmMapper.map(data)
        Log.d(TAG, "savedData: $data")
        data.addChangeListener { t, changeSet ->
            _mapData.value = realmMapper.map(t)
            Log.d(TAG, "recievedData: $t")
        }
    }

    companion object {
        private const val TAG = "RealmRepository"
    }
}
