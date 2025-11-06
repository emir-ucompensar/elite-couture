package com.elitecouture.app.ui.feature.profile

import android.os.Bundle
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
                    findNavController().popBackStack()
                    true
                }
                R.id.navigation_cart -> {
                    if (sessionManager.isGuestMode()) {
                        requireView().showStyledSnackbar(getString(R.string.toast_guest_restricted_feature))
                        false
                    } else {
                        requireView().showStyledSnackbar(getString(R.string.toast_cart_under_construction))
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

    override fun onResume() {
        super.onResume()
        // Asegurar que el ítem correcto esté seleccionado al volver a este fragmento
        bottomNavigation.selectedItemId = R.id.navigation_profile
    }
}
