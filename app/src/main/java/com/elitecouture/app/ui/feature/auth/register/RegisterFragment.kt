package com.elitecouture.app.ui.feature.auth.register

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
import com.elitecouture.app.domain.usecase.auth.RegisterUserUseCase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var firstNameInputLayout: TextInputLayout
    private lateinit var lastNameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var registerButton: MaterialButton
    private lateinit var googleRegisterButton: MaterialButton
    private lateinit var appleRegisterButton: MaterialButton
    private lateinit var loginLink: TextView

    private val authRepository by lazy { ServiceLocator.provideAuthRepository(requireContext()) }
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }
    private val registerUserUseCase by lazy { RegisterUserUseCase(authRepository, sessionManager) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inicializar vistas
        firstNameInput = view.findViewById(R.id.first_name_input)
        lastNameInput = view.findViewById(R.id.last_name_input)
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input)
    firstNameInputLayout = view.findViewById(R.id.first_name_input_layout)
    lastNameInputLayout = view.findViewById(R.id.last_name_input_layout)
    emailInputLayout = view.findViewById(R.id.register_email_input_layout)
    passwordInputLayout = view.findViewById(R.id.register_password_input_layout)
    confirmPasswordInputLayout = view.findViewById(R.id.confirm_password_input_layout)
        registerButton = view.findViewById(R.id.register_button)
        googleRegisterButton = view.findViewById(R.id.google_register)
        appleRegisterButton = view.findViewById(R.id.apple_register)
        loginLink = view.findViewById(R.id.login_link)

        // Configurar listeners
        registerButton.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener

            val firstName = firstNameInput.text?.toString()?.trim().orEmpty()
            val lastName = lastNameInput.text?.toString()?.trim().orEmpty().ifBlank { null }
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val password = passwordInput.text?.toString()?.trim().orEmpty()

            val existingUser = authRepository.findByEmail(email)
            if (existingUser != null) {
                Toast.makeText(requireContext(), getString(R.string.error_account_already_exists), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val result = registerUserUseCase(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
            result.onSuccess {
                Toast.makeText(requireContext(), getString(R.string.toast_registration_success), Toast.LENGTH_SHORT).show()
                
                // Navegar a tienda y limpiar back stack completo (login y register)
                val navOptions = androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true) // Eliminar todo hasta loginFragment inclusive
                    .build()
                findNavController().navigate(R.id.action_registerFragment_to_storeFragment, null, navOptions)
            }.onFailure {
                Toast.makeText(requireContext(), getString(R.string.error_registration_generic), Toast.LENGTH_SHORT).show()
            }
        }

        googleRegisterButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_feature_unavailable), Toast.LENGTH_SHORT).show()
        }

        appleRegisterButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_feature_unavailable), Toast.LENGTH_SHORT).show()
        }

        loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = firstNameInput.text?.toString()?.trim().orEmpty()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        clearErrors()

        var isValid = true

        if (firstName.isEmpty()) {
            firstNameInputLayout.error = getString(R.string.error_first_name_required)
            isValid = false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = getString(R.string.error_email_invalid)
            isValid = false
        }

        if (password.isEmpty() || password.length < 6) {
            passwordInputLayout.error = getString(R.string.error_password_length)
            isValid = false
        }

        if (password != confirmPassword) {
            confirmPasswordInputLayout.error = getString(R.string.error_password_mismatch)
            isValid = false
        }

        return isValid
    }

    private fun clearErrors() {
        firstNameInputLayout.error = null
        lastNameInputLayout.error = null
        emailInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null
        firstNameInput.error = null
        lastNameInput.error = null
        emailInput.error = null
        passwordInput.error = null
        confirmPasswordInput.error = null
    }
}
