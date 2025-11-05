package com.elitecouture.app.ui.common.extension

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/**
 * Extensiones útiles para trabajar con Context en Android.
 * 
 * Proporciona métodos de conveniencia para:
 * - Acceso a recursos (colores, drawables, dimensiones, strings)
 * - Conversión de unidades (dp <-> px)
 * - Conectividad de red
 * - Teclado virtual
 * - Navegación entre activities
 */

/**
 * Obtiene un color desde los recursos.
 * 
 * @param colorResId ID del recurso de color
 * @return color en formato int
 */
fun Context.getColorCompat(@ColorRes colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}

/**
 * Obtiene un drawable desde los recursos.
 * 
 * @param drawableResId ID del recurso drawable
 * @return drawable o null si no existe
 */
fun Context.getDrawableCompat(@DrawableRes drawableResId: Int) =
    ContextCompat.getDrawable(this, drawableResId)

/**
 * Obtiene una dimensión desde los recursos.
 * 
 * @param dimenResId ID del recurso de dimensión
 * @return dimensión en píxeles
 */
fun Context.getDimen(@DimenRes dimenResId: Int): Float {
    return resources.getDimension(dimenResId)
}

/**
 * Obtiene una dimensión entera desde los recursos.
 * 
 * @param dimenResId ID del recurso de dimensión
 * @return dimensión en píxeles como int
 */
fun Context.getDimenInt(@DimenRes dimenResId: Int): Int {
    return resources.getDimensionPixelSize(dimenResId)
}

/**
 * Convierte DP (density-independent pixels) a píxeles.
 * 
 * @param dp valor en dp
 * @return valor equivalente en píxeles
 */
fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    ).toInt()
}

/**
 * Convierte DP (density-independent pixels) a píxeles (versión para Int).
 * 
 * @param dp valor en dp
 * @return valor equivalente en píxeles
 */
fun Context.dpToPx(dp: Int): Int = dpToPx(dp.toFloat())

/**
 * Convierte píxeles a DP (density-independent pixels).
 * 
 * @param px valor en píxeles
 * @return valor equivalente en dp
 */
fun Context.pxToDp(px: Float): Int {
    return (px / resources.displayMetrics.density).toInt()
}

/**
 * Convierte píxeles a DP (versión para Int).
 * 
 * @param px valor en píxeles
 * @return valor equivalente en dp
 */
fun Context.pxToDp(px: Int): Int = pxToDp(px.toFloat())

/**
 * Convierte SP (scale-independent pixels) a píxeles.
 * 
 * Útil para tamaños de texto.
 * 
 * @param sp valor en sp
 * @return valor equivalente en píxeles
 */
fun Context.spToPx(sp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        resources.displayMetrics
    ).toInt()
}

/**
 * Muestra un Toast con mensaje String.
 * 
 * @param message mensaje a mostrar
 * @param duration duración (default: LENGTH_SHORT)
 */
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Muestra un Toast con mensaje desde recursos.
 * 
 * @param messageResId ID del string resource
 * @param duration duración (default: LENGTH_SHORT)
 */
fun Context.toast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageResId, duration).show()
}

/**
 * Muestra un Toast largo con mensaje String.
 * 
 * @param message mensaje a mostrar
 */
fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Muestra un Toast largo con mensaje desde recursos.
 * 
 * @param messageResId ID del string resource
 */
fun Context.longToast(@StringRes messageResId: Int) {
    Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
}

/**
 * Verifica si hay conexión a internet disponible.
 * 
 * Requiere permiso: ACCESS_NETWORK_STATE
 * 
 * @return true si hay conexión, false en caso contrario
 */
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        ?: return false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo
        @Suppress("DEPRECATION")
        return networkInfo?.isConnected == true
    }
}

/**
 * Oculta el teclado virtual.
 * 
 * @param windowToken token de la ventana actual
 */
fun Context.hideKeyboard(windowToken: android.os.IBinder) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Muestra el teclado virtual.
 */
fun Context.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

/**
 * Navega a una Activity con intent.
 * 
 * @param T tipo de Activity de destino
 * @param extras pares clave-valor opcionales para pasar en el intent
 */
inline fun <reified T : Any> Context.navigateTo(vararg extras: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java).apply {
        extras.forEach { (key, value) ->
            when (value) {
                is String -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is Long -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Float -> putExtra(key, value)
                is Double -> putExtra(key, value)
                // Agregar más tipos según necesidad
            }
        }
    }
    startActivity(intent)
}

/**
 * Navega a una Activity y limpia el back stack.
 * 
 * Útil para navegación después de login/logout.
 * 
 * @param T tipo de Activity de destino
 */
inline fun <reified T : Any> Context.navigateToAndClearStack() {
    val intent = Intent(this, T::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}

/**
 * Obtiene el ancho de la pantalla en píxeles.
 * 
 * @return ancho de la pantalla
 */
fun Context.getScreenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

/**
 * Obtiene el alto de la pantalla en píxeles.
 * 
 * @return alto de la pantalla
 */
fun Context.getScreenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * Verifica si el dispositivo está en modo oscuro.
 * 
 * @return true si está en modo oscuro
 */
fun Context.isDarkMode(): Boolean {
    return resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
}
