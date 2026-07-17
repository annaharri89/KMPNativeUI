package com.annaharri89.platformgallery.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlin.coroutines.cancellation.CancellationException

interface PortfolioApi {
    suspend fun getProjects(): List<PortfolioProject>
}

class GithubPortfolioApi(private val client: HttpClient) : PortfolioApi {
    companion object {
        private const val USERNAME = "annaharri89"
        private const val API_URL = "https://api.github.com/users/$USERNAME/repos"
    }

    override suspend fun getProjects(): List<PortfolioProject> {
        return try {
            client.get(API_URL) {
                header(HttpHeaders.Accept, "application/vnd.github+json")
                header(HttpHeaders.UserAgent, "PlatformGallery")
                parameter("sort", "updated")
                parameter("direction", "desc")
                parameter("per_page", "100")
                parameter("type", "owner")
            }.body<List<PortfolioProject>>()
                .filterNot { it.fork }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emptyList()
        }
    }
}
