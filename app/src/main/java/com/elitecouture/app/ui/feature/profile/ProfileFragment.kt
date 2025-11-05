package com.elitecouture.app.ui.feature.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
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

        // Configurar listeners
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        favoritesButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_profile_favorites_in_progress), Toast.LENGTH_SHORT).show()
        }

        purchaseHistoryButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_profile_history_in_progress), Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_profile_settings_in_progress), Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            sessionManager.clearSession()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
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
}
