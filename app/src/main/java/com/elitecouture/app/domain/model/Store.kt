package com.elitecouture.app.domain.model

/**
 * Modelo de datos para una tienda física
 * 
 * @property id Identificador único de la tienda
 * @property name Nombre de la tienda
 * @property address Dirección completa de la tienda
 * @property phone Número de teléfono de contacto
 * @property hours Horario de atención
 * @property latitude Latitud de la ubicación (para el mapa)
 * @property longitude Longitud de la ubicación (para el mapa)
 */
data class Store(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String,
    val hours: String,
    val latitude: Double,
    val longitude: Double
)
