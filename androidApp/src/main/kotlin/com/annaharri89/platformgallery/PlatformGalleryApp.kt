package com.annaharri89.platformgallery

import android.app.Application
import com.annaharri89.platformgallery.di.initKoin
import com.annaharri89.platformgallery.screens.DetailViewModel
import com.annaharri89.platformgallery.screens.FavoritesViewModel
import com.annaharri89.platformgallery.screens.ListViewModel
import org.koin.dsl.module

class PlatformGalleryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            listOf(
                module {
                    factory { ListViewModel(get()) }
                    factory { DetailViewModel(get(), get()) }
                    factory { FavoritesViewModel(get(), get()) }
                }
            )
        )
    }
}
