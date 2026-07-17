package com.annaharri89.platformgallery.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface PortfolioStorage {
    suspend fun saveProjects(newProjects: List<PortfolioProject>)

    fun getProjectById(projectId: Long): Flow<PortfolioProject?>

    fun getProjects(): Flow<List<PortfolioProject>>
}

class InMemoryPortfolioStorage : PortfolioStorage {
    private val storedProjects = MutableStateFlow(emptyList<PortfolioProject>())

    override suspend fun saveProjects(newProjects: List<PortfolioProject>) {
        storedProjects.value = newProjects
    }

    override fun getProjectById(projectId: Long): Flow<PortfolioProject?> {
        return storedProjects.map { projects ->
            projects.find { it.id == projectId }
        }
    }

    override fun getProjects(): Flow<List<PortfolioProject>> = storedProjects
}
