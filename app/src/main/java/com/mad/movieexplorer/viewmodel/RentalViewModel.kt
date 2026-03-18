package com.mad.movieexplorer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mad.movieexplorer.data.repository.RentalRepository
import com.mad.movieexplorer.domain.model.Movie
import com.mad.movieexplorer.domain.model.Rental
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RentalUiState(
    val rentals: List<Rental> = emptyList(),
    val totalPrice: Double = 0.0,
    val message: String? = null
)

class RentalViewModel(
    private val rentalRepository: RentalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RentalUiState())
    val uiState: StateFlow<RentalUiState> = _uiState.asStateFlow()

    init {
        observeRentals()
    }

    fun rentMovie(movie: Movie, days: Int) {
        viewModelScope.launch {
            val result = rentalRepository.rentMovie(movie, days)
            _uiState.update {
                it.copy(
                    message = result.fold(
                        onSuccess = { "${movie.title} added to rentals." },
                        onFailure = { throwable -> throwable.message ?: "Unable to rent movie." }
                    )
                )
            }
        }
    }

    fun increaseDays(rental: Rental) {
        viewModelScope.launch {
            rentalRepository.updateRentalDays(rental.id, rental.days + 1)
        }
    }

    fun decreaseDays(rental: Rental) {
        viewModelScope.launch {
            rentalRepository.updateRentalDays(rental.id, (rental.days - 1).coerceAtLeast(1))
        }
    }

    fun removeRental(rentalId: Long) {
        viewModelScope.launch {
            rentalRepository.deleteRental(rentalId)
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun observeRentals() {
        viewModelScope.launch {
            rentalRepository.rentals.collect { rentals ->
                _uiState.update {
                    it.copy(
                        rentals = rentals,
                        totalPrice = rentals.sumOf(Rental::totalPrice)
                    )
                }
            }
        }
    }
}
