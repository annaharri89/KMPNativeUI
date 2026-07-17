package com.annaharri89.platformgallery

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.annaharri89.platformgallery.screens.DetailScreen
import com.annaharri89.platformgallery.screens.FavoritesScreen
import com.annaharri89.platformgallery.screens.ListScreen
import com.annaharri89.platformgallery.screens.PlatformTourScreen
import kotlinx.serialization.Serializable

@Serializable
object CollectionDestination

@Serializable
object FavoritesDestination

@Serializable
object TourDestination

@Serializable
data class DetailDestination(val projectId: Int)

private data class BottomNavItem(
    val route: Any,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = currentDestination?.route?.contains("DetailDestination") != true

            val bottomNavItems = listOf(
                BottomNavItem(CollectionDestination, R.string.tab_collection, Icons.Default.Home),
                BottomNavItem(FavoritesDestination, R.string.tab_favorites, Icons.Default.Favorite),
                BottomNavItem(TourDestination, R.string.tab_tour, Icons.Default.Info),
            )

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar {
                            bottomNavItems.forEach { item ->
                                val selected = currentDestination
                                    ?.hierarchy
                                    ?.any { it.route == item.route::class.qualifiedName } == true
                                    || currentDestination?.route?.contains(
                                        item.route::class.simpleName.orEmpty()
                                    ) == true

                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(item.icon, contentDescription = null) },
                                    label = { Text(stringResource(item.labelRes)) },
                                )
                            }
                        }
                    }
                },
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = CollectionDestination,
                    modifier = Modifier.padding(paddingValues),
                ) {
                    composable<CollectionDestination> {
                        ListScreen(
                            navigateToDetails = { projectId ->
                                navController.navigate(DetailDestination(projectId))
                            }
                        )
                    }
                    composable<FavoritesDestination> {
                        FavoritesScreen(
                            navigateToDetails = { projectId ->
                                navController.navigate(DetailDestination(projectId))
                            }
                        )
                    }
                    composable<TourDestination> {
                        PlatformTourScreen()
                    }
                    composable<DetailDestination> { backStackEntry ->
                        DetailScreen(
                            projectId = backStackEntry.toRoute<DetailDestination>().projectId,
                            navigateBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
