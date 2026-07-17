package com.annaharri89.platformgallery.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioRepository(
    private val portfolioApi: PortfolioApi,
    private val portfolioStorage: PortfolioStorage,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val isLoadingState = MutableStateFlow(true)
    private val errorMessageState = MutableStateFlow<String?>(null)

    val isLoading: StateFlow<Boolean> = isLoadingState.asStateFlow()
    val errorMessage: StateFlow<String?> = errorMessageState.asStateFlow()

    fun initialize() {
        requestRefresh()
    }

    fun requestRefresh() {
        scope.launch {
            refresh()
        }
    }

    suspend fun refresh() {
        isLoadingState.value = true
        errorMessageState.value = null
        when (val result = portfolioApi.fetchProjects()) {
            is PortfolioFetchResult.Success -> {
                portfolioStorage.saveProjects(result.projects)
                errorMessageState.value = null
            }
            is PortfolioFetchResult.Failure -> {
                errorMessageState.value = result.message
            }
        }
        isLoadingState.value = false
    }

    fun getProjects(): Flow<List<PortfolioProject>> = portfolioStorage.getProjects()

    fun getProjectById(projectId: Long): Flow<PortfolioProject?> =
        portfolioStorage.getProjectById(projectId)
}
