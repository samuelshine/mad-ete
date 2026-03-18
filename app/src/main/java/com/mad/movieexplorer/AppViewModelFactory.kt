package com.mad.movieexplorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mad.movieexplorer.viewmodel.AuthViewModel
import com.mad.movieexplorer.viewmodel.FavouritesViewModel
import com.mad.movieexplorer.viewmodel.MovieViewModel
import com.mad.movieexplorer.viewmodel.RentalViewModel

class AppViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(appContainer.authRepository) as T
            }

            modelClass.isAssignableFrom(MovieViewModel::class.java) -> {
                MovieViewModel(appContainer.movieRepository) as T
            }

            modelClass.isAssignableFrom(RentalViewModel::class.java) -> {
                RentalViewModel(appContainer.rentalRepository) as T
            }

            modelClass.isAssignableFrom(FavouritesViewModel::class.java) -> {
                FavouritesViewModel(
                    favouritesRepository = appContainer.favouritesRepository,
                    movieRepository = appContainer.movieRepository
                ) as T
            }

            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
