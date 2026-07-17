package com.annaharri89.platformgallery.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

expect fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient
