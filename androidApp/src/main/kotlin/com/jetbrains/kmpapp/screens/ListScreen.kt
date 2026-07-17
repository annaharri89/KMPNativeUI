package com.jetbrains.kmpapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.R
import com.jetbrains.kmpapp.data.MuseumObject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navigateToDetails: (objectId: Int) -> Unit) {
    val viewModel: ListViewModel = koinViewModel()
    val objects by viewModel.objects.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val departmentFilter by viewModel.departmentFilter.collectAsStateWithLifecycle()
    val departments by viewModel.departments.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = viewModel::setSearchQuery,
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text(stringResource(R.string.search_artworks)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.back),
                                    )
                                }
                            }
                        },
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                content = {},
            )

            if (departments.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = departmentFilter == null,
                        onClick = { viewModel.setDepartmentFilter(null) },
                        label = { Text(stringResource(R.string.filter_all)) },
                    )
                    departments.forEach { department ->
                        FilterChip(
                            selected = departmentFilter == department,
                            onClick = {
                                viewModel.setDepartmentFilter(
                                    if (departmentFilter == department) null else department
                                )
                            },
                            label = {
                                Text(
                                    department,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                        )
                    }
                }
            }

            when {
                objects.isEmpty() && query.isEmpty() && departmentFilter == null -> {
                    EmptyScreenContent(Modifier.fillMaxSize())
                }
                objects.isEmpty() -> {
                    EmptyScreenContent(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.no_results),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                    ) {
                        items(objects, key = { it.objectID }) { obj ->
                            MuseumListRow(
                                obj = obj,
                                onClick = { navigateToDetails(obj.objectID) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MuseumListRow(
    obj: MuseumObject,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                obj.title.ifBlank { "Untitled" },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                buildString {
                    append(obj.artistDisplayName.ifBlank { "Unknown artist" })
                    if (obj.objectDate.isNotBlank()) {
                        append(" · ")
                        append(obj.objectDate)
                    }
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            AsyncImage(
                model = obj.primaryImageSmall,
                contentDescription = obj.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    )
}
