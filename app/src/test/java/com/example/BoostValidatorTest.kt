package com.example

import com.example.data.BoostValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoostValidatorTest {

    @Test
    fun `valid Kenyan M-Pesa numbers are accepted`() {
        assertTrue(BoostValidator.isValidKenyanMpesaPhone("0712345678"))
        assertTrue(BoostValidator.isValidKenyanMpesaPhone("0112345678"))
        assertTrue(BoostValidator.isValidKenyanMpesaPhone("254712345678"))
        assertTrue(BoostValidator.isValidKenyanMpesaPhone("+254712345678"))
        assertTrue(BoostValidator.isValidKenyanMpesaPhone("0712 345 678"))
    }

    @Test
    fun `invalid M-Pesa numbers are rejected`() {
        assertFalse(BoostValidator.isValidKenyanMpesaPhone(""))
        assertFalse(BoostValidator.isValidKenyanMpesaPhone("07123"))
        assertFalse(BoostValidator.isValidKenyanMpesaPhone("0812345678"))
        assertFalse(BoostValidator.isValidKenyanMpesaPhone("+12025550123"))
    }

    @Test
    fun `target URLs require http or https with host`() {
        assertTrue(BoostValidator.isValidTargetUrl("https://tiktok.com/@creator/video/123"))
        assertTrue(BoostValidator.isValidTargetUrl("http://localhost:3000"))
        assertFalse(BoostValidator.isValidTargetUrl("tiktok.com/video"))
        assertFalse(BoostValidator.isValidTargetUrl(""))
    }

    @Test
    fun `endpoint builder normalizes trailing slash and path`() {
        assertEquals(
            "http://10.0.2.2:3000/api/stkpush",
            BoostValidator.buildStkEndpointUrl("http://10.0.2.2:3000/")
        )
        assertEquals(
            "https://boostke.onrender.com/api/stkpush",
            BoostValidator.buildStkEndpointUrl("https://boostke.onrender.com")
        )
        assertEquals(
            "https://boostke.onrender.com/api/stkpush",
            BoostValidator.buildStkEndpointUrl("https://boostke.onrender.com/api/stkpush")
        )
    }
}
