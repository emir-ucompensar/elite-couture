package com.elitecouture.app.ui.feature.profile

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.elitecouture.app.data.service.LocationService
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.domain.model.User
import com.elitecouture.app.ui.common.EliteCoutureDialog
import com.elitecouture.app.ui.common.extension.showStyledSnackbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }
    private val updateProfileUseCase by lazy { ServiceLocator.provideUpdateProfileUseCase(requireContext()) }
    
    // Vistas - Información personal
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var addressLayout: TextInputLayout
    private lateinit var addressInput: TextInputEditText
    private lateinit var locationButton: MaterialButton
    
    // Vistas - Seguridad
    private lateinit var currentPasswordLayout: TextInputLayout
    private lateinit var currentPasswordInput: TextInputEditText
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordInput: TextInputEditText
    
    // Botones
    private lateinit var backButton: MaterialButton
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    
    // Estado
    private var hasUnsavedChanges = false
    private var initialFirstName: String? = null
    private var initialLastName: String? = null
    private var initialEmail: String? = null
    private var initialAddress: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Interceptar el botón de retroceso del sistema
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feature_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        loadUserData()
        setupTextWatchers()
        setupClickListeners()
    }
    
    private fun initializeViews(view: View) {
        // Información personal
        firstNameLayout = view.findViewById(R.id.edit_profile_first_name_layout)
        firstNameInput = view.findViewById(R.id.edit_profile_first_name_input)
        lastNameLayout = view.findViewById(R.id.edit_profile_last_name_layout)
        lastNameInput = view.findViewById(R.id.edit_profile_last_name_input)
        emailLayout = view.findViewById(R.id.edit_profile_email_layout)
        emailInput = view.findViewById(R.id.edit_profile_email_input)
        addressLayout = view.findViewById(R.id.edit_profile_address_layout)
        addressInput = view.findViewById(R.id.edit_profile_address_input)
        locationButton = view.findViewById(R.id.edit_profile_location_button)
        
        // Seguridad
        currentPasswordLayout = view.findViewById(R.id.edit_profile_current_password_layout)
        currentPasswordInput = view.findViewById(R.id.edit_profile_current_password_input)
        newPasswordLayout = view.findViewById(R.id.edit_profile_new_password_layout)
        newPasswordInput = view.findViewById(R.id.edit_profile_new_password_input)
        confirmPasswordLayout = view.findViewById(R.id.edit_profile_confirm_password_layout)
        confirmPasswordInput = view.findViewById(R.id.edit_profile_confirm_password_input)
        
        // Botones
        backButton = view.findViewById(R.id.edit_profile_back_button)
        saveButton = view.findViewById(R.id.edit_profile_save_button)
        cancelButton = view.findViewById(R.id.edit_profile_cancel_button)
        
        // Configurar listeners de botones
        setupLocationButtonListener()
    }
    
    private fun loadUserData() {
        // Cargar datos actuales desde SessionManager
        initialFirstName = sessionManager.getUserFirstName()
        initialLastName = sessionManager.getUserLastName()
        initialEmail = sessionManager.getUserEmail()
        initialAddress = sessionManager.getUserAddress()
        
        // Rellenar los campos
        firstNameInput.setText(initialFirstName)
        lastNameInput.setText(initialLastName)
        emailInput.setText(initialEmail)
        addressInput.setText(initialAddress)
    }
    
    private fun setupTextWatchers() {
        // Detectar cambios en los campos de información personal
        firstNameInput.addTextChangedListener { checkForChanges() }
        lastNameInput.addTextChangedListener { checkForChanges() }
        emailInput.addTextChangedListener { checkForChanges() }
        addressInput.addTextChangedListener { checkForChanges() }
        
        // Detectar cambios en los campos de contraseña
        currentPasswordInput.addTextChangedListener { checkForChanges() }
        newPasswordInput.addTextChangedListener { checkForChanges() }
        confirmPasswordInput.addTextChangedListener { checkForChanges() }
    }
    
    private fun checkForChanges() {
        val firstNameChanged = firstNameInput.text.toString() != (initialFirstName ?: "")
        val lastNameChanged = lastNameInput.text.toString() != (initialLastName ?: "")
        val emailChanged = emailInput.text.toString() != (initialEmail ?: "")
        val addressChanged = addressInput.text.toString() != (initialAddress ?: "")
        
        val passwordFieldsFilled = currentPasswordInput.text.toString().isNotEmpty() ||
                                   newPasswordInput.text.toString().isNotEmpty() ||
                                   confirmPasswordInput.text.toString().isNotEmpty()
        
        hasUnsavedChanges = firstNameChanged || lastNameChanged || 
                           emailChanged || addressChanged || 
                           passwordFieldsFilled
    }
    
    /**
     * Configurar el listener del botón de geolocalización
     */
    private fun setupLocationButtonListener() {
        locationButton.setOnClickListener {
            handleLocationButtonClick()
        }
    }
    
    /**
     * Maneja el clic en el botón de ubicación
     */
    private fun handleLocationButtonClick() {
        // Verificar si tenemos permisos
        if (LocationService.checkLocationPermissions(requireContext())) {
            // Tenemos permisos, obtener ubicación
            obtainCurrentLocation()
        } else {
            // No tenemos permisos, solicitarlos
            LocationService.requestLocationPermissions(requireActivity())
        }
    }
    
    /**
     * Obtiene la ubicación actual y actualiza el campo de dirección
     */
    private fun obtainCurrentLocation() {
        // Deshabilitar botón mientras se obtiene la ubicación
        locationButton.isEnabled = false
        
        // Mostrar mensaje de carga
        requireView().showStyledSnackbar(getString(R.string.location_obtaining))
        
        // Lanzar coroutine para obtener ubicación
        lifecycleScope.launch {
            try {
                // Obtener coordenadas (solo para convertir a dirección)
                val coordinates = LocationService.getCurrentLocation(requireContext())
                
                if (coordinates != null) {
                    val (latitude, longitude) = coordinates
                    
                    // Obtener dirección desde coordenadas (NO guardamos las coordenadas por seguridad)
                    val address = LocationService.getAddressFromCoordinates(
                        requireContext(),
                        latitude,
                        longitude
                    )
                    
                    if (address != null) {
                        // Actualizar campo de dirección
                        addressInput.setText(address)
                        
                        // Mostrar mensaje de éxito
                        requireView().showStyledSnackbar(getString(R.string.location_success))
                    } else {
                        // No se pudo obtener la dirección
                        requireView().showStyledSnackbar(getString(R.string.location_geocoding_error))
                    }
                } else {
                    // No se pudo obtener la ubicación
                    requireView().showStyledSnackbar(getString(R.string.location_error))
                }
            } catch (e: Exception) {
                // Error al obtener ubicación
                requireView().showStyledSnackbar(getString(R.string.location_error))
            } finally {
                // Rehabilitar botón
                locationButton.isEnabled = true
            }
        }
    }
    
    /**
     * Callback para el resultado de la solicitud de permisos
     */
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == LocationService.getLocationRequestCode()) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, obtener ubicación
                obtainCurrentLocation()
            } else {
                // Permiso denegado
                requireView().showStyledSnackbar(getString(R.string.location_permission_denied))
            }
        }
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            handleBackNavigation()
        }
        
        cancelButton.setOnClickListener {
            handleBackNavigation()
        }
        
        saveButton.setOnClickListener {
            saveChanges()
        }
    }
    
    private fun handleBackNavigation() {
        if (hasUnsavedChanges) {
            showUnsavedChangesDialog()
        } else {
            findNavController().popBackStack()
        }
    }
    
    private fun showUnsavedChangesDialog() {
        EliteCoutureDialog.create(requireContext())
            .setTitle(R.string.edit_profile_unsaved_changes_title)
            .setMessage(R.string.edit_profile_unsaved_changes_message)
            .setPositiveButton(R.string.edit_profile_unsaved_changes_save) {
                saveChanges()
            }
            .setNeutralButton(R.string.edit_profile_unsaved_changes_discard) {
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.edit_profile_unsaved_changes_cancel)
            .setCancelable(true)
            .show()
    }
    
    private fun saveChanges() {
        // Limpiar errores previos
        clearErrors()
        
        // Validar campos
        if (!validateFields()) {
            return
        }
        
        lifecycleScope.launch {
            // Guardar información personal
            val success = savePersonalInfo()
            
            // Guardar contraseña si se modificó
            val passwordChanged = savePasswordIfChanged()
            
            // Mostrar resultado
            if (success) {
                hasUnsavedChanges = false
                
                if (passwordChanged) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.edit_profile_password_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.edit_profile_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                // Volver al perfil
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.edit_profile_error_update_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun validateFields(): Boolean {
        var isValid = true
        
        // Validar nombre (requerido)
        val firstName = firstNameInput.text.toString().trim()
        if (firstName.isEmpty()) {
            firstNameLayout.error = getString(R.string.error_first_name_required)
            isValid = false
        }
        
        // Validar email (requerido y formato)
        val email = emailInput.text.toString().trim()
        if (email.isEmpty()) {
            emailLayout.error = getString(R.string.error_email_invalid)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = getString(R.string.error_email_invalid)
            isValid = false
        }
        
        // Validar contraseñas si se está intentando cambiar
        val currentPassword = currentPasswordInput.text.toString()
        val newPassword = newPasswordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()
        
        if (currentPassword.isNotEmpty() || newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
            // Si algún campo de contraseña tiene contenido, validar todos
            
            if (currentPassword.isEmpty()) {
                currentPasswordLayout.error = getString(R.string.error_password_required)
                isValid = false
            }
            
            if (newPassword.isEmpty()) {
                newPasswordLayout.error = getString(R.string.error_password_required)
                isValid = false
            } else if (newPassword.length < 6) {
                newPasswordLayout.error = getString(R.string.error_password_length)
                isValid = false
            }
            
            if (confirmPassword.isEmpty()) {
                confirmPasswordLayout.error = getString(R.string.error_password_required)
                isValid = false
            } else if (newPassword != confirmPassword) {
                confirmPasswordLayout.error = getString(R.string.error_password_mismatch)
                isValid = false
            }
        }
        
        return isValid
    }
    
    private fun clearErrors() {
        firstNameLayout.error = null
        lastNameLayout.error = null
        emailLayout.error = null
        addressLayout.error = null
        currentPasswordLayout.error = null
        newPasswordLayout.error = null
        confirmPasswordLayout.error = null
    }
    
    private suspend fun savePersonalInfo(): Boolean {
        return try {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val address = addressInput.text.toString().trim()
            
            // Obtener datos actuales que no se están editando
            val userId = sessionManager.getUserId()
            val userUuid = sessionManager.getUserUuid()
            val isGuest = sessionManager.isGuestMode()
            val createdAt = sessionManager.getUserCreatedAt()
            
            // Crear objeto User con los nuevos datos
            val updatedUser = User(
                id = userId ?: 0,
                uuid = userUuid ?: "",
                email = email,
                firstName = firstName,
                lastName = lastName.ifEmpty { null },
                address = address.ifEmpty { null },
                isGuest = isGuest,
                createdAt = createdAt ?: System.currentTimeMillis()
            )
            
            // Actualizar en la base de datos Y en la sesión
            val result = updateProfileUseCase(updatedUser)
            
            result.isSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun savePasswordIfChanged(): Boolean {
        val currentPassword = currentPasswordInput.text.toString()
        val newPassword = newPasswordInput.text.toString()
        
        // Si no hay campos de contraseña rellenados, no hacer nada
        if (currentPassword.isEmpty() && newPassword.isEmpty()) {
            return false
        }
        
        // TODO: Aquí iría la lógica real de cambio de contraseña con el backend
        // Por ahora, solo simulamos el cambio exitoso
        // En producción, deberías:
        // 1. Verificar que currentPassword sea correcta con el backend
        // 2. Actualizar la contraseña en el backend con newPassword
        // 3. Retornar true solo si ambas operaciones son exitosas
        
        return true
    }
}
