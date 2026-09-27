package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.entity.UserEntity
import com.example.data.util.PasswordSecurity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context, private val database: AppDatabase) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("medrad_auth_prefs", Context.MODE_PRIVATE)

  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: Flow<UserEntity?> = _currentUser.asStateFlow()

  suspend fun checkExistingSession() {
    withContext(Dispatchers.IO) {
      val savedUserId = prefs.getLong("logged_in_user_id", -1L)
      if (savedUserId != -1L) {
        val user = database.userDao().getUserById(savedUserId)
        _currentUser.value = user
      }
    }
  }

  suspend fun login(email: String, password: String): Result<UserEntity> {
    return withContext(Dispatchers.IO) {
      try {
        val trimmedEmail = email.trim().lowercase()
        val user = database.userDao().getUserByEmail(trimmedEmail)
          ?: return@withContext Result.failure(Exception("No account found with this email address."))

        if (!PasswordSecurity.verifyPassword(password, user.passwordHash)) {
          return@withContext Result.failure(Exception("Incorrect password. Please verify your credentials."))
        }

        // Check if premium expired
        val updatedUser = checkAndUpdatePremiumExpiry(user)

        prefs.edit().putLong("logged_in_user_id", updatedUser.id).apply()
        _currentUser.value = updatedUser
        Result.success(updatedUser)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun register(
    fullName: String,
    email: String,
    phoneNumber: String,
    password: String,
    university: String,
    faculty: String,
    department: String,
    level: String,
    semester: String
  ): Result<UserEntity> {
    return withContext(Dispatchers.IO) {
      try {
        val trimmedEmail = email.trim().lowercase()
        val existing = database.userDao().getUserByEmail(trimmedEmail)
        if (existing != null) {
          return@withContext Result.failure(Exception("An account with this email already exists."))
        }

        if (password.length < 6) {
          return@withContext Result.failure(Exception("Password must be at least 6 characters."))
        }

        val newUser = UserEntity(
          fullName = fullName.trim(),
          email = trimmedEmail,
          phoneNumber = phoneNumber.trim(),
          passwordHash = PasswordSecurity.hashPassword(password),
          role = "STUDENT",
          university = university.trim(),
          faculty = faculty.trim(),
          department = department.trim(),
          level = level.trim(),
          semester = semester.trim(),
          isPremium = false
        )

        val id = database.userDao().insertUser(newUser)
        val created = newUser.copy(id = id)
        prefs.edit().putLong("logged_in_user_id", id).apply()
        _currentUser.value = created
        Result.success(created)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun resetPassword(email: String, newPassword: String): Result<Boolean> {
    return withContext(Dispatchers.IO) {
      try {
        val trimmedEmail = email.trim().lowercase()
        val user = database.userDao().getUserByEmail(trimmedEmail)
          ?: return@withContext Result.failure(Exception("No student found with this email address."))

        val newHash = PasswordSecurity.hashPassword(newPassword)
        database.userDao().updateUser(user.copy(passwordHash = newHash))
        Result.success(true)
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
  }

  suspend fun logout() {
    withContext(Dispatchers.IO) {
      prefs.edit().remove("logged_in_user_id").apply()
      _currentUser.value = null
    }
  }

  suspend fun refreshCurrentUser() {
    withContext(Dispatchers.IO) {
      val user = _currentUser.value ?: return@withContext
      val freshUser = database.userDao().getUserById(user.id)
      if (freshUser != null) {
        val updated = checkAndUpdatePremiumExpiry(freshUser)
        _currentUser.value = updated
      }
    }
  }

  private suspend fun checkAndUpdatePremiumExpiry(user: UserEntity): UserEntity {
    if (user.isPremium && user.role != "ADMIN") {
      val expiry = user.premiumExpiry
      if (expiry != null && System.currentTimeMillis() > expiry) {
        database.userDao().updatePremiumStatus(user.id, false, null, null)
        return user.copy(isPremium = false, premiumPlan = null, premiumExpiry = null)
      }
    }
    return user
  }
}
