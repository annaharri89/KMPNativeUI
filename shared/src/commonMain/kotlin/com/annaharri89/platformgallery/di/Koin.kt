package com.annaharri89.platformgallery.di

import com.annaharri89.platformgallery.data.FavoritesStore
import com.annaharri89.platformgallery.data.GithubPortfolioApi
import com.annaharri89.platformgallery.data.InMemoryPortfolioStorage
import com.annaharri89.platformgallery.data.PortfolioApi
import com.annaharri89.platformgallery.data.PortfolioRepository
import com.annaharri89.platformgallery.data.PortfolioStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    single<PortfolioApi> { GithubPortfolioApi(get()) }
    single<PortfolioStorage> { InMemoryPortfolioStorage() }
    single { FavoritesStore() }
    single {
        PortfolioRepository(get(), get()).apply {
            initialize()
        }
    }
}

fun initKoin() = initKoin(emptyList())

fun initKoin(extraModules: List<Module>) {
    startKoin {
        modules(
            dataModule,
            *extraModules.toTypedArray(),
        )
    }
}
