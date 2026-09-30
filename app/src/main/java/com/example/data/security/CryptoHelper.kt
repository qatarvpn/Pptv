package com.example.data.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Helper class for encrypting/decrypting sensitive data (Xtream credentials)
 * Uses Android Keystore for secure key management
 */
object CryptoHelper {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "iptv_credential_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH = 12

    fun encrypt(value: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            // Prepend IV to ciphertext for decryption
            return Base64.encodeToString(iv + encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            // If encryption fails, return original (fallback for older devices)
            return value
        }
    }

    fun decrypt(value: String): String {
        try {
            val decoded = Base64.decode(value, Base64.NO_WRAP)
            require(decoded.size > IV_LENGTH) { "Ciphertext is too short" }

            val iv = decoded.copyOfRange(0, IV_LENGTH)
            val payload = decoded.copyOfRange(IV_LENGTH, decoded.size)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
            val result = cipher.doFinal(payload)
            return String(result, Charsets.UTF_8)
        } catch (e: Exception) {
            // Return original if decryption fails
            return value
        }
    }

    fun decryptOrNull(value: String?): String? {
        if (value.isNullOrBlank()) return null
        return try {
            decrypt(value)
        } catch (_: Exception) {
            value
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existing = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) {
            return existing.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keySpecBuilder = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        
        // setRandomizedEncryptionRequired is not available on older API levels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            keySpecBuilder.setRandomizedEncryptionRequired(true)
        }
        
        keyGenerator.init(keySpecBuilder.build())
        return keyGenerator.generateKey()
    }
}