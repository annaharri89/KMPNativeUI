package com.annaharri89.platformgallery.screens

import com.annaharri89.platformgallery.data.FavoritesStore
import com.annaharri89.platformgallery.data.PortfolioProject
import com.annaharri89.platformgallery.data.PortfolioRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

class FavoritesViewModel(
    portfolioRepository: PortfolioRepository,
    favoritesStore: FavoritesStore,
) : ViewModel() {
    @NativeCoroutinesState
    val favorites: StateFlow<List<PortfolioProject>> =
        combine(
            portfolioRepository.getProjects(),
            favoritesStore.favorites,
        ) { projects, favoriteIds ->
            projects.filter { it.id in favoriteIds }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
