package ru.zakoulov.navigatorx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.zakoulov.navigatorx.data.RealmRepository

class MainViewModelFactory (
    private val realmRepository: RealmRepository
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(realmRepository) as T
    }

    companion object {
        private var instance: MainViewModelFactory? = null

        fun getInstance(realmRepository: RealmRepository): MainViewModelFactory {
            return instance ?: MainViewModelFactory(realmRepository).also {
                instance = it
            }
        }
    }
}
