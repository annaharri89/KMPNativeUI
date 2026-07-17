package com.annaharri89.platformgallery.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioProject(
    val id: Int,
    val name: String,
    @SerialName("description") val summary: String? = null,
    val language: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int = 0,
    @SerialName("forks_count") val forksCount: Int = 0,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("updated_at") val updatedAt: String,
    val fork: Boolean = false,
    val license: LicenseInfo? = null,
    val owner: RepoOwner,
) {
    val avatarUrl: String get() = owner.avatarUrl
    val languageOrUnknown: String get() = language?.takeIf { it.isNotBlank() } ?: "Other"
    val starsAndForks: String get() = "★ $stargazersCount  ·  ⑂ $forksCount"
    val updatedDateLabel: String get() = updatedAt.take(10)
    val licenseName: String get() = license?.name.orEmpty()
    val ownerLogin: String get() = owner.login
}

@Serializable
data class RepoOwner(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
)

@Serializable
data class LicenseInfo(
    val name: String? = null,
)
