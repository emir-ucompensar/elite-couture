package com.elitecouture.app.ui.common.extension

import android.util.Patterns
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extensiones útiles para trabajar con Strings.
 * 
 * Proporciona métodos de conveniencia para:
 * - Validaciones
 * - Formateo
 * - Transformaciones
 * - Truncado y ellipsis
 */

/**
 * Verifica si el string es un email válido.
 * 
 * @return true si es un email válido
 */
fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Verifica si el string es una contraseña segura.
 * 
 * Criterios:
 * - Mínimo 6 caracteres
 * - Al menos una letra
 * - Al menos un número
 * 
 * @return true si cumple los criterios de seguridad
 */
fun String.isSecurePassword(): Boolean {
    if (this.length < 6) return false
    val hasLetter = this.any { it.isLetter() }
    val hasDigit = this.any { it.isDigit() }
    return hasLetter && hasDigit
}

/**
 * Verifica si el string contiene solo letras.
 * 
 * @return true si solo contiene letras (sin números ni caracteres especiales)
 */
fun String.isAlphabetic(): Boolean {
    return this.all { it.isLetter() || it.isWhitespace() }
}

/**
 * Verifica si el string contiene solo números.
 * 
 * @return true si solo contiene dígitos
 */
fun String.isNumeric(): Boolean {
    return this.all { it.isDigit() }
}

/**
 * Capitaliza la primera letra del string.
 * 
 * Ejemplo: "hola mundo" -> "Hola mundo"
 * 
 * @return string con primera letra mayúscula
 */
fun String.capitalizeFirst(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

/**
 * Capitaliza la primera letra de cada palabra.
 * 
 * Ejemplo: "hola mundo" -> "Hola Mundo"
 * 
 * @return string con cada palabra capitalizada
 */
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.capitalizeFirst()
    }
}

/**
 * Trunca el string a una longitud máxima y agrega ellipsis.
 * 
 * Ejemplo: "Hola mundo".truncate(7) -> "Hola..."
 * 
 * @param maxLength longitud máxima
 * @param ellipsis sufijo a agregar (default: "...")
 * @return string truncado
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (this.length <= maxLength) {
        this
    } else {
        this.substring(0, maxLength - ellipsis.length) + ellipsis
    }
}

/**
 * Elimina todos los espacios en blanco del string.
 * 
 * @return string sin espacios
 */
fun String.removeWhitespace(): String {
    return this.replace("\\s".toRegex(), "")
}

/**
 * Elimina caracteres especiales, dejando solo letras, números y espacios.
 * 
 * @return string sanitizado
 */
fun String.removeSpecialCharacters(): String {
    return this.replace("[^A-Za-z0-9 ]".toRegex(), "")
}

/**
 * Convierte el string a formato de precio con símbolo de moneda.
 * 
 * Ejemplo: "1500".toPrice() -> "$1,500.00"
 * 
 * @param currencySymbol símbolo de moneda (default: "$")
 * @return string formateado como precio
 */
fun String.toPrice(currencySymbol: String = "$"): String {
    val number = this.toDoubleOrNull() ?: return this
    val formatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance("USD")
    }
    return formatter.format(number).replace("$", currencySymbol)
}

/**
 * Convierte un número a formato de precio con símbolo de moneda.
 * 
 * @param currencySymbol símbolo de moneda (default: "$")
 * @return string formateado como precio
 */
fun Double.toPrice(currencySymbol: String = "$"): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance("USD")
    }
    return formatter.format(this).replace("$", currencySymbol)
}

/**
 * Convierte un número Int a formato de precio.
 * 
 * @param currencySymbol símbolo de moneda (default: "$")
 * @return string formateado como precio
 */
fun Int.toPrice(currencySymbol: String = "$"): String {
    return this.toDouble().toPrice(currencySymbol)
}

/**
 * Formatea un timestamp (milisegundos) a string de fecha.
 * 
 * @param pattern patrón de formato (default: "dd/MM/yyyy")
 * @return fecha formateada
 */
fun Long.toDateString(pattern: String = "dd/MM/yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Formatea un timestamp (milisegundos) a string de fecha y hora.
 * 
 * @param pattern patrón de formato (default: "dd/MM/yyyy HH:mm")
 * @return fecha y hora formateada
 */
fun Long.toDateTimeString(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

/**
 * Enmascara un email para privacidad.
 * 
 * Ejemplo: "usuario@email.com" -> "us****@email.com"
 * 
 * @return email enmascarado
 */
fun String.maskEmail(): String {
    if (!this.isValidEmail()) return this
    
    val parts = this.split("@")
    if (parts.size != 2) return this
    
    val username = parts[0]
    val domain = parts[1]
    
    val maskedUsername = if (username.length <= 2) {
        username
    } else {
        username.take(2) + "*".repeat(username.length - 2)
    }
    
    return "$maskedUsername@$domain"
}

/**
 * Extrae las iniciales de un nombre completo.
 * 
 * Ejemplo: "Sofia Martinez" -> "SM"
 * 
 * @param maxInitials número máximo de iniciales (default: 2)
 * @return iniciales en mayúsculas
 */
fun String.toInitials(maxInitials: Int = 2): String {
    return this.trim()
        .split(" ")
        .take(maxInitials)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
}

/**
 * Verifica si el string contiene al menos una letra mayúscula.
 * 
 * @return true si contiene mayúsculas
 */
fun String.hasUpperCase(): Boolean {
    return this.any { it.isUpperCase() }
}

/**
 * Verifica si el string contiene al menos una letra minúscula.
 * 
 * @return true si contiene minúsculas
 */
fun String.hasLowerCase(): Boolean {
    return this.any { it.isLowerCase() }
}

/**
 * Verifica si el string contiene al menos un dígito.
 * 
 * @return true si contiene números
 */
fun String.hasDigit(): Boolean {
    return this.any { it.isDigit() }
}

/**
 * Verifica si el string contiene al menos un carácter especial.
 * 
 * @return true si contiene caracteres especiales
 */
fun String.hasSpecialChar(): Boolean {
    return this.any { !it.isLetterOrDigit() && !it.isWhitespace() }
}

/**
 * Pluraliza una palabra según la cantidad.
 * 
 * Ejemplo: pluralize(1, "producto", "productos") -> "1 producto"
 * Ejemplo: pluralize(5, "producto", "productos") -> "5 productos"
 * 
 * @param count cantidad
 * @param singular forma singular
 * @param plural forma plural
 * @return string con cantidad y palabra pluralizada
 */
fun pluralize(count: Int, singular: String, plural: String): String {
    return if (count == 1) "$count $singular" else "$count $plural"
}
