package com.elitecouture.app.ui.common.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Clase base para todas las Activities de la aplicación.
 * 
 * Proporciona funcionalidad común:
 * - Manejo automático de View Binding
 * - Método simplificado para mostrar Toast
 * - Lifecycle helpers
 * - Configuración común del toolbar
 * 
 * Soporta dos formas de uso:
 * 1. Con View Binding (recomendado)
 * 2. Con layout resource ID (tradicional)
 * 
 * Ejemplo con View Binding:
 * ```kotlin
 * class MainActivity : BaseActivity<ActivityMainBinding>() {
 *     override fun getViewBinding(): ActivityMainBinding {
 *         return ActivityMainBinding.inflate(layoutInflater)
 *     }
 *     
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         binding.toolbar.title = "Elite Couture"
 *     }
 * }
 * ```
 * 
 * Ejemplo sin View Binding (fallback):
 * ```kotlin
 * class SplashActivity : BaseActivity<ViewBinding>(R.layout.activity_splash) {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         findViewById<TextView>(R.id.title).text = "Splash"
 *     }
 * }
 * ```
 * 
 * @param VB tipo de ViewBinding para esta Activity
 * @param layoutResId ID del layout resource (opcional, para uso sin binding)
 */
abstract class BaseActivity<VB : ViewBinding>(
    @LayoutRes private val layoutResId: Int = 0
) : AppCompatActivity() {

    /**
     * Instancia del ViewBinding para acceder a las vistas.
     * 
     * Inicializado en onCreate a través de getViewBinding() o layoutResId.
     */
    private var _binding: VB? = null

    /**
     * Getter del ViewBinding.
     * 
     * Lanza excepción si se intenta acceder antes de onCreate.
     * 
     * @throws IllegalStateException si el binding aún no se inicializó
     */
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException(
            "ViewBinding solo está disponible después de onCreate"
        )

    /**
     * Indica si el binding está disponible para uso.
     */
    protected val isBindingAvailable: Boolean
        get() = _binding != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intentar usar ViewBinding primero
        try {
            _binding = getViewBinding()
            setContentView(_binding!!.root)
        } catch (e: NotImplementedError) {
            // Fallback: usar layout resource ID si fue proporcionado
            if (layoutResId != 0) {
                setContentView(layoutResId)
            } else {
                throw IllegalStateException(
                    "Debes implementar getViewBinding() o pasar un layoutResId al constructor"
                )
            }
        }

        setupViews()
        setupObservers()
    }

    /**
     * Implementar este método para proporcionar el ViewBinding.
     * 
     * Por defecto lanza NotImplementedError para permitir uso sin binding.
     * 
     * @return instancia del ViewBinding para esta Activity
     */
    protected open fun getViewBinding(): VB {
        throw NotImplementedError("Implementa getViewBinding() o usa constructor con layoutResId")
    }

    /**
     * Método para configurar las vistas de la Activity.
     * 
     * Llamado automáticamente en onCreate después de setContentView.
     * Override para inicializar vistas, listeners, toolbar, etc.
     */
    protected open fun setupViews() {
        // Por defecto no hace nada, las activities hijas pueden sobrescribir
    }

    /**
     * Método para configurar observers de LiveData/StateFlow.
     * 
     * Llamado automáticamente en onCreate después de setupViews.
     * Override para observar ViewModels, eventos, etc.
     */
    protected open fun setupObservers() {
        // Por defecto no hace nada, las activities hijas pueden sobrescribir
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar referencia del binding
        _binding = null
    }

    /**
     * Muestra un Toast con duración corta.
     * 
     * Método de conveniencia para mostrar mensajes al usuario.
     * 
     * @param message el mensaje a mostrar
     */
    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Muestra un Toast con duración larga.
     * 
     * Útil para mensajes importantes que requieren más tiempo de lectura.
     * 
     * @param message el mensaje a mostrar
     */
    protected fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Muestra un Toast con un string resource.
     * 
     * @param stringResId ID del string resource
     */
    protected fun showToast(stringResId: Int) {
        Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
    }

    /**
     * Configura el soporte de ActionBar con un toolbar personalizado.
     * 
     * Método helper para facilitar configuración de toolbar.
     * 
     * @param toolbar el toolbar a usar como ActionBar
     * @param showBackButton si se debe mostrar botón de navegación atrás
     */
    protected fun setupToolbar(toolbar: androidx.appcompat.widget.Toolbar, showBackButton: Boolean = false) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
        supportActionBar?.setDisplayShowHomeEnabled(showBackButton)
    }

    /**
     * Maneja el botón back del sistema.
     * 
     * Por defecto llama a finish().
     * Override para comportamiento personalizado.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
