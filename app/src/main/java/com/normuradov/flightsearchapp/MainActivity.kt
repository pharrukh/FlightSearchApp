package com.normuradov.flightsearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.normuradov.flightsearchapp.ui.theme.FlightSearchAppTheme
import com.normuradov.flightsearchapp.ui.flight.FlightViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.normuradov.flightsearchapp.ui.flight.Flight
import com.normuradov.flightsearchapp.ui.flight.HomeScreenMode
import com.normuradov.flightsearchapp.ui.flight.UiState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: FlightViewModel by viewModels {
        FlightViewModel.factory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                setContent {
                    FlightSearchAppTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            HomeScreen(uiState = uiState, viewModel = viewModel)
                        }
                    }
                }
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: UiState,
    modifier: Modifier = Modifier,
    viewModel: FlightViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val text by viewModel.text.collectAsState()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            modifier = modifier
                .padding(4.dp)
                .width(350.dp),
            value = text, onValueChange = { viewModel.updateText(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (uiState.mode == HomeScreenMode.SearchForAirport) {
            LazyColumn {
                items(
                    items = uiState.airports,
                    key = { flight -> flight.id }) {
                    Row(modifier = modifier.clickable {
                        coroutineScope.launch {
                            viewModel.getFlightsBasedOn(it)
                            viewModel.showFlights()
                        }
                    }) {
                        Text(text = "${it.code} ${it.name}")
                    }
                }
            }
        } else if (uiState.mode == HomeScreenMode.ShowSearchResults) {
            FlightColumn(flights = uiState.searchedFlights, viewModel = viewModel)
        } else if (uiState.mode == HomeScreenMode.ShowFavorites) {
            FlightColumn(flights = uiState.favoriteFlights, viewModel = viewModel)
        }
    }
}

@Composable
fun FlightColumn(modifier: Modifier = Modifier, flights: List<Flight>, viewModel: FlightViewModel) {
    LazyColumn {
        items(
            items = flights,
            key = { flight -> flight.departureCode + flight.arrivalCode }) {
            FlightCard(flight = it, viewModel = viewModel)
        }
    }
}

@Composable
fun FlightCard(modifier: Modifier = Modifier, flight: Flight, viewModel: FlightViewModel) {
    Card(modifier = modifier.padding(4.dp)) {
        Row(
            modifier = modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = modifier.weight(2f))
            {
                Text(text = "DEPARTURE", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${flight.departureCode} - ${flight.departureName}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "ARRIVAL", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${flight.arrivalCode} - ${flight.arrivalName}",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            val coroutineScope = rememberCoroutineScope()
            Box(
                modifier = modifier
                    .weight(1f)
                    .height(100.dp)
            ) {
                if (flight.isFavorite) {
                    IconButton(modifier = modifier.fillMaxSize(),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.removeFavorite(flight)
                            }
                        }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_star_24),
                            contentDescription = "unlike",
                            modifier = modifier.fillMaxSize(),
                        )
                    }
                } else {
                    IconButton(modifier = modifier.fillMaxSize(),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.saveFavorite(flight)
                            }
                        }) {
                        Icon(
                            painterResource(id = R.drawable.outline_star_outline_24),
                            contentDescription = "like",
                            modifier = modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlightSearchAppTheme {
    }
}