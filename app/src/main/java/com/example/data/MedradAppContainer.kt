package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.repository.AcademicRepository
import com.example.data.repository.AdminRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.CbtRepository
import com.example.data.repository.SubscriptionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedradAppContainer(val context: Context) {
  val database: AppDatabase by lazy {
    AppDatabase.getDatabase(context)
  }

  val authRepository: AuthRepository by lazy {
    AuthRepository(context, database)
  }

  val cbtRepository: CbtRepository by lazy {
    CbtRepository(database)
  }

  val academicRepository: AcademicRepository by lazy {
    AcademicRepository(database)
  }

  val subscriptionRepository: SubscriptionRepository by lazy {
    SubscriptionRepository(database)
  }

  val adminRepository: AdminRepository by lazy {
    AdminRepository(database)
  }

  init {
    CoroutineScope(Dispatchers.IO).launch {
      DatabaseInitializer.populateInitialData(database)
    }
  }

  companion object {
    @Volatile
    private var INSTANCE: MedradAppContainer? = null

    fun getInstance(context: Context): MedradAppContainer {
      return INSTANCE ?: synchronized(this) {
        val instance = MedradAppContainer(context.applicationContext)
        INSTANCE = instance
        instance
      }
    }
  }
}
