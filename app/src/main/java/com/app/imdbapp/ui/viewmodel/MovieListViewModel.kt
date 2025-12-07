package com.app.imdbapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.imdbapp.data.local.MovieDatabase
import com.app.imdbapp.data.model.Movie
import com.app.imdbapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MovieListUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class MovieListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(
        movieDao = MovieDatabase.getDatabase(application).movieDao(),
        context = application
    )

    private val _uiState = MutableStateFlow(MovieListUiState(isLoading = true))
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        loadTrendingMovies()
    }

    fun loadTrendingMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getTrendingMovies().collect { result ->
                result.getOrNull()?.let { movies ->
                    _uiState.value = _uiState.value.copy(
                        movies = movies,
                        isLoading = false,
                        error = null
                    )
                } ?: run {
                    val exception = result.exceptionOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception?.message ?: "Failed to load movies"
                    )
                }
            }
        }
    }

    fun searchMovies(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        viewModelScope.launch {
            if (query.isBlank()) {
                loadTrendingMovies()
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                repository.searchMovies(query).collect { result ->
                    result.getOrNull()?.let { movies ->
                        _uiState.value = _uiState.value.copy(
                            movies = movies,
                            isLoading = false,
                            error = null
                        )
                    } ?: run {
                        val exception = result.exceptionOrNull()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception?.message ?: "Search failed"
                        )
                    }
                }
            }
        }
    }
}