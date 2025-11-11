package com.elitecouture.app.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Utilidad para encriptar/desencriptar datos sensibles del usuario.
 * 
 * Usa AES-256 con CBC y PKCS7Padding para encriptación segura.
 * Utilizado principalmente para proteger información personal como direcciones.
 */
object CryptoUtil {

    /**
     * Genera un hash SHA-256 seguro para contraseñas.
     * @param input texto plano de la contraseña
     * @return hash en hexadecimal
     */
    fun sha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    private const val ALGORITHM = "AES/CBC/PKCS7Padding"
    private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val KEY_LENGTH = 256
    private const val ITERATION_COUNT = 10000
    
    // Salt fija para esta aplicación (en producción debería ser única por usuario)
    private const val SALT = "EliteCouture2025SecureSalt"
    
    // Password base (en producción esto debería venir de Android Keystore)
    private const val PASSWORD = "EliteCouture_Secure_Key_2025"
    
    /**
     * Encripta un texto usando AES-256.
     * 
     * @param plainText texto a encriptar
     * @return texto encriptado en Base64, o null si hay error
     */
    fun encrypt(plainText: String?): String? {
        if (plainText.isNullOrEmpty()) return null
        
        return try {
            val key = generateKey()
            val cipher = Cipher.getInstance(ALGORITHM)
            
            // Generar IV aleatorio
            val ivBytes = ByteArray(16)
            SecureRandom().nextBytes(ivBytes)
            val iv = IvParameterSpec(ivBytes)
            
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            
            // Combinar IV + datos encriptados
            val combined = ivBytes + encryptedBytes
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            android.util.Log.e("CryptoUtil", "Error encrypting: ${e.message}")
            null
        }
    }
    
    /**
     * Desencripta un texto encriptado con AES-256.
     * 
     * @param encryptedText texto encriptado en Base64
     * @return texto desencriptado, o null si hay error
     */
    fun decrypt(encryptedText: String?): String? {
        if (encryptedText.isNullOrEmpty()) return null
        
        return try {
            val key = generateKey()
            val cipher = Cipher.getInstance(ALGORITHM)
            
            // Decodificar Base64
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)
            
            // Separar IV y datos encriptados
            val ivBytes = combined.sliceArray(0 until 16)
            val encryptedBytes = combined.sliceArray(16 until combined.size)
            
            val iv = IvParameterSpec(ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            android.util.Log.e("CryptoUtil", "Error decrypting: ${e.message}")
            null
        }
    }
    
    /**
     * Genera la clave secreta usando PBKDF2.
     */
    private fun generateKey(): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec = PBEKeySpec(
            PASSWORD.toCharArray(),
            SALT.toByteArray(),
            ITERATION_COUNT,
            KEY_LENGTH
        )
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }
}
