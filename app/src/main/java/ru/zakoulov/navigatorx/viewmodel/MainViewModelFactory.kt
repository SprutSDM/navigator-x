package ru.zakoulov.navigatorx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.zakoulov.navigatorx.data.SharedPreferencesManager
import ru.zakoulov.navigatorx.data.realm.RealmRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory (
    private val realmRepository: RealmRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(realmRepository, sharedPreferencesManager) as T
    }

    companion object {
        private var instance: MainViewModelFactory? = null

        fun getInstance(
            realmRepository: RealmRepository,
            sharedPreferencesManager: SharedPreferencesManager
        ): MainViewModelFactory {
            return instance ?: MainViewModelFactory(realmRepository, sharedPreferencesManager).also {
                instance = it
            }
        }
    }
}
