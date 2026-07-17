package com.annaharri89.platformgallery.data

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesStoreTest {
    @Test
    fun toggleAddsAndRemovesFavoriteIds() {
        val store = FavoritesStore(MapSettings())

        store.toggle(101)
        assertTrue(store.isFavorite(101))
        assertEquals(setOf(101), store.favorites.value)

        store.toggle(101)
        assertFalse(store.isFavorite(101))
        assertEquals(emptySet(), store.favorites.value)
    }

    @Test
    fun favoritesSurviveStoreRecreation() {
        val settings = MapSettings()
        val firstStore = FavoritesStore(settings)
        firstStore.toggle(42)
        firstStore.toggle(7)

        val reloadedStore = FavoritesStore(settings)
        assertEquals(setOf(7, 42), reloadedStore.favorites.value)
        assertTrue(reloadedStore.isFavorite(42))
        assertTrue(reloadedStore.isFavorite(7))
    }
}
