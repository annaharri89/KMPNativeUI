package com.annaharri89.platformgallery

import com.annaharri89.platformgallery.data.FavoritesStore
import com.annaharri89.platformgallery.data.PortfolioRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinDependencies : KoinComponent {
    val portfolioRepository: PortfolioRepository by inject()
    val favoritesStore: FavoritesStore by inject()
}
