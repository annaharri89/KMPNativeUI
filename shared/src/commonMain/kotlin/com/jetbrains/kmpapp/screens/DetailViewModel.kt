package com.jetbrains.kmpapp.screens

import com.jetbrains.kmpapp.data.FavoritesStore
import com.jetbrains.kmpapp.data.MuseumObject
import com.jetbrains.kmpapp.data.MuseumRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class DetailViewModel(
    private val museumRepository: MuseumRepository,
    private val favoritesStore: FavoritesStore,
) : ViewModel() {
    private val objectId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutinesState
    val museumObject: StateFlow<MuseumObject?> = objectId
        .flatMapLatest {
            val id = it ?: return@flatMapLatest flowOf(null)
            museumRepository.getObjectById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @NativeCoroutinesState
    val isFavorite: StateFlow<Boolean> =
        combine(objectId, favoritesStore.favorites) { id, favorites ->
            id != null && id in favorites
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setId(objectId: Int) {
        this.objectId.value = objectId
    }

    fun toggleFavorite() {
        val id = objectId.value ?: return
        favoritesStore.toggle(id)
    }
}
