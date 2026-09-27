package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MedRad Study app", appName)
  }

  @Test
  fun `password hashing and verification is consistent`() {
    val rawPassword = "Student@12345"
    val hash = com.example.data.util.PasswordSecurity.hashPassword(rawPassword)
    org.junit.Assert.assertTrue(com.example.data.util.PasswordSecurity.verifyPassword(rawPassword, hash))
    org.junit.Assert.assertFalse(com.example.data.util.PasswordSecurity.verifyPassword("WrongPassword", hash))
  }

  @Test
  fun `launch MainActivity without crashing`() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
    controller.setup()
  }
}
