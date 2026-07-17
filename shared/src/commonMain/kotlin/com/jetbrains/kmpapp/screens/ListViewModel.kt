package com.jetbrains.kmpapp.screens

import com.jetbrains.kmpapp.data.MuseumObject
import com.jetbrains.kmpapp.data.MuseumRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ListViewModel(museumRepository: MuseumRepository) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedDepartment = MutableStateFlow<String?>(null)

    private val allObjects: StateFlow<List<MuseumObject>> =
        museumRepository.getObjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @NativeCoroutinesState
    val query: StateFlow<String> = searchQuery

    @NativeCoroutinesState
    val departmentFilter: StateFlow<String?> = selectedDepartment

    @NativeCoroutinesState
    val departments: StateFlow<List<String>> = allObjects
        .map { objects ->
            objects.map { it.department }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @NativeCoroutinesState
    val objects: StateFlow<List<MuseumObject>> =
        combine(allObjects, searchQuery, selectedDepartment) { objects, queryText, department ->
            objects.filter { museumObject ->
                matchesQuery(museumObject, queryText) &&
                    matchesDepartment(museumObject, department)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun setDepartmentFilter(department: String?) {
        selectedDepartment.value = department
    }

    private fun matchesQuery(museumObject: MuseumObject, queryText: String): Boolean {
        if (queryText.isBlank()) return true
        val needle = queryText.trim()
        return museumObject.title.contains(needle, ignoreCase = true) ||
            museumObject.artistDisplayName.contains(needle, ignoreCase = true) ||
            museumObject.medium.contains(needle, ignoreCase = true)
    }

    private fun matchesDepartment(museumObject: MuseumObject, department: String?): Boolean {
        return department == null || museumObject.department == department
    }
}
