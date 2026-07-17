package com.annaharri89.platformgallery.data

sealed interface PortfolioFetchResult {
    data class Success(val projects: List<PortfolioProject>) : PortfolioFetchResult
    data class Failure(val message: String, val cause: Throwable? = null) : PortfolioFetchResult
}
