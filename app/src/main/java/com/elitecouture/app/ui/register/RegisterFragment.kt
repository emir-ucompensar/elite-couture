package com.elitecouture.app.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterFragment : Fragment() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var registerButton: MaterialButton
    private lateinit var googleRegisterButton: MaterialButton
    private lateinit var appleRegisterButton: MaterialButton
    private lateinit var registerTab: TextView
    private lateinit var loginTab: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inicializar vistas
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input)
        registerButton = view.findViewById(R.id.register_button)
        googleRegisterButton = view.findViewById(R.id.google_register)
        appleRegisterButton = view.findViewById(R.id.apple_register)
        registerTab = view.findViewById(R.id.register_tab)
        loginTab = view.findViewById(R.id.login_tab)

        // Configurar listeners
        registerButton.setOnClickListener {
            if (validateInputs()) {
                // Como no hay backend, simplemente navegamos al StoreFragment
                findNavController().navigate(R.id.action_registerFragment_to_storeFragment)
            }
        }

        googleRegisterButton.setOnClickListener {
            Toast.makeText(context, "Registrándose con Google...", Toast.LENGTH_SHORT).show()
            // Simular registro exitoso y navegar al store
            findNavController().navigate(R.id.action_registerFragment_to_storeFragment)
        }

        appleRegisterButton.setOnClickListener {
            Toast.makeText(context, "Registrándose con Apple...", Toast.LENGTH_SHORT).show()
            // Simular registro exitoso y navegar al store
            findNavController().navigate(R.id.action_registerFragment_to_storeFragment)
        }

        loginTab.setOnClickListener {
            // Navegar al fragmento de login
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        registerTab.setOnClickListener {
            // Ya estamos en registro, solo actualizar colores
            registerTab.setTextColor(resources.getColor(R.color.accent_red, null))
            loginTab.setTextColor(resources.getColor(R.color.text_hint, null))
        }
    }

    private fun validateInputs(): Boolean {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Ingresa un correo electrónico válido"
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            passwordInput.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordInput.error = "Las contraseñas no coinciden"
            return false
        }

        return true
    }
}
