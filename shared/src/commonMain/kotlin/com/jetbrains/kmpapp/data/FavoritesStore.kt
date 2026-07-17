package com.jetbrains.kmpapp.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoritesStore {
    private val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

    val favorites: StateFlow<Set<Int>> = favoriteIds.asStateFlow()

    fun isFavorite(objectId: Int): Boolean = objectId in favoriteIds.value

    fun toggle(objectId: Int) {
        favoriteIds.update { current ->
            if (objectId in current) current - objectId else current + objectId
        }
    }
}
