package cl.cvega.android.evaluacionunidad3.data.ubicacion

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class ObtenerUbicacion {

    private var clientUbi: FusedLocationProviderClient? = null

    fun obtenerUbicacion(
        contexto: Context,
        alObtenerUbicacion: (ubicacion: Location) -> Unit,
        alFallarObtencion: (excepcion: Exception) -> Unit,

    ) {
        try {
            clientUbi = LocationServices.getFusedLocationProviderClient(contexto)

            val solicitudUbicacion = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

            val callbackUbicacion = object : LocationCallback() {
                override fun onLocationResult(resultadoUbicacion: LocationResult) {
                    super.onLocationResult(resultadoUbicacion)
                    if (resultadoUbicacion.locations.isNotEmpty()) {
                        alObtenerUbicacion(resultadoUbicacion.locations.first())
                        // Puedes detener las actualizaciones si solo necesitas una ubicación
                        detenerActualizacionesDeUbicacion()
                    }
                }

                override fun onLocationAvailability(disponibilidadUbicacion: LocationAvailability) {
                    // Aquí puedes manejar la disponibilidad de la ubicación si es necesario
                }
            }

            clientUbi?.requestLocationUpdates(solicitudUbicacion, callbackUbicacion, null)
        } catch (excepcion: SecurityException) {
            alFallarObtencion(excepcion)
        }
    }

    private fun detenerActualizacionesDeUbicacion() {
        clientUbi?.removeLocationUpdates(object : LocationCallback() {})

    }
}