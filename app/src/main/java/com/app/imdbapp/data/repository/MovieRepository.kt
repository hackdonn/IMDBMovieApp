package com.app.imdbapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.app.imdbapp.BuildConfig
import com.app.imdbapp.data.local.MovieDao
import com.app.imdbapp.data.model.Movie
import com.app.imdbapp.data.model.MovieDetail
import com.app.imdbapp.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MovieRepository(
    private val movieDao: MovieDao,
    private val context: Context
) {

    private val apiService = RetrofitClient.tmdbApiService
    private val apiKey = BuildConfig.TMDB_API_KEY

    fun getTrendingMovies(): Flow<Result<List<Movie>>> = flow {
        try {
            if (isNetworkAvailable()) {
                // Fetch from API
                val response = withContext(Dispatchers.IO) {
                    apiService.getTrendingMovies(apiKey = apiKey)
                }
                val movies = response.results.take(20) // Limit to 20 items as per assignment

                // Save to database
                withContext(Dispatchers.IO) {
                    movieDao.deleteAllMovies()
                    movieDao.insertMovies(movies)
                }

                emit(Result.success(movies))
            } else {
                // Load from database
                val movies = withContext(Dispatchers.IO) {
                    movieDao.getAllMovies()
                }
                if (movies.isEmpty()) {
                    emit(Result.failure(Exception("No internet connection and no cached data available")))
                } else {
                    emit(Result.success(movies))
                }
            }
        } catch (e: Exception) {
            // Try to load from database on error
            val movies = withContext(Dispatchers.IO) {
                movieDao.getAllMovies()
            }
            if (movies.isEmpty()) {
                emit(Result.failure(e))
            } else {
                emit(Result.success(movies))
            }
        }
    }

    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail> {
        return try {
            if (isNetworkAvailable()) {
                val movieDetail = apiService.getMovieDetails(movieId, apiKey = apiKey)
                Result.success(movieDetail)
            } else {
                // Try to get from cache (basic movie data)
                val movie = movieDao.getMovieById(movieId)
                if (movie != null) {
                    // Convert Movie to MovieDetail
                    val movieDetail = MovieDetail(
                        id = movie.id,
                        title = movie.title,
                        overview = movie.overview,
                        posterPath = movie.posterPath
                    )
                    Result.success(movieDetail)
                } else {
                    Result.failure(Exception("No internet connection and movie not cached"))
                }
            }
        } catch (e: Exception) {
            // Try cache
            val movie = movieDao.getMovieById(movieId)
            if (movie != null) {
                val movieDetail = MovieDetail(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    posterPath = movie.posterPath
                )
                Result.success(movieDetail)
            } else {
                Result.failure(e)
            }
        }
    }

    fun searchMovies(query: String): Flow<Result<List<Movie>>> = flow {
        try {
            if (isNetworkAvailable()) {
                // Search from API - TMDB doesn't have a simple search in trending, so we'll search in cache first
                val movies = withContext(Dispatchers.IO) {
                    if (query.isBlank()) {
                        movieDao.getAllMovies()
                    } else {
                        movieDao.searchMovies(query)
                    }
                }
                emit(Result.success(movies))
            } else {
                // Search in database
                val movies = withContext(Dispatchers.IO) {
                    if (query.isBlank()) {
                        movieDao.getAllMovies()
                    } else {
                        movieDao.searchMovies(query)
                    }
                }
                emit(Result.success(movies))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}