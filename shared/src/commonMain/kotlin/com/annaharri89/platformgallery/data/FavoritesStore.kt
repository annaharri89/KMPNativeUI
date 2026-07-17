package com.annaharri89.platformgallery.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoritesStore(
    private val settings: Settings = Settings(),
) {
    private val favoriteIds = MutableStateFlow(loadFavoriteIds())

    val favorites: StateFlow<Set<Int>> = favoriteIds.asStateFlow()

    fun isFavorite(projectId: Int): Boolean = projectId in favoriteIds.value

    fun toggle(projectId: Int) {
        favoriteIds.update { current ->
            val updated =
                if (projectId in current) current - projectId else current + projectId
            persistFavoriteIds(updated)
            updated
        }
    }

    private fun loadFavoriteIds(): Set<Int> {
        val raw = settings.getStringOrNull(FAVORITE_IDS_KEY).orEmpty()
        if (raw.isBlank()) return emptySet()
        return raw.split(SEPARATOR)
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    private fun persistFavoriteIds(ids: Set<Int>) {
        settings[FAVORITE_IDS_KEY] = ids.sorted().joinToString(SEPARATOR)
    }

    companion object {
        private const val FAVORITE_IDS_KEY = "favorite_repo_ids"
        private const val SEPARATOR = ","
    }
}
