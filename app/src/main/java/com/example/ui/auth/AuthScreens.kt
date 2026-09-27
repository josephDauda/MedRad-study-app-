package com.example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.ProfessionalBlue
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.UiState

@Composable
fun LoginScreen(
  authViewModel: AuthViewModel,
  onNavigateToRegister: () -> Unit,
  onLoginSuccess: (UserEntity) -> Unit
) {
  var email by remember { mutableStateOf("joseph@medrad.edu.ng") }
  var password by remember { mutableStateOf("Student@12345") }
  var passwordVisible by remember { mutableStateOf(false) }
  var showResetDialog by remember { mutableStateOf(false) }

  val authState by authViewModel.authActionState.collectAsState()

  LaunchedEffect(authState) {
    if (authState is UiState.Success) {
      onLoginSuccess((authState as UiState.Success<UserEntity>).data)
      authViewModel.clearState()
    }
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // App Logo & Header
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(AcademicNavy),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.medrad_logo),
          contentDescription = "MEDRAD Logo",
          modifier = Modifier.size(70.dp).clip(CircleShape),
          contentScale = ContentScale.Crop
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "MedRad Study app",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          color = AcademicNavy,
          letterSpacing = 0.5.sp
        ),
        textAlign = TextAlign.Center
      )

      Text(
        text = "Study smarter. Practice harder. Perform better.",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
        ) {
          Text(
            text = "Student & Faculty Login",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Enter your university credentials to continue",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp)
          )

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Institutional Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("email_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("password_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp)
          )

          if (authState is UiState.Error) {
            Text(
              text = (authState as UiState.Error).message,
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(top = 8.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(
              onClick = { showResetDialog = true },
              modifier = Modifier.testTag("forgot_password_button")
            ) {
              Text(
                "Forgot password?",
                style = MaterialTheme.typography.labelMedium,
                color = ProfessionalBlue
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("login_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
            enabled = authState !is UiState.Loading
          ) {
            if (authState is UiState.Loading) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
              Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Quick Demo Switcher Card for convenience
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          Text(
            text = "Quick Demo Accounts (Instant Fill):",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                email = "joseph@medrad.edu.ng"
                password = "Student@12345"
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Joseph (Student)", style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(
              onClick = {
                email = "admin@medrad.edu.ng"
                password = "Admin@12345"
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Prof. Bello (Admin)", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Don't have an account?",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
          onClick = onNavigateToRegister,
          modifier = Modifier.testTag("navigate_to_register_button")
        ) {
          Text(
            "Create Account",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = ProfessionalBlue
          )
        }
      }
    }
  }

  if (showResetDialog) {
    ResetPasswordDialog(
      authViewModel = authViewModel,
      initialEmail = email,
      onDismiss = { showResetDialog = false }
    )
  }
}

@Composable
fun RegisterScreen(
  authViewModel: AuthViewModel,
  onNavigateToLogin: () -> Unit,
  onRegisterSuccess: (UserEntity) -> Unit
) {
  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  // Academic hierarchy fields
  var university by remember { mutableStateOf("University of Maiduguri") }
  var faculty by remember { mutableStateOf("College of Medical Sciences") }
  var department by remember { mutableStateOf("Medical Radiography") }
  var level by remember { mutableStateOf("100 Level") }
  var semester by remember { mutableStateOf("First Semester") }

  val authState by authViewModel.authActionState.collectAsState()

  LaunchedEffect(authState) {
    if (authState is UiState.Success) {
      onRegisterSuccess((authState as UiState.Success<UserEntity>).data)
      authViewModel.clearState()
    }
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "MedRad Study app",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = AcademicNavy
        )
      )

      Text(
        text = "Create your academic profile to start CBT practice",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text(
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = AcademicNavy
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name (e.g. Joseph Audu)") },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number (e.g. +234 80...)") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min 6 characters)") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().testTag("reg_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(24.dp))

          Text(
            text = "Academic Hierarchy & Placement",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = AcademicNavy
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = university,
            onValueChange = { university = it },
            label = { Text("University") },
            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = faculty,
            onValueChange = { faculty = it },
            label = { Text("Faculty / College") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = department,
            onValueChange = { department = it },
            label = { Text("Department") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = level,
              onValueChange = { level = it },
              label = { Text("Level") },
              singleLine = true,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
              value = semester,
              onValueChange = { semester = it },
              label = { Text("Semester") },
              singleLine = true,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }

          if (authState is UiState.Error) {
            Text(
              text = (authState as UiState.Error).message,
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(top = 12.dp)
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

          Button(
            onClick = {
              authViewModel.register(
                fullName = fullName,
                email = email,
                phone = phoneNumber,
                pass = password,
                uni = university,
                faculty = faculty,
                dept = department,
                level = level,
                semester = semester
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("submit_register_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
            enabled = authState !is UiState.Loading
          ) {
            if (authState is UiState.Loading) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
              Text(
                text = "Complete Registration",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Already registered?",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
          onClick = onNavigateToLogin,
          modifier = Modifier.testTag("navigate_to_login_button")
        ) {
          Text(
            "Sign In",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = ProfessionalBlue
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun ResetPasswordDialog(
  authViewModel: AuthViewModel,
  initialEmail: String,
  onDismiss: () -> Unit
) {
  var resetEmail by remember { mutableStateOf(initialEmail) }
  var newPassword by remember { mutableStateOf("") }
  val resetState by authViewModel.resetPasswordState.collectAsState()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Reset Account Password") },
    text = {
      Column {
        Text(
          "Enter your registered institutional email address and a new password.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = resetEmail,
          onValueChange = { resetEmail = it },
          label = { Text("Email") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = newPassword,
          onValueChange = { newPassword = it },
          label = { Text("New Password (min 6 chars)") },
          singleLine = true,
          visualTransformation = PasswordVisualTransformation(),
          modifier = Modifier.fillMaxWidth()
        )
        if (resetState is UiState.Error) {
          Text(
            text = (resetState as UiState.Error).message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
          )
        }
        if (resetState is UiState.Success) {
          Text(
            text = (resetState as UiState.Success<String>).data,
            color = Color(0xFF10B981),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { authViewModel.resetPassword(resetEmail, newPassword) },
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
      ) {
        Text("Update Password")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
