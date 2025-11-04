package com.elitecouture.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
    private lateinit var profileLastname: TextView
    private lateinit var profileEmail: TextView
    private lateinit var editProfileButton: ImageButton
    private lateinit var favoritesButton: MaterialButton
    private lateinit var purchaseHistoryButton: MaterialButton
    private lateinit var settingsButton: MaterialButton
    private lateinit var saveChangesButton: MaterialButton
    private lateinit var logoutButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        profileName = view.findViewById(R.id.profile_name)
        profileLastname = view.findViewById(R.id.profile_lastname)
        profileEmail = view.findViewById(R.id.profile_email)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        favoritesButton = view.findViewById(R.id.favorites_button)
        purchaseHistoryButton = view.findViewById(R.id.purchase_history_button)
        settingsButton = view.findViewById(R.id.settings_button)
        saveChangesButton = view.findViewById(R.id.save_changes_button)
        logoutButton = view.findViewById(R.id.logout_button)

        // Configurar listeners
        editProfileButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_profile_edit_in_progress), Toast.LENGTH_SHORT).show()
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

        saveChangesButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_profile_changes_saved), Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            sessionManager.clearSession()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        // Cargar datos de prueba
        loadMockData()
    }

    private fun loadMockData() {
        profileName.text = getString(R.string.profile_field_name_placeholder)
        profileLastname.text = getString(R.string.profile_field_lastname_placeholder)
        profileEmail.text = getString(R.string.profile_field_email_placeholder)
    }
}
