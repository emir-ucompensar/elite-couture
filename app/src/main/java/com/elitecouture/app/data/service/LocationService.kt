package com.elitecouture.app.data.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Servicio singleton para gestionar operaciones de geolocalización.
 * 
 * Funcionalidades:
 * - Obtener ubicación actual del dispositivo
 * - Convertir coordenadas a dirección legible (Geocoding inverso)
 * - Verificar y solicitar permisos de ubicación
 * 
 * Utiliza FusedLocationProviderClient de Google Play Services para
 * obtener ubicaciones precisas con eficiencia de batería.
 */
object LocationService {
    
    private const val TAG = "LocationService"
    private const val LOCATION_REQUEST_CODE = 1001
    private const val TIMEOUT_MILLIS = 10_000L // 10 segundos
    
    /**
     * Verifica si los permisos de ubicación están concedidos.
     * 
     * @param context Contexto de la aplicación
     * @return true si los permisos están concedidos, false en caso contrario
     */
    fun checkLocationPermissions(context: Context): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        return fineLocationGranted && coarseLocationGranted
    }
    
    /**
     * Solicita los permisos de ubicación al usuario.
     * 
     * @param activity Activity desde donde se solicitan los permisos
     */
    fun requestLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }
    
    /**
     * Obtiene la ubicación actual del dispositivo.
     * 
     * Esta función es suspendible y debe ser llamada desde una coroutine.
     * Utiliza FusedLocationProviderClient para obtener la mejor ubicación
     * disponible con el menor consumo de batería.
     * 
     * @param context Contexto de la aplicación
     * @return Par de coordenadas (latitud, longitud) o null si falla
     * @throws SecurityException Si no se tienen los permisos necesarios
     * @throws TimeoutCancellationException Si se excede el timeout de 10 segundos
     */
    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        // Verificar permisos
        if (!checkLocationPermissions(context)) {
            Log.e(TAG, "Permisos de ubicación no concedidos")
            return null
        }
        
        return try {
            withTimeout(TIMEOUT_MILLIS) {
                suspendCancellableCoroutine { continuation ->
                    val fusedLocationClient: FusedLocationProviderClient = 
                        LocationServices.getFusedLocationProviderClient(context)
                    
                    val cancellationTokenSource = CancellationTokenSource()
                    
                    // Configurar el callback de cancelación
                    continuation.invokeOnCancellation {
                        cancellationTokenSource.cancel()
                    }
                    
                    try {
                        // Solicitar ubicación actual con alta precisión
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cancellationTokenSource.token
                        ).addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                Log.d(TAG, "Ubicación obtenida: ${location.latitude}, ${location.longitude}")
                                continuation.resume(Pair(location.latitude, location.longitude))
                            } else {
                                Log.e(TAG, "Location es null")
                                continuation.resume(null)
                            }
                        }.addOnFailureListener { exception ->
                            Log.e(TAG, "Error al obtener ubicación", exception)
                            continuation.resume(null)
                        }
                    } catch (e: SecurityException) {
                        Log.e(TAG, "SecurityException al obtener ubicación", e)
                        continuation.resume(null)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en getCurrentLocation", e)
            null
        }
    }
    
    /**
     * Convierte coordenadas geográficas en una dirección legible.
     * 
     * Utiliza Geocoder para realizar geocoding inverso (coordenadas -> dirección).
     * 
     * @param context Contexto de la aplicación
     * @param latitude Latitud de la ubicación
     * @param longitude Longitud de la ubicación
     * @return Dirección formateada como String o null si falla
     */
    suspend fun getAddressFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? {
        return try {
            // Verificar disponibilidad de Geocoder
            if (!Geocoder.isPresent()) {
                Log.e(TAG, "Geocoder no está disponible en este dispositivo")
                return null
            }
            
            val geocoder = Geocoder(context, Locale.getDefault())
            
            // Android 13+ (API 33) usa callback asíncrono
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = formatAddress(addresses[0])
                            Log.d(TAG, "Dirección obtenida: $address")
                            continuation.resume(address)
                        } else {
                            Log.e(TAG, "No se encontraron direcciones")
                            continuation.resume(null)
                        }
                    }
                }
            } else {
                // API < 33 usa método síncrono (deprecado pero necesario)
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = formatAddress(addresses[0])
                    Log.d(TAG, "Dirección obtenida: $address")
                    address
                } else {
                    Log.e(TAG, "No se encontraron direcciones")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener dirección desde coordenadas", e)
            null
        }
    }
    
    /**
     * Formatea un objeto Address en un String legible.
     * 
     * Construye una dirección concatenando los componentes disponibles:
     * - Nombre de la vía (thoroughfare)
     * - Número de edificio (subThoroughfare)
     * - Localidad (locality)
     * - País (countryName)
     * 
     * @param address Objeto Address del Geocoder
     * @return Dirección formateada como String
     */
    private fun formatAddress(address: Address): String {
        val addressParts = mutableListOf<String>()
        
        // Agregar nombre de la vía y número
        address.thoroughfare?.let { addressParts.add(it) }
        address.subThoroughfare?.let { 
            if (addressParts.isNotEmpty()) {
                addressParts[addressParts.size - 1] = "${addressParts.last()} $it"
            } else {
                addressParts.add(it)
            }
        }
        
        // Agregar localidad/ciudad
        address.locality?.let { addressParts.add(it) }
        
        // Agregar país
        address.countryName?.let { addressParts.add(it) }
        
        return if (addressParts.isNotEmpty()) {
            addressParts.joinToString(", ")
        } else {
            // Fallback: usar la primera línea de dirección
            address.getAddressLine(0) ?: "Dirección desconocida"
        }
    }
    
    /**
     * Código de solicitud de permisos.
     * Usado para identificar la respuesta en onRequestPermissionsResult.
     */
    fun getLocationRequestCode(): Int = LOCATION_REQUEST_CODE
}
