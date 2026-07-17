package com.annaharri89.platformgallery.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PortfolioRepository(
    private val portfolioApi: PortfolioApi,
    private val portfolioStorage: PortfolioStorage,
) {
    private val scope = CoroutineScope(SupervisorJob())

    fun initialize() {
        scope.launch {
            refresh()
        }
    }

    suspend fun refresh() {
        portfolioStorage.saveProjects(portfolioApi.getProjects())
    }

    fun getProjects(): Flow<List<PortfolioProject>> = portfolioStorage.getProjects()

    fun getProjectById(projectId: Int): Flow<PortfolioProject?> =
        portfolioStorage.getProjectById(projectId)
}
