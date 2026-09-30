package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.StreamItem
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.MoviePosterCard
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.DarkBg
import com.example.ui.theme.TextMuted

@Composable
fun VodMoviesScreen(
    movies: List<StreamItem>,
    categories: List<String>,
    strings: AppStrings.Strings,
    onPlayMovie: (StreamItem) -> Unit,
    onToggleFavorite: (StreamItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filteredMovies = remember(movies, selectedCategory) {
        if (selectedCategory == null) movies
        else movies.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        if (categories.isNotEmpty()) {
            CategoryFilterRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onSelectCategory = { selectedCategory = it },
                allLabel = strings.allCategories
            )
        }

        if (movies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.noChannelsFound,
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 145.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMovies, key = { it.id }) { movie ->
                    MoviePosterCard(
                        stream = movie,
                        onPlay = { onPlayMovie(movie) },
                        onToggleFavorite = { onToggleFavorite(movie) }
                    )
                }
            }
        }
    }
}
