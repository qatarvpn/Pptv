package com.example.data.security

import android.os.Build
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CryptoHelperTest {

    @Test
    fun testEncryptAndDecrypt() {
        val original = "mysecretpassword123"
        val encrypted = CryptoHelper.encrypt(original)

        assertNotEquals(original, encrypted)
        assertTrue(encrypted.isNotEmpty())

        val decrypted = CryptoHelper.decrypt(encrypted)
        assertEquals(original, decrypted)
    }

    @Test
    fun testEncryptMultipleValues() {
        val password1 = "password1"
        val password2 = "password2"

        val encrypted1 = CryptoHelper.encrypt(password1)
        val encrypted2 = CryptoHelper.encrypt(password2)

        assertNotEquals(encrypted1, encrypted2)
        assertEquals(password1, CryptoHelper.decrypt(encrypted1))
        assertEquals(password2, CryptoHelper.decrypt(encrypted2))
    }

    @Test
    fun testDecryptOrNullWithValidValue() {
        val original = "test_password"
        val encrypted = CryptoHelper.encrypt(original)
        val decrypted = CryptoHelper.decryptOrNull(encrypted)

        assertEquals(original, decrypted)
    }

    @Test
    fun testDecryptOrNullWithNull() {
        val result = CryptoHelper.decryptOrNull(null)
        assertNull(result)
    }

    @Test
    fun testDecryptOrNullWithEmpty() {
        val result = CryptoHelper.decryptOrNull("")
        assertNull(result)
    }

    @Test
    fun testDecryptOrNullWithInvalidData() {
        val result = CryptoHelper.decryptOrNull("invalid_data")
        assertEquals("invalid_data", result)
    }
}
