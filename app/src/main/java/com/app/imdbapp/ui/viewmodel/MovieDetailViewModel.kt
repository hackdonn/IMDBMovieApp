package com.app.imdbapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.imdbapp.data.local.MovieDatabase
import com.app.imdbapp.data.model.MovieDetail
import com.app.imdbapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MovieDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(
        movieDao = MovieDatabase.getDatabase(application).movieDao(),
        context = application
    )

    private val _uiState = MutableStateFlow(MovieDetailUiState(isLoading = true))
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getMovieDetails(movieId)
            result.getOrNull()?.let { movieDetail ->
                _uiState.value = _uiState.value.copy(
                    movieDetail = movieDetail,
                    isLoading = false,
                    error = null
                )
            } ?: run {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception?.message ?: "Failed to load movie details"
                )
            }
        }
    }
}