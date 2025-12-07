# IMDB App - Atlys Take Home Assignment

A modern Android movie app built with Jetpack Compose that displays trending movies from TMDB API.

## Demo Video

- Demo Video Link: [Watch Demo](https://drive.google.com/file/d/1IRvNYZKoOzkm-m9frP00CDrbs2xlYumD/view?usp=sharing)

## Features

- 📱 **Movie List Screen**: Display trending movies with beautiful UI
- 🔍 **Search Functionality**: Search movies by title
- 🎬 **Movie Details**: View detailed information about each movie
- 💾 **Offline Support**: Cache movie data locally for offline access
- 🔄 **Loading States**: Proper loading, empty, and error states
- 🎨 **Modern UI**: Built with Jetpack Compose and Material 3

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM with Repository Pattern
- **Navigation**: Jetpack Compose Navigation
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Local Database**: Room
- **Coroutines**: Kotlin Coroutines for async operations

## Setup Instructions

### 1. Get TMDB API Key

1. Register for a free account at [TMDB](https://www.themoviedb.org/)
2. Go to Settings → API → Request API Key
3. Generate your API key

### 2. Configure API Key

Add your TMDB API key to `local.properties` file (in the project root):

```properties
TMDB_API_KEY=your_api_key_here
```

**Note**: `local.properties` is already in `.gitignore`, so your API key won't be committed to version control.

### 3. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run the app on an emulator or device

## Project Structure

```
app/src/main/java/com/app/imdbapp/
├── data/
│   ├── local/           # Room database and DAO
│   ├── model/           # Data models
│   ├── remote/          # Retrofit API service
│   └── repository/      # Repository pattern implementation
├── navigation/          # Navigation setup
├── ui/
│   ├── screens/         # Compose screens
│   ├── theme/           # App theme
│   └── viewmodel/       # ViewModels
└── MainActivity.kt      # Main activity
```

## API Details

- **Base URL**: `https://api.themoviedb.org/3/`
- **Trending Movies Endpoint**: `/trending/movie/week`
- **Movie Details Endpoint**: `/movie/{movie_id}`
- **Image Base URL**: `https://image.tmdb.org/t/p/w500/`

## Assignment Requirements Coverage

✅ Movie list screen with trending movies  
✅ Movie detail screen  
✅ Search functionality  
✅ Jetpack Compose UI  
✅ Compose Navigation  
✅ Offline caching with Room  
✅ Loading, empty, and error states  
✅ Clean architecture (MVVM + Repository)  
✅ Kotlin best practices  

## Notes

- The app displays the first 20 trending movies as per assignment requirements
- Search is performed on cached movies (offline-first approach)
- Movie images are loaded using Coil library
- All network requests are handled with proper error handling and offline fallback

## License

This project is created for Atlys take-home assignment.