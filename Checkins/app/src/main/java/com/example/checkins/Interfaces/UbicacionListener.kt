package com.example.checkins.Interfaces

import com.google.android.gms.location.LocationResult

interface UbicacionListener {
    fun ubicacionResponse(locationResult:LocationResult)
}