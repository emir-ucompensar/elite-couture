package com.elitecouture.app.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.domain.usecase.EnableGuestAccessUseCase
import com.elitecouture.app.domain.usecase.LoginUserUseCase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var googleLoginButton: MaterialButton
    private lateinit var appleLoginButton: MaterialButton
    private lateinit var guestAccessText: TextView
    private lateinit var forgotPasswordText: TextView
    private lateinit var createAccountLink: TextView
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout

    private val authRepository by lazy { ServiceLocator.provideAuthRepository(requireContext()) }
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }
    private val loginUserUseCase by lazy { LoginUserUseCase(authRepository, sessionManager) }
    private val enableGuestAccessUseCase by lazy { EnableGuestAccessUseCase(authRepository, sessionManager) }

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
        emailInputLayout = view.findViewById(R.id.email_input_layout)
        passwordInputLayout = view.findViewById(R.id.password_input_layout)
        loginButton = view.findViewById(R.id.login_button)
        googleLoginButton = view.findViewById(R.id.google_login)
        appleLoginButton = view.findViewById(R.id.apple_login)
        guestAccessText = view.findViewById(R.id.guest_access_text)
        forgotPasswordText = view.findViewById(R.id.forgot_password)
        createAccountLink = view.findViewById(R.id.create_account_link)

        // Configurar listeners
        loginButton.setOnClickListener {
            attemptLogin()
        }

        googleLoginButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_feature_unavailable), Toast.LENGTH_SHORT).show()
        }

        appleLoginButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_feature_unavailable), Toast.LENGTH_SHORT).show()
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.toast_forgot_password), Toast.LENGTH_SHORT).show()
        }

        createAccountLink.setOnClickListener {
            // Navegar al fragmento de registro
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        guestAccessText.setOnClickListener {
            enableGuestAccessUseCase()
            Toast.makeText(requireContext(), getString(R.string.toast_guest_mode_enabled), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loginFragment_to_storeFragment)
        }
    }

    private fun attemptLogin() {
        val email = emailInput.text?.toString()?.trim().orEmpty()
        val password = passwordInput.text?.toString()?.trim().orEmpty()

        var hasError = false

        emailInputLayout.error = null
        passwordInputLayout.error = null
        emailInput.error = null
        passwordInput.error = null

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = getString(R.string.error_email_invalid)
            hasError = true
        } else {
            emailInputLayout.error = null
        }

        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.error_password_required)
            hasError = true
        } else {
            passwordInputLayout.error = null
        }

        if (hasError) return

        val result = loginUserUseCase(email, password)
        result.onSuccess {
            passwordInputLayout.error = null
            findNavController().navigate(R.id.action_loginFragment_to_storeFragment)
        }.onFailure {
            passwordInputLayout.error = getString(R.string.error_login_invalid)
            Toast.makeText(requireContext(), getString(R.string.error_login_invalid), Toast.LENGTH_SHORT).show()
        }
    }
}
