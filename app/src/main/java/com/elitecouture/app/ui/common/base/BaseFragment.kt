package com.elitecouture.app.ui.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Clase base para todos los Fragments de la aplicación.
 * 
 * Proporciona funcionalidad común:
 * - Manejo automático de View Binding con lazy initialization
 * - Método simplificado para mostrar Toast
 * - Lifecycle helpers
 * - Prevención de memory leaks limpiando binding en onDestroyView
 * 
 * Soporta dos formas de uso:
 * 1. Con View Binding (recomendado)
 * 2. Con layout resource ID (tradicional)
 * 
 * Ejemplo con View Binding:
 * ```kotlin
 * class LoginFragment : BaseFragment<FragmentLoginBinding>() {
 *     override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
 *         return FragmentLoginBinding.inflate(inflater, container, false)
 *     }
 *     
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         binding.loginButton.setOnClickListener { /* ... */ }
 *     }
 * }
 * ```
 * 
 * Ejemplo sin View Binding (fallback):
 * ```kotlin
 * class StoreFragment : BaseFragment<ViewBinding>(R.layout.fragment_feature_store) {
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         view.findViewById<Button>(R.id.button).setOnClickListener { /* ... */ }
 *     }
 * }
 * ```
 * 
 * @param VB tipo de ViewBinding para este Fragment
 * @param layoutResId ID del layout resource (opcional, para uso sin binding)
 */
abstract class BaseFragment<VB : ViewBinding>(
    @LayoutRes private val layoutResId: Int = 0
) : Fragment() {

    /**
     * Instancia del ViewBinding para acceder a las vistas.
     * 
     * Privado para forzar el uso del getter [binding] que previene acceso
     * cuando el binding ya fue destruido.
     */
    private var _binding: VB? = null

    /**
     * Getter seguro del ViewBinding.
     * 
     * Lanza excepción si se intenta acceder después de onDestroyView.
     * Esto previene crashes y facilita debugging.
     * 
     * @throws IllegalStateException si el binding ya fue destruido
     */
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException(
            "ViewBinding solo está disponible entre onCreateView y onDestroyView"
        )

    /**
     * Indica si el binding está disponible para uso.
     * 
     * Útil para validar en métodos que pueden ejecutarse después de onDestroyView.
     */
    protected val isBindingAvailable: Boolean
        get() = _binding != null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Si se implementó getViewBinding, usar eso
        return try {
            _binding = getViewBinding(inflater, container)
            _binding?.root
        } catch (e: NotImplementedError) {
            // Fallback: usar layout resource ID si fue proporcionado
            if (layoutResId != 0) {
                inflater.inflate(layoutResId, container, false)
            } else {
                throw IllegalStateException(
                    "Debes implementar getViewBinding() o pasar un layoutResId al constructor"
                )
            }
        }
    }

    /**
     * Implementar este método para proporcionar el ViewBinding.
     * 
     * Por defecto lanza NotImplementedError para permitir uso sin binding.
     * 
     * @param inflater el LayoutInflater
     * @param container el contenedor padre (puede ser null)
     * @return instancia del ViewBinding para este Fragment
     */
    protected open fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        throw NotImplementedError("Implementa getViewBinding() o usa constructor con layoutResId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    /**
     * Método para configurar las vistas del Fragment.
     * 
     * Llamado automáticamente en onViewCreated.
     * Override para inicializar vistas, listeners, adapters, etc.
     */
    protected open fun setupViews() {
        // Por defecto no hace nada, los fragments hijos pueden sobrescribir
    }

    /**
     * Método para configurar observers de LiveData/StateFlow.
     * 
     * Llamado automáticamente en onViewCreated después de setupViews.
     * Override para observar ViewModels, eventos, etc.
     */
    protected open fun setupObservers() {
        // Por defecto no hace nada, los fragments hijos pueden sobrescribir
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar referencia del binding para prevenir memory leaks
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Muestra un Toast con duración larga.
     * 
     * Útil para mensajes importantes que requieren más tiempo de lectura.
     * 
     * @param message el mensaje a mostrar
     */
    protected fun showLongToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    /**
     * Muestra un Toast con un string resource.
     * 
     * @param stringResId ID del string resource
     */
    protected fun showToast(stringResId: Int) {
        Toast.makeText(requireContext(), stringResId, Toast.LENGTH_SHORT).show()
    }
}
