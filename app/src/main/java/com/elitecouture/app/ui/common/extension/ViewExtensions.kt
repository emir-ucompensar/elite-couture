package com.elitecouture.app.ui.common.extension

import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import com.elitecouture.app.util.Constants

/**
 * Extensiones útiles para trabajar con Views en Android.
 * 
 * Proporciona métodos de conveniencia para operaciones comunes:
 * - Visibilidad (show, hide, toggle)
 * - Animaciones (fadeIn, fadeOut, slideIn, slideOut)
 * - Clicks con debounce
 * - Margins dinámicos
 */

/**
 * Hace visible la vista (visibility = VISIBLE).
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Oculta la vista (visibility = GONE).
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Hace invisible la vista pero mantiene su espacio (visibility = INVISIBLE).
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Alterna la visibilidad entre VISIBLE y GONE.
 * 
 * @return true si quedó visible, false si quedó oculta
 */
fun View.toggle(): Boolean {
    isVisible = !isVisible
    return isVisible
}

/**
 * Muestra la vista con animación de fade in.
 * 
 * @param duration duración de la animación en milisegundos
 * @param onEnd callback opcional ejecutado al finalizar
 */
fun View.fadeIn(
    duration: Long = Constants.UI.ANIMATION_DURATION_MS,
    onEnd: (() -> Unit)? = null
) {
    if (visibility == View.VISIBLE && alpha == 1f) return
    
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction { onEnd?.invoke() }
        .start()
}

/**
 * Oculta la vista con animación de fade out.
 * 
 * @param duration duración de la animación en milisegundos
 * @param onEnd callback opcional ejecutado al finalizar
 */
fun View.fadeOut(
    duration: Long = Constants.UI.ANIMATION_DURATION_MS,
    onEnd: (() -> Unit)? = null
) {
    if (visibility == View.GONE || alpha == 0f) return
    
    animate()
        .alpha(0f)
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            visibility = View.GONE
            alpha = 1f // Resetear alpha para próxima vez
            onEnd?.invoke()
        }
        .start()
}

/**
 * Muestra la vista con animación de slide desde arriba.
 * 
 * @param duration duración de la animación en milisegundos
 */
fun View.slideInFromTop(duration: Long = Constants.UI.ANIMATION_DURATION_MS) {
    visibility = View.VISIBLE
    translationY = -height.toFloat()
    animate()
        .translationY(0f)
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .start()
}

/**
 * Oculta la vista con animación de slide hacia arriba.
 * 
 * @param duration duración de la animación en milisegundos
 */
fun View.slideOutToTop(duration: Long = Constants.UI.ANIMATION_DURATION_MS) {
    animate()
        .translationY(-height.toFloat())
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            visibility = View.GONE
            translationY = 0f // Resetear para próxima vez
        }
        .start()
}

/**
 * Muestra la vista con animación de slide desde abajo.
 * 
 * @param duration duración de la animación en milisegundos
 */
fun View.slideInFromBottom(duration: Long = Constants.UI.ANIMATION_DURATION_MS) {
    visibility = View.VISIBLE
    translationY = height.toFloat()
    animate()
        .translationY(0f)
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .start()
}

/**
 * Oculta la vista con animación de slide hacia abajo.
 * 
 * @param duration duración de la animación en milisegundos
 */
fun View.slideOutToBottom(duration: Long = Constants.UI.ANIMATION_DURATION_MS) {
    animate()
        .translationY(height.toFloat())
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            visibility = View.GONE
            translationY = 0f // Resetear para próxima vez
        }
        .start()
}

/**
 * Establece un click listener con debounce para prevenir clicks múltiples.
 * 
 * Útil para prevenir que el usuario haga click repetidamente en botones
 * que ejecutan operaciones costosas o navegación.
 * 
 * @param debounceTime tiempo mínimo entre clicks en milisegundos
 * @param action acción a ejecutar en el click
 */
fun View.setDebouncedClickListener(
    debounceTime: Long = 500L,
    action: (View) -> Unit
) {
    var lastClickTime = 0L
    setOnClickListener { view ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            action(view)
        }
    }
}

/**
 * Establece el margin de la vista dinámicamente.
 * 
 * @param left margen izquierdo en px
 * @param top margen superior en px
 * @param right margen derecho en px
 * @param bottom margen inferior en px
 */
fun View.setMargins(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    params.setMargins(left, top, right, bottom)
    layoutParams = params
}

/**
 * Establece el padding de la vista.
 * 
 * @param left padding izquierdo en px
 * @param top padding superior en px
 * @param right padding derecho en px
 * @param bottom padding inferior en px
 */
fun View.setPaddings(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setPadding(left, top, right, bottom)
}

/**
 * Habilita o deshabilita la vista y ajusta su alpha.
 * 
 * Cuando está deshabilitada, se reduce el alpha para indicar visualmente el estado.
 * 
 * @param enabled si la vista debe estar habilitada
 * @param disabledAlpha alpha a usar cuando está deshabilitada (default 0.5)
 */
fun View.setEnabledWithAlpha(enabled: Boolean, disabledAlpha: Float = 0.5f) {
    isEnabled = enabled
    alpha = if (enabled) 1f else disabledAlpha
}

/**
 * Ejecuta un bloque de código solo si la vista está visible.
 * 
 * @param block código a ejecutar
 */
inline fun View.doIfVisible(block: View.() -> Unit) {
    if (isVisible) {
        block()
    }
}

/**
 * Ejecuta un bloque de código solo si la vista está oculta.
 * 
 * @param block código a ejecutar
 */
inline fun View.doIfGone(block: View.() -> Unit) {
    if (visibility == View.GONE) {
        block()
    }
}

/**
 * Muestra un Snackbar con estilo personalizado (color de fondo magenta, texto blanco)
 * y anclado encima del BottomNavigationView si está presente.
 * 
 * @param message mensaje a mostrar
 * @param duration duración del Snackbar (por defecto LENGTH_SHORT)
 * @param actionText texto del botón de acción (opcional)
 * @param actionCallback callback a ejecutar cuando se hace clic en la acción (opcional)
 * @param onDismissed callback a ejecutar cuando el Snackbar se descarta sin acción (opcional)
 */
fun View.showStyledSnackbar(
    message: String,
    duration: Int = com.google.android.material.snackbar.Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    actionCallback: (() -> Unit)? = null,
    onDismissed: (() -> Unit)? = null
) {
    val snackbar = com.google.android.material.snackbar.Snackbar.make(this, message, duration)
    
    // Buscar el BottomNavigationView en la jerarquía para anclarlo correctamente
    val rootView = this.rootView
    val bottomNav = rootView.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
        com.elitecouture.app.R.id.bottom_navigation
    )
    
    // Si existe el BottomNavigationView, anclar el Snackbar encima de él
    if (bottomNav != null) {
        snackbar.anchorView = bottomNav
    }
    
    // Cambiar el color de fondo a magenta oscuro
    snackbar.view.setBackgroundColor(context.getColorCompat(com.elitecouture.app.R.color.color_primary))
    
    // Cambiar el color del texto a blanco
    val textView = snackbar.view.findViewById<android.widget.TextView>(
        com.google.android.material.R.id.snackbar_text
    )
    textView.setTextColor(context.getColorCompat(android.R.color.white))
    
    // Configurar acción si se proporciona
    if (actionText != null && actionCallback != null) {
        snackbar.setAction(actionText) {
            actionCallback.invoke()
        }
        // Color del texto de la acción en blanco
        snackbar.setActionTextColor(context.getColorCompat(android.R.color.white))
    }
    
    // Configurar callback de descarte si se proporciona
    if (onDismissed != null) {
        snackbar.addCallback(object : com.google.android.material.snackbar.Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: com.google.android.material.snackbar.Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                // Solo ejecutar onDismissed si el Snackbar se descartó por timeout o swipe, no por acción
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_CONSECUTIVE) {
                    onDismissed.invoke()
                }
            }
        })
    }
    
    // Aumentar la elevación para asegurar que esté encima de todo
    snackbar.view.elevation = 16f
    
    snackbar.show()
}
