package com.elitecouture.app.util

import android.util.Patterns
import com.elitecouture.app.util.Constants.Auth
import com.elitecouture.app.util.Constants.Validation

/**
 * Utilidad centralizada para validaciones de entrada del usuario.
 * 
 * Proporciona métodos de validación reutilizables y consistentes en toda la aplicación.
 * Cada método retorna un [ValidationResult] con el estado y mensaje de error si aplica.
 * 
 * @see ValidationResult
 * @see Constants.Auth para configuraciones de validación
 */
object ValidationUtil {

    /**
     * Resultado de una validación
     * 
     * @property isValid true si la validación fue exitosa
     * @property errorMessage mensaje descriptivo del error (null si isValid = true)
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    ) {
        companion object {
            /** Resultado de validación exitosa */
            fun success() = ValidationResult(isValid = true, errorMessage = null)
            
            /** Resultado de validación fallida */
            fun error(message: String) = ValidationResult(isValid = false, errorMessage = message)
        }
    }

    /**
     * Valida un correo electrónico.
     * 
     * Verifica:
     * - No esté vacío
     * - Tenga formato válido según Android Patterns
     * - Cumpla con regex personalizado
     * 
     * @param email el correo a validar
     * @return ValidationResult con el resultado
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> {
                ValidationResult.error("El correo electrónico es requerido")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                ValidationResult.error("Ingresa un correo electrónico válido")
            }
            !email.matches(Regex(Auth.EMAIL_PATTERN)) -> {
                ValidationResult.error("Formato de correo inválido")
            }
            else -> ValidationResult.success()
        }
    }

    /**
     * Valida una contraseña.
     * 
     * Verifica:
     * - No esté vacía
     * - Cumpla con longitud mínima
     * - No exceda longitud máxima
     * 
     * @param password la contraseña a validar
     * @return ValidationResult con el resultado
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> {
                ValidationResult.error("La contraseña es requerida")
            }
            password.length < Auth.MIN_PASSWORD_LENGTH -> {
                ValidationResult.error("La contraseña debe tener al menos ${Auth.MIN_PASSWORD_LENGTH} caracteres")
            }
            password.length > Auth.MAX_PASSWORD_LENGTH -> {
                ValidationResult.error("La contraseña no puede exceder ${Auth.MAX_PASSWORD_LENGTH} caracteres")
            }
            else -> ValidationResult.success()
        }
    }

    /**
     * Valida que dos contraseñas coincidan.
     * 
     * Útil en pantallas de registro o cambio de contraseña.
     * 
     * @param password contraseña original
     * @param confirmPassword contraseña de confirmación
     * @return ValidationResult con el resultado
     */
    fun validatePasswordMatch(password: String, confirmPassword: String): ValidationResult {
        return if (password == confirmPassword) {
            ValidationResult.success()
        } else {
            ValidationResult.error("Las contraseñas no coinciden")
        }
    }

    /**
     * Valida un nombre de usuario.
     * 
     * Verifica:
     * - No esté vacío
     * - Cumpla con longitud mínima
     * - No exceda longitud máxima
     * - Solo contenga caracteres permitidos
     * 
     * @param name el nombre a validar
     * @param fieldName nombre del campo para mensajes (default: "Nombre")
     * @return ValidationResult con el resultado
     */
    fun validateName(name: String, fieldName: String = "Nombre"): ValidationResult {
        return when {
            name.isBlank() -> {
                ValidationResult.error("$fieldName es requerido")
            }
            name.trim().length < Auth.MIN_NAME_LENGTH -> {
                ValidationResult.error("$fieldName debe tener al menos ${Auth.MIN_NAME_LENGTH} caracteres")
            }
            name.length > Auth.MAX_NAME_LENGTH -> {
                ValidationResult.error("$fieldName no puede exceder ${Auth.MAX_NAME_LENGTH} caracteres")
            }
            !name.all { it in Validation.NAME_ALLOWED_CHARS } -> {
                ValidationResult.error("$fieldName contiene caracteres no permitidos")
            }
            else -> ValidationResult.success()
        }
    }

    /**
     * Valida que un campo de texto no esté vacío.
     * 
     * Validación genérica para campos obligatorios.
     * 
     * @param text el texto a validar
     * @param fieldName nombre del campo para el mensaje de error
     * @return ValidationResult con el resultado
     */
    fun validateRequired(text: String, fieldName: String = "Este campo"): ValidationResult {
        return if (text.isBlank()) {
            ValidationResult.error("$fieldName es requerido")
        } else {
            ValidationResult.success()
        }
    }

    /**
     * Valida una dirección.
     * 
     * Verifica:
     * - No esté vacía
     * - No exceda longitud máxima
     * 
     * @param address la dirección a validar
     * @return ValidationResult con el resultado
     */
    fun validateAddress(address: String): ValidationResult {
        return when {
            address.isBlank() -> {
                ValidationResult.error("La dirección es requerida")
            }
            address.length > Validation.MAX_ADDRESS_LENGTH -> {
                ValidationResult.error("La dirección no puede exceder ${Validation.MAX_ADDRESS_LENGTH} caracteres")
            }
            else -> ValidationResult.success()
        }
    }

    /**
     * Valida un comentario o texto largo.
     * 
     * Verifica que no exceda la longitud máxima permitida.
     * 
     * @param comment el comentario a validar
     * @param maxLength longitud máxima permitida (default: de Constants)
     * @return ValidationResult con el resultado
     */
    fun validateComment(comment: String, maxLength: Int = Validation.MAX_COMMENT_LENGTH): ValidationResult {
        return if (comment.length > maxLength) {
            ValidationResult.error("El comentario no puede exceder $maxLength caracteres")
        } else {
            ValidationResult.success()
        }
    }

    /**
     * Valida múltiples campos a la vez.
     * 
     * Útil para validar formularios completos.
     * Se detiene en el primer error encontrado.
     * 
     * @param validations lista de ValidationResults a verificar
     * @return el primer ValidationResult con error, o success si todos son válidos
     */
    fun validateAll(vararg validations: ValidationResult): ValidationResult {
        val firstError = validations.firstOrNull { !it.isValid }
        return firstError ?: ValidationResult.success()
    }

    /**
     * Valida credenciales de login.
     * 
     * Combina validación de email y password.
     * 
     * @param email el email a validar
     * @param password la contraseña a validar
     * @return ValidationResult con el resultado
     */
    fun validateLoginCredentials(email: String, password: String): ValidationResult {
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            return emailValidation
        }

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            return passwordValidation
        }

        return ValidationResult.success()
    }

    /**
     * Valida formulario de registro completo.
     * 
     * @param firstName nombre
     * @param email correo electrónico
     * @param password contraseña
     * @param confirmPassword confirmación de contraseña
     * @return ValidationResult con el resultado
     */
    fun validateRegistrationForm(
        firstName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        return validateAll(
            validateName(firstName, "Nombre"),
            validateEmail(email),
            validatePassword(password),
            validatePasswordMatch(password, confirmPassword)
        )
    }
}
