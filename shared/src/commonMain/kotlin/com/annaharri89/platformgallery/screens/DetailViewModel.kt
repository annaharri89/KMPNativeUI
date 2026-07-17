package com.annaharri89.platformgallery.screens

import com.annaharri89.platformgallery.data.FavoritesStore
import com.annaharri89.platformgallery.data.PortfolioProject
import com.annaharri89.platformgallery.data.PortfolioRepository
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
    private val portfolioRepository: PortfolioRepository,
    private val favoritesStore: FavoritesStore,
) : ViewModel() {
    private val projectId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutinesState
    val project: StateFlow<PortfolioProject?> = projectId
        .flatMapLatest {
            val id = it ?: return@flatMapLatest flowOf(null)
            portfolioRepository.getProjectById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @NativeCoroutinesState
    val isFavorite: StateFlow<Boolean> =
        combine(projectId, favoritesStore.favorites) { id, favorites ->
            id != null && id in favorites
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setId(projectId: Int) {
        this.projectId.value = projectId
    }

    fun toggleFavorite() {
        val id = projectId.value ?: return
        favoritesStore.toggle(id)
    }
}
