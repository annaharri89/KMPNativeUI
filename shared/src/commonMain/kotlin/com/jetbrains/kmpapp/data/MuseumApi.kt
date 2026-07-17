package com.jetbrains.kmpapp.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException

interface MuseumApi {
    suspend fun getData(): List<MuseumObject>
}

class KtorMuseumApi(private val client: HttpClient) : MuseumApi {
    companion object {
        private const val API_URL =
            "https://raw.githubusercontent.com/Kotlin/KMP-App-Template-Native/main/list.json"
    }

    override suspend fun getData(): List<MuseumObject> {
        return try {
            client.get(API_URL).body<List<MuseumObject>>().map { it.withStableImageUrls() }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            emptyList()
        }
    }
}

/**
 * Met CDN paths in the sample JSON go stale; IIIF main-image URLs stay valid.
 */
private fun MuseumObject.withStableImageUrls(): MuseumObject {
    val imageUrl =
        "https://collectionapi.metmuseum.org/api/collection/v1/iiif/$objectID/main-image"
    return copy(primaryImage = imageUrl, primaryImageSmall = imageUrl)
}