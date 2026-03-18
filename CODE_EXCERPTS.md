# Movie Explorer Code Excerpts

## 1. Material Design 3 UI

Theme
```kotlin
package com.mad.movieexplorer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TealGreen,
    onPrimary = Night,
    primaryContainer = SurfaceHigh,
    onPrimaryContainer = WarmText,
    secondary = MintGlow,
    onSecondary = Night,
    tertiary = ColorWhite,
    onTertiary = Night,
    background = Night,
    onBackground = WarmText,
    surface = DeepOcean,
    onSurface = WarmText,
    surfaceVariant = SurfaceMid,
    onSurfaceVariant = MutedText,
    error = Danger
)

private val LightColorScheme = lightColorScheme(
    primary = TealGreen,
    onPrimary = Night,
    background = ColorWhite,
    onBackground = Night,
    surface = ColorWhite,
    onSurface = Night,
    surfaceVariant = ColorGray,
    onSurfaceVariant = DeepOcean
)

@Composable
fun MovieExplorerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
```

TopBar
```kotlin
@Composable
private fun HomeHeader(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(26.dp),
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 12.dp,
                    bottomEnd = 8.dp,
                    bottomStart = 3.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalMovies,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .align(androidx.compose.ui.Alignment.Center),
                    tint = Color(0xFF20160D)
                )
            }
            Text(
                text = "Movie Explorer",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            HeaderActionButton(
                icon = Icons.Outlined.Search,
                contentDescription = "Search",
                onClick = onSearchClick
            )
            HeaderActionButton(
                icon = Icons.Outlined.Person,
                contentDescription = "Profile",
                onClick = onProfileClick
            )
        }
    }
}
```

MovieCard
```kotlin
@Composable
fun MoviePosterCard(
    movie: Movie,
    isFavourite: Boolean,
    activeRental: Rental?,
    onToggleFavourite: () -> Unit,
    onRentClick: () -> Unit,
    onIncreaseDays: () -> Unit,
    onDecreaseDays: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(220.dp)
            .height(320.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DeepOcean.copy(alpha = 0.26f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = movie.title,
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Night.copy(alpha = 0.42f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatRating(movie.rating),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    RentalDayControl(
                        activeRental = activeRental,
                        onRentClick = onRentClick,
                        onIncreaseDays = onIncreaseDays,
                        onDecreaseDays = onDecreaseDays,
                        compact = true
                    )
                }
            }
        }
    }
}
```

## 2. API Integration and Movie Display

MovieApiService
```kotlin
package com.mad.movieexplorer.data.remote.api

import com.mad.movieexplorer.data.remote.dto.MovieResponseDto
import retrofit2.http.GET

interface MovieApiService {
    @GET("api/movies")
    suspend fun getMovies(): MovieResponseDto
}
```

MovieDto
```kotlin
data class MovieResponseDto(
    @SerializedName("data") val data: List<MovieDto> = emptyList()
)

data class MovieDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("year") val year: String = "",
    @SerializedName("rated") val rated: String = "",
    @SerializedName("released") val released: String = "",
    @SerializedName("runtime") val runtime: String = "",
    @SerializedName("genre") val genre: String = "",
    @SerializedName("director") val director: String = "",
    @SerializedName("writer") val writer: String = "",
    @SerializedName("actors") val actors: String = "",
    @SerializedName("plot") val plot: String = "",
    @SerializedName("language") val language: String = "",
    @SerializedName("country") val country: String = "",
    @SerializedName("awards") val awards: String = "",
    @SerializedName("poster") val poster: String = "",
    @SerializedName("imdbRating") val imdbRating: String = "",
    @SerializedName("imdbId") val imdbId: String = "",
    @SerializedName("boxOffice") val boxOffice: String = ""
)

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id.ifBlank { title },
        title = title.ifBlank { "Untitled Movie" },
        posterUrl = poster,
        rating = imdbRating.toDoubleOrNull() ?: 0.0,
        genres = genre
            .split(",")
            .map(String::trim)
            .filter(String::isNotEmpty),
        overview = plot.ifBlank { "Overview unavailable." },
        year = year,
        rated = rated,
        released = released,
        runtime = runtime,
        director = director,
        writer = writer,
        actors = actors,
        language = language,
        country = country,
        awards = awards,
        imdbId = imdbId,
        boxOffice = boxOffice
    )
}
```

MovieRepository
```kotlin
class MovieRepository(
    private val apiService: MovieApiService
) {
    private val cachedMovies = MutableStateFlow<List<Movie>>(emptyList())

    val movies: StateFlow<List<Movie>> = cachedMovies.asStateFlow()

    suspend fun refreshMovies(): Result<List<Movie>> {
        return try {
            val remoteMovies = apiService.getMovies().data.map { dto -> dto.toDomain() }
            val finalMovies = remoteMovies.ifEmpty { sampleMovies }
            cachedMovies.value = finalMovies
            Result.success(finalMovies)
        } catch (exception: Exception) {
            val fallbackMovies = cachedMovies.value.ifEmpty { sampleMovies }
            cachedMovies.value = fallbackMovies
            Result.failure(
                IllegalStateException(
                    "We couldn't refresh the catalog, so a fallback collection is being shown.",
                    exception
                )
            )
        }
    }

    fun getMovieById(id: String): Movie? = movies.value.firstOrNull { it.id == id }
}
```

MovieDisplayAndRentAction
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: MovieUiState,
    favouriteIds: Set<String>,
    rentalsByMovieId: Map<String, Rental>,
    onMovieClick: (String) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onRentMovie: (Movie) -> Unit,
    onIncreaseRentalDays: (Movie) -> Unit,
    onDecreaseRentalDays: (String) -> Unit,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(uiState.groupedMovies.toList(), key = { it.first }) { (genre, movies) ->
                Column {
                    Text(
                        text = genre,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyRow(
                        modifier = Modifier.padding(top = 14.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(movies, key = { it.id }) { movie ->
                            MoviePosterCard(
                                movie = movie,
                                isFavourite = movie.id in favouriteIds,
                                activeRental = rentalsByMovieId[movie.id],
                                onToggleFavourite = { onToggleFavourite(movie.id) },
                                onRentClick = { onRentMovie(movie) },
                                onIncreaseDays = { onIncreaseRentalDays(movie) },
                                onDecreaseDays = { onDecreaseRentalDays(movie.id) },
                                onClick = { onMovieClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
```

## 3. Room Database with MVVM Architecture

RentalEntity
```kotlin
@Entity(tableName = "rentals")
data class RentalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val movieId: String,
    val title: String,
    val posterUrl: String,
    val rating: Double,
    val pricePerDay: Double,
    val days: Int
)

fun RentalEntity.toDomain(): Rental {
    return Rental(
        id = id,
        movieId = movieId,
        title = title,
        posterUrl = posterUrl,
        rating = rating,
        pricePerDay = pricePerDay,
        days = days
    )
}
```

RentalDao
```kotlin
@Dao
interface RentalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRental(rental: RentalEntity): Long

    @Query("SELECT * FROM rentals ORDER BY title ASC")
    fun getAllRentals(): Flow<List<RentalEntity>>

    @Query("UPDATE rentals SET days = :days WHERE id = :id")
    suspend fun updateRentalDays(id: Long, days: Int)

    @Delete
    suspend fun deleteRental(rental: RentalEntity)

    @Query("SELECT * FROM rentals WHERE movieId = :movieId LIMIT 1")
    suspend fun getRentalByMovieId(movieId: String): RentalEntity?

    @Query("SELECT * FROM rentals WHERE id = :id LIMIT 1")
    suspend fun getRentalById(id: Long): RentalEntity?
}
```

RoomDatabase
```kotlin
@Database(
    entities = [RentalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieExplorerDatabase : RoomDatabase() {
    abstract fun rentalDao(): RentalDao

    companion object {
        @Volatile
        private var INSTANCE: MovieExplorerDatabase? = null

        fun getInstance(context: Context): MovieExplorerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MovieExplorerDatabase::class.java,
                    "movie_explorer.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
```

Repository
```kotlin
class RentalRepository(
    private val rentalDao: RentalDao
) {
    val rentals: Flow<List<Rental>> = rentalDao.getAllRentals().map { entities ->
        entities.map(RentalEntity::toDomain)
    }

    suspend fun rentMovie(movie: Movie, days: Int): Result<Unit> {
        if (days <= 0) {
            return Result.failure(IllegalArgumentException("Select at least one rental day."))
        }

        val existingRental = rentalDao.getRentalByMovieId(movie.id)
        if (existingRental == null) {
            rentalDao.insertRental(
                RentalEntity(
                    movieId = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterUrl,
                    rating = movie.rating,
                    pricePerDay = generatePrice(movie.id),
                    days = days
                )
            )
        } else {
            rentalDao.updateRentalDays(existingRental.id, existingRental.days + days)
        }
        return Result.success(Unit)
    }

    suspend fun updateRentalDays(rentalId: Long, days: Int) {
        val rental = rentalDao.getRentalById(rentalId) ?: return
        if (days <= 0) {
            rentalDao.deleteRental(rental)
        } else {
            rentalDao.updateRentalDays(rentalId, days)
        }
    }

    suspend fun deleteRental(rentalId: Long) {
        val rental = rentalDao.getRentalById(rentalId) ?: return
        rentalDao.deleteRental(rental)
    }
}
```

AppContainer
```kotlin
class AppContainer(context: Context) {
    private val database = MovieExplorerDatabase.getInstance(context)

    val authRepository: AuthRepository by lazy {
        AuthRepository(AuthPreferences(context))
    }

    val movieRepository: MovieRepository by lazy {
        MovieRepository(NetworkModule.movieApiService)
    }

    val favouritesRepository: FavouritesRepository by lazy {
        FavouritesRepository(FavouritesPreferences(context))
    }

    val rentalRepository: RentalRepository by lazy {
        RentalRepository(database.rentalDao())
    }
}
```

MovieViewModel
```kotlin
data class MovieUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val movies: List<Movie> = emptyList(),
    val groupedMovies: Map<String, List<Movie>> = emptyMap(),
    val searchQuery: String = "",
    val filteredMovies: List<Movie> = emptyList()
)

class MovieViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        observeMovies()
        refreshMovies()
    }

    fun refreshMovies(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = state.movies.isEmpty(),
                    isRefreshing = isPullToRefresh,
                    errorMessage = null
                )
            }

            val result = movieRepository.refreshMovies()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
```

RentalViewModel
```kotlin
data class RentalUiState(
    val rentals: List<Rental> = emptyList(),
    val rentalsByMovieId: Map<String, Rental> = emptyMap(),
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
            rentalRepository.updateRentalDays(rental.id, rental.days - 1)
        }
    }

    fun removeRental(rentalId: Long) {
        viewModelScope.launch {
            rentalRepository.deleteRental(rentalId)
        }
    }

    private fun observeRentals() {
        viewModelScope.launch {
            rentalRepository.rentals.collect { rentals ->
                _uiState.update {
                    it.copy(
                        rentals = rentals,
                        rentalsByMovieId = rentals.associateBy(Rental::movieId),
                        totalPrice = rentals.sumOf(Rental::totalPrice)
                    )
                }
            }
        }
    }
}
```

## 4. Rental Screen and CRUD Operations

RentalsScreen
```kotlin
@Composable
fun RentalsScreen(
    uiState: RentalUiState,
    onIncreaseDays: (Rental) -> Unit,
    onDecreaseDays: (Rental) -> Unit,
    onRemoveRental: (Long) -> Unit
) {
    if (uiState.rentals.isEmpty()) {
        EmptyStateView(
            title = "No active rentals",
            description = "Rent a movie from the catalog to start tracking pricing and reminders."
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 120.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(uiState.rentals, key = { it.id }) { rental ->
                RentalItemCard(
                    rental = rental,
                    onIncreaseDays = { onIncreaseDays(rental) },
                    onDecreaseDays = { onDecreaseDays(rental) },
                    onRemoveRental = { onRemoveRental(rental.id) }
                )
            }
        }

        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(uiState.totalPrice),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
```

RentalItemCard
```kotlin
@Composable
private fun RentalItemCard(
    rental: Rental,
    onIncreaseDays: () -> Unit,
    onDecreaseDays: () -> Unit,
    onRemoveRental: () -> Unit
) {
    GlassSurface(
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AsyncImage(
                model = rental.posterUrl,
                contentDescription = rental.title,
                modifier = Modifier
                    .size(width = 96.dp, height = 138.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rental.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${formatCurrency(rental.pricePerDay)} per day",
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Item total: ${formatCurrency(rental.totalPrice)}",
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onDecreaseDays) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = null
                        )
                    }
                    Text(
                        text = "${rental.days} day${if (rental.days == 1) "" else "s"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onIncreaseDays) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null
                        )
                    }
                    Box(modifier = Modifier.weight(1f))
                    IconButton(onClick = onRemoveRental) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
```

## 5. Background Processing using WorkManager

RentalReminderWorker
```kotlin
class RentalReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val rentals = MovieExplorerDatabase.getInstance(applicationContext)
            .rentalDao()
            .getAllRentals()
            .first()

        if (rentals.isNotEmpty() && notificationsAllowed()) {
            createNotificationChannel()
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Movie Explorer")
                .setContentText("You have active movie rentals")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "rental_reminder_worker"
        private const val CHANNEL_ID = "rental_reminder_channel"
        private const val NOTIFICATION_ID = 1001

        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<RentalReminderWorker>(
                repeatInterval = 12,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
```

WorkerSchedule
```kotlin
class MovieExplorerApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }

    override fun onCreate() {
        super.onCreate()
        RentalReminderWorker.schedule(this)
    }
}
```
