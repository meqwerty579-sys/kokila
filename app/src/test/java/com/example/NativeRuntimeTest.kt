package com.example

import com.example.core.runtime.NativeRuntimeValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NativeRuntimeTest {

    @Test
    fun testAbiSupportedCheck() {
        val isSupported = NativeRuntimeValidator.isAbiSupported()
        assertNotNull(isSupported)
    }

    @Test
    fun testValidateRuntimeFailureGraceful() {
        val result = NativeRuntimeValidator.validateRuntime()
        assertNotNull(result)
        assertFalse(result.isSupported)
        assertNotNull(result.reason)
    }
}
