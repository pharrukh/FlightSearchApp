package com.normuradov.flightsearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.normuradov.flightsearchapp.ui.theme.FlightSearchAppTheme
import com.normuradov.flightsearchapp.ui.flight.FlightViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import com.normuradov.flightsearchapp.ui.flight.FlightUiState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlightSearchAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestScreen()
                }
            }
        }
    }
}

@Composable
fun TestScreen(
    modifier: Modifier = Modifier,
    viewModel: FlightViewModel = viewModel(factory = FlightViewModel.factory)
) {
    Column {

        val favorites by viewModel.getFavorites().collectAsState(emptyList())
        Text(text = favorites.size.toString(), style = MaterialTheme.typography.displayLarge)
        val flights by viewModel.getFlights().collectAsState(emptyList())
        LazyColumn {
            items(items = flights, key = { flight -> flight.departureCode + flight.arrivalCode }) {
                Card(modifier = modifier.padding(4.dp)) {
                    Row(modifier = modifier.padding(2.dp)) {
                        Column(modifier = modifier.weight(2f))
                        {
                            Text(text = it.favoriteId.toString())
                            Text(text = "FROM ${it.departureCode} - ${it.departureName}")
                            Text(text = "TO ${it.arrivalCode} - ${it.arrivalName}")
                        }

                        val coroutineScope = rememberCoroutineScope()
                        Box(modifier = modifier.weight(1f)) {
                            if (it.isFavorite) {
                                Button(onClick = {
                                    coroutineScope.launch {
                                        viewModel.removeFavorite(it)
                                    }
                                }) {
                                    Text(text = "Unlike")
                                }
                            } else {
                                Button(onClick = {
                                    coroutineScope.launch {
                                        viewModel.saveFavorite(it)
                                    }
                                }) {
                                    Text(text = "Like")
                                }
                            }
                        }
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