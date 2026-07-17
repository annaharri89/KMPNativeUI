package com.annaharri89.platformgallery.screens

import com.annaharri89.platformgallery.data.PortfolioProject
import com.annaharri89.platformgallery.data.PortfolioRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ListViewModel(portfolioRepository: PortfolioRepository) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedLanguage = MutableStateFlow<String?>(null)

    private val allProjects: StateFlow<List<PortfolioProject>> =
        portfolioRepository.getProjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @NativeCoroutinesState
    val query: StateFlow<String> = searchQuery

    @NativeCoroutinesState
    val languageFilter: StateFlow<String?> = selectedLanguage

    @NativeCoroutinesState
    val languages: StateFlow<List<String>> = allProjects
        .map { projects ->
            projects.map { it.languageOrUnknown }
                .distinct()
                .sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @NativeCoroutinesState
    val projects: StateFlow<List<PortfolioProject>> =
        combine(allProjects, searchQuery, selectedLanguage) { projects, queryText, language ->
            projects.filter { project ->
                matchesQuery(project, queryText) && matchesLanguage(project, language)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun setLanguageFilter(language: String?) {
        selectedLanguage.value = language
    }

    private fun matchesQuery(project: PortfolioProject, queryText: String): Boolean {
        if (queryText.isBlank()) return true
        val needle = queryText.trim()
        return project.name.contains(needle, ignoreCase = true) ||
            project.summary.orEmpty().contains(needle, ignoreCase = true) ||
            project.languageOrUnknown.contains(needle, ignoreCase = true)
    }

    private fun matchesLanguage(project: PortfolioProject, language: String?): Boolean {
        return language == null || project.languageOrUnknown == language
    }
}
