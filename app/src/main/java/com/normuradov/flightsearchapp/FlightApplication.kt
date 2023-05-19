package com.normuradov.flightsearchapp

import android.app.Application
import com.normuradov.flightsearchapp.data.AppContainer
import com.normuradov.flightsearchapp.data.AppDataContainer

class FlightApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}