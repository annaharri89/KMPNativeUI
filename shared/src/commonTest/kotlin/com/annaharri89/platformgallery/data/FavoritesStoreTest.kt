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

        store.toggle(101L)
        assertTrue(store.isFavorite(101L))
        assertEquals(setOf(101L), store.favorites.value)

        store.toggle(101L)
        assertFalse(store.isFavorite(101L))
        assertEquals(emptySet<Long>(), store.favorites.value)
    }

    @Test
    fun favoritesSurviveStoreRecreation() {
        val settings = MapSettings()
        val firstStore = FavoritesStore(settings)
        firstStore.toggle(42L)
        firstStore.toggle(7L)

        val reloadedStore = FavoritesStore(settings)
        assertEquals(setOf(7L, 42L), reloadedStore.favorites.value)
        assertTrue(reloadedStore.isFavorite(42L))
        assertTrue(reloadedStore.isFavorite(7L))
    }
}
