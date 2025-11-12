package com.elitecouture.app.ui.feature.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.ui.common.EliteCoutureDialog
import com.elitecouture.app.ui.common.extension.showStyledSnackbar
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment() {
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }

    private lateinit var profileName: TextView
    private lateinit var profileEmailDetail: TextView
    private lateinit var profileAddress: TextView
    private lateinit var editProfileButton: MaterialButton
    private lateinit var backButton: MaterialButton
    private lateinit var favoritesButton: MaterialButton
    private lateinit var purchaseHistoryButton: MaterialButton
    private lateinit var settingsButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var cameraButton: MaterialButton
    private lateinit var bottomNavigation: com.google.android.material.bottomnavigation.BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feature_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        profileName = view.findViewById(R.id.profile_name)
        profileEmailDetail = view.findViewById(R.id.profile_email_value)
        profileAddress = view.findViewById(R.id.profile_address)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        backButton = view.findViewById(R.id.profile_back_button)
        favoritesButton = view.findViewById(R.id.favorites_button)
        purchaseHistoryButton = view.findViewById(R.id.purchase_history_button)
        settingsButton = view.findViewById(R.id.settings_button)
        logoutButton = view.findViewById(R.id.logout_button)
        cameraButton = view.findViewById(R.id.camera_button)
        bottomNavigation = view.findViewById(R.id.bottom_navigation)

        // Configurar Bottom Navigation
        setupBottomNavigation()

        // Configurar listeners
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        favoritesButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_favoritesFragment)
        }

        purchaseHistoryButton.setOnClickListener {
            requireView().showStyledSnackbar(getString(R.string.toast_profile_history_in_progress))
        }

        settingsButton.setOnClickListener {
            requireView().showStyledSnackbar(getString(R.string.toast_profile_settings_in_progress))
        }

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        cameraButton.setOnClickListener {
            requestCameraPermission()
        }

        // Hacer que al tocar la dirección se soliciten permisos de ubicación
        profileAddress.setOnClickListener {
            requestLocationPermission()
        }

        // Cargar datos del usuario desde la sesión
        loadUserData()
    }

    private fun loadUserData() {
        // Obtener datos del usuario desde SessionManager
        val firstName = sessionManager.getUserFirstName()
        val lastName = sessionManager.getUserLastName()
        val email = sessionManager.getUserEmail()
        val address = sessionManager.getUserAddress()

        // Construir nombre completo
        val fullName = when {
            !firstName.isNullOrEmpty() && !lastName.isNullOrEmpty() -> "$firstName $lastName"
            !firstName.isNullOrEmpty() -> firstName
            !lastName.isNullOrEmpty() -> lastName
            else -> getString(R.string.profile_field_name_placeholder)
        }

        // Asignar valores a las vistas
        profileName.text = fullName
        profileEmailDetail.text = email ?: getString(R.string.profile_field_email_placeholder)
        
        // Configurar dirección con hint elegante si no existe
        if (address.isNullOrEmpty()) {
            profileAddress.text = getString(R.string.profile_address_hint)
            profileAddress.setTextColor(
                resources.getColor(R.color.text_hint, null)
            )
            profileAddress.setTypeface(null, android.graphics.Typeface.ITALIC)
        } else {
            profileAddress.text = address
            profileAddress.setTextColor(
                resources.getColor(R.color.text_primary_dark, null)
            )
            profileAddress.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun showLogoutConfirmationDialog() {
        EliteCoutureDialog.create(requireContext())
            .setTitle(R.string.profile_logout_dialog_title)
            .setMessage(R.string.profile_logout_dialog_message)
            .setPositiveButton(R.string.profile_logout_dialog_confirm) {
                performLogout()
            }
            .setPositiveButtonColor(R.color.color_error)
            .setNegativeButton(R.string.profile_logout_dialog_cancel)
            .setCancelable(true)
            .show()
    }

    private fun performLogout() {
        sessionManager.clearSession()
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

    private fun setupBottomNavigation() {
        // Establecer el ítem seleccionado como "Mi Perfil"
        bottomNavigation.selectedItemId = R.id.navigation_profile

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_store -> {
                    // Volver a la tienda (pop back stack)
                    findNavController().navigate(R.id.action_profileFragment_to_storeFragment)
                    true
                }
                R.id.navigation_cart -> {
                    if (sessionManager.isGuestMode()) {
                        requireView().showStyledSnackbar(
                            message = getString(R.string.toast_guest_restricted_feature),
                            duration = com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
                            actionText = getString(R.string.action_login),
                            actionCallback = {
                                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                            }
                        )
                        false
                    } else {
                        findNavController().navigate(R.id.action_profileFragment_to_cartFragment)
                        true
                    }
                }
                R.id.navigation_profile -> {
                    // Ya estamos en perfil
                    true
                }
                else -> false
            }
        }
    }

    private fun requestCameraPermission() {
        Log.d(TAG, "requestCameraPermission called")
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            EliteCoutureDialog.create(requireContext())
                .setTitle(R.string.camera_permission_dialog_title)
                .setMessage(R.string.camera_permission_dialog_message)
                .setPositiveButton(R.string.action_grant_permission) {
                    requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                }
                .setNegativeButton(R.string.action_cancel)
                .setCancelable(true)
                .show()
        } else {
            Log.d(TAG, "Requesting CAMERA permission directly")
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            EliteCoutureDialog.create(requireContext())
                .setTitle(R.string.location_permission_dialog_title)
                .setMessage(R.string.location_permission_dialog_message)
                .setPositiveButton(R.string.action_grant_permission) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                .setNegativeButton(R.string.action_cancel)
                .setCancelable(true)
                .show()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
                Log.d(TAG, "CAMERA permission granted")
                openCamera()
            } else {
                requireView().showStyledSnackbar(getString(R.string.camera_permission_denied_message))
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
                // Location permission granted
                requireView().showStyledSnackbar(getString(R.string.location_permission_granted_message))
            } else {
                requireView().showStyledSnackbar(getString(R.string.location_permission_denied_message))
            }
        }
    }

    private fun openCamera() {
        // Primero navegamos al CameraFragment que usa CameraX (fallback interno)
        try {
            findNavController().navigate(R.id.cameraFragment)
            return
        } catch (e: Exception) {
            Log.e(TAG, "Navigation to CameraFragment failed", e)
        }

        // Si la navegación falla, intentamos con la app de cámara externa como fallback
        val intent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        // Verificar que exista una Activity que resuelva el intent
        val resolveInfo = requireActivity().packageManager.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo == null) {
            Log.e(TAG, "No activity found to handle camera intent")
            requireView().showStyledSnackbar("No se encontró una aplicación de cámara en el dispositivo.")
            return
        } else {
            val activityName = resolveInfo.activityInfo?.packageName + "/" + resolveInfo.activityInfo?.name
            Log.d(TAG, "Camera intent will be handled by: $activityName")
        }

        try {
            Log.d(TAG, "Starting external camera intent as fallback")
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting external camera intent", e)
            // Mostrar feedback al usuario y evitar que la app crashee
            requireView().showStyledSnackbar("Error al abrir la cámara: ${e.message}")
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1002
        private const val TAG = "EliteCoutureCamera"
    }

    override fun onResume() {
        super.onResume()
        // Asegurar que el ítem correcto esté seleccionado al volver a este fragmento
        bottomNavigation.selectedItemId = R.id.navigation_profile
    }
}
