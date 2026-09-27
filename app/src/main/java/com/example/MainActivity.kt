package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.MedradAppContainer
import com.example.ui.navigation.MedradNavHost
import com.example.ui.theme.MedradTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val appContainer = MedradAppContainer.getInstance(applicationContext)

    setContent {
      MedradTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          MedradNavHost(container = appContainer)
        }
      }
    }
  }
}
