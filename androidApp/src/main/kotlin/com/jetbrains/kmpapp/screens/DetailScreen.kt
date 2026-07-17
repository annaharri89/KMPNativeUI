package com.jetbrains.kmpapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.R
import com.jetbrains.kmpapp.data.MuseumObject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailScreen(objectId: Int, navigateBack: () -> Unit) {
    val viewModel: DetailViewModel = koinViewModel()
    val obj by viewModel.museumObject.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    LaunchedEffect(objectId) {
        viewModel.setId(objectId)
    }

    if (obj == null) {
        EmptyScreenContent(Modifier.fillMaxSize())
    } else {
        ObjectDetails(
            obj = obj!!,
            isFavorite = isFavorite,
            onBackClick = navigateBack,
            onToggleFavorite = viewModel::toggleFavorite,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObjectDetails(
    obj: MuseumObject,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.artwork_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.remove_favorite else R.string.save_favorite
                    ),
                )
            }
        },
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AsyncImage(
                model = obj.primaryImageSmall,
                contentDescription = obj.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                obj.title.ifBlank { "Untitled" },
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                obj.artistDisplayName.ifBlank { "Unknown artist" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))

            DetailCard(title = stringResource(R.string.artwork_details)) {
                LabeledField(stringResource(R.string.label_date), obj.objectDate)
                LabeledField(stringResource(R.string.label_medium), obj.medium)
                LabeledField(stringResource(R.string.label_dimensions), obj.dimensions)
            }

            Spacer(Modifier.height(12.dp))

            DetailCard(title = stringResource(R.string.collection_info)) {
                LabeledField(stringResource(R.string.label_department), obj.department)
                LabeledField(stringResource(R.string.label_repository), obj.repository)
                LabeledField(stringResource(R.string.label_credits), obj.creditLine)
            }

            Spacer(Modifier.height(88.dp))
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun LabeledField(label: String, value: String) {
    if (value.isBlank()) return
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
