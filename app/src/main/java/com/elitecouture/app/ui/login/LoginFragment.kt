package com.elitecouture.app.ui.login

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

class LoginFragment : Fragment() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var googleLoginButton: MaterialButton
    private lateinit var appleLoginButton: MaterialButton
    private lateinit var forgotPasswordText: TextView
    private lateinit var registerTab: TextView
    private lateinit var loginTab: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inicializar vistas
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        googleLoginButton = view.findViewById(R.id.google_login)
        appleLoginButton = view.findViewById(R.id.apple_login)
        forgotPasswordText = view.findViewById(R.id.forgot_password)
        registerTab = view.findViewById(R.id.register_tab)
        loginTab = view.findViewById(R.id.login_tab)

        // Configurar listeners
        loginButton.setOnClickListener {
            // Como no hay backend, simplemente navegamos al StoreFragment
            findNavController().navigate(R.id.action_loginFragment_to_storeFragment)
        }

        googleLoginButton.setOnClickListener {
            Toast.makeText(context, "Iniciando sesión con Google...", Toast.LENGTH_SHORT).show()
            // Simular login exitoso y navegar al store
            findNavController().navigate(R.id.action_loginFragment_to_storeFragment)
        }

        appleLoginButton.setOnClickListener {
            Toast.makeText(context, "Iniciando sesión con Apple...", Toast.LENGTH_SHORT).show()
            // Simular login exitoso y navegar al store
            findNavController().navigate(R.id.action_loginFragment_to_storeFragment)
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(context, "Función de recuperación de contraseña", Toast.LENGTH_SHORT).show()
        }

        registerTab.setOnClickListener {
            // Cambiar colores para indicar tab seleccionado
            registerTab.setTextColor(resources.getColor(R.color.color_primary, null))
            loginTab.setTextColor(resources.getColor(R.color.text_hint, null))
            // Aquí podrías mostrar el formulario de registro
            Toast.makeText(context, "Cambiando a registro...", Toast.LENGTH_SHORT).show()
        }

        loginTab.setOnClickListener {
            // Cambiar colores para indicar tab seleccionado
            loginTab.setTextColor(resources.getColor(R.color.color_primary, null))
            registerTab.setTextColor(resources.getColor(R.color.text_hint, null))
        }
    }
}
