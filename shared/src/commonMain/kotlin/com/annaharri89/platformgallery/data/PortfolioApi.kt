package com.annaharri89.platformgallery.data

import com.annaharri89.platformgallery.util.platformLogDebug
import com.annaharri89.platformgallery.util.platformLogError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlin.coroutines.cancellation.CancellationException

interface PortfolioApi {
    suspend fun fetchProjects(): PortfolioFetchResult
}

class GithubPortfolioApi(private val client: HttpClient) : PortfolioApi {
    companion object {
        private const val LOG_TAG = "PlatformGallery/PortfolioApi"
        private const val USERNAME = "annaharri89"
        private const val API_URL = "https://api.github.com/users/$USERNAME/repos"
    }

    override suspend fun fetchProjects(): PortfolioFetchResult {
        return try {
            platformLogDebug(LOG_TAG, "Fetching repos for $USERNAME")
            val projects = client.get(API_URL) {
                header(HttpHeaders.Accept, "application/vnd.github+json")
                header(HttpHeaders.UserAgent, "PlatformGallery")
                parameter("sort", "updated")
                parameter("direction", "desc")
                parameter("per_page", "100")
                parameter("type", "owner")
            }.body<List<PortfolioProject>>()
                .filterNot { it.fork }
            platformLogDebug(LOG_TAG, "Fetched ${projects.size} non-fork repos")
            PortfolioFetchResult.Success(projects)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            platformLogError(LOG_TAG, "Failed to fetch GitHub repos", e)
            PortfolioFetchResult.Failure(
                message = e.message ?: "Could not load projects from GitHub",
                cause = e,
            )
        }
    }
}
