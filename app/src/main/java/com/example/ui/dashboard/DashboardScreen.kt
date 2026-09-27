package com.example.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.ExamAttemptEntity
import com.example.data.local.entity.ExamEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ProfessionalBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  user: UserEntity,
  dashboardViewModel: DashboardViewModel,
  onStartPractice: (courseId: Long) -> Unit,
  onStartExam: (ExamEntity) -> Unit,
  onNavigateToMaterials: () -> Unit,
  onNavigateToSubscription: () -> Unit,
  onNavigateToResults: (attemptId: Long) -> Unit,
  onNavigateToCourses: () -> Unit
) {
  LaunchedEffect(user.id) {
    dashboardViewModel.loadDashboard(user)
  }

  val dailyAllowance by dashboardViewModel.dailyAllowance.collectAsState()
  val recentAttempts by dashboardViewModel.recentAttempts.collectAsState()
  val activeExams by dashboardViewModel.activeExams.collectAsState()

  val greeting = rememberGreeting()

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
      // 1. Welcome Header & Academic Identity
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "$greeting, ${user.fullName.split(" ").firstOrNull() ?: user.fullName}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AcademicNavy
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${user.university} • ${user.department}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${user.level} • ${user.semester}",
            style = MaterialTheme.typography.labelSmall,
            color = ProfessionalBlue
          )
        }

        // Tier Badge
        if (user.isPremium || user.role == "ADMIN") {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = AcademicNavy,
            contentColor = Color.White
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp), tint = WarningAmber)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (user.role == "ADMIN") "ADMIN" else "PREMIUM",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        } else {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF1F5F9),
            modifier = Modifier.clickable { onNavigateToSubscription() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "FREE TIER",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = WarningAmber)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Hero Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
          Image(
            painter = painterResource(id = R.drawable.medrad_hero),
            contentDescription = "Medical Radiography Academic Squad",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                androidx.compose.ui.graphics.Brush.horizontalGradient(
                  colors = listOf(
                    AcademicNavy.copy(alpha = 0.92f),
                    AcademicNavy.copy(alpha = 0.65f),
                    Color.Transparent
                  )
                )
              )
          )
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = "RAD 101",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = WarningAmber
            )
            Text(
              text = "Introduction to Radiography",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Text(
              text = "5 Core Topics • 12+ Practice Questions • 1 Mock Exam",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFE2E8F0)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Daily CBT Allowance Card (Section 5)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (user.isPremium || user.role == "ADMIN") Color(0xFFF0FDF4) else Color(0xFFFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Daily Practice Allowance",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = AcademicNavy
              )
              Text(
                text = if (user.isPremium || user.role == "ADMIN") {
                  "Unlimited questions active (Premium Account)"
                } else if (dailyAllowance > 0) {
                  "$dailyAllowance / 15 questions remaining today"
                } else {
                  "You've used today's free practice questions."
                },
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = if (dailyAllowance == 0 && !user.isPremium && user.role != "ADMIN") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (dailyAllowance > 0 || user.isPremium) Color(0xFFE0EBF7) else Color(0xFFFEE2E2)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (user.isPremium || user.role == "ADMIN") Icons.Default.Stars else Icons.Default.Timer,
                contentDescription = null,
                tint = if (dailyAllowance > 0 || user.isPremium) ProfessionalBlue else MaterialTheme.colorScheme.error
              )
            }
          }

          if (!user.isPremium && user.role != "ADMIN") {
            Spacer(modifier = Modifier.height(12.dp))
            val progress = (dailyAllowance.toFloat() / 15f).coerceIn(0f, 1f)
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = if (dailyAllowance > 4) ProfessionalBlue else WarningAmber,
              trackColor = Color(0xFFE2E8F0)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Action Button
          if (dailyAllowance > 0 || user.isPremium || user.role == "ADMIN") {
            Button(
              onClick = { onStartPractice(1L) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("continue_practice_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Continue Practice (RAD 101)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            }
          } else {
            Button(
              onClick = onNavigateToSubscription,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("upgrade_from_dashboard_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
            ) {
              Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Black)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Upgrade for Unlimited Practice (₦3,500)", color = Color.Black, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Compact 2x2 Grid for Core Actions: Recent Result, Study Materials, Mock Exams, Subscriptions
      Text(
        text = "Academic Center",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = AcademicNavy,
        modifier = Modifier.padding(bottom = 10.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Card: Mock Exams
        CompactDashboardCard(
          modifier = Modifier.weight(1f),
          title = "Mock Exams",
          subtitle = "${activeExams.size} Active Exam",
          badge = "Timed",
          icon = Icons.Default.Assignment,
          iconTint = ProfessionalBlue,
          onClick = {
            val exam = activeExams.firstOrNull()
            if (exam != null) {
              onStartExam(exam)
            } else {
              onNavigateToCourses()
            }
          }
        )

        // Card: Study Materials
        CompactDashboardCard(
          modifier = Modifier.weight(1f),
          title = "Study Materials",
          subtitle = "Notes & Solutions",
          badge = "4 Available",
          icon = Icons.Default.MenuBook,
          iconTint = Color(0xFF0D9488),
          onClick = onNavigateToMaterials
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Card: Recent Result
        val lastAttempt = recentAttempts.firstOrNull()
        CompactDashboardCard(
          modifier = Modifier.weight(1f),
          title = "Recent Result",
          subtitle = if (lastAttempt != null) "${lastAttempt.score}% (${lastAttempt.correctAnswers}/${lastAttempt.totalQuestions})" else "No attempts yet",
          badge = if (lastAttempt != null) "View" else "Start",
          icon = Icons.Default.CheckCircle,
          iconTint = if (lastAttempt != null && lastAttempt.score >= 50) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
          onClick = {
            if (lastAttempt != null) {
              onNavigateToResults(lastAttempt.id)
            } else {
              onStartPractice(1L)
            }
          }
        )

        // Card: Subscription
        CompactDashboardCard(
          modifier = Modifier.weight(1f),
          title = "Subscription",
          subtitle = if (user.isPremium) "${user.premiumPlan ?: "Active Plan"}" else "Free Tier (15 Qs)",
          badge = if (user.isPremium) "Active" else "Plans",
          icon = Icons.Default.WorkspacePremium,
          iconTint = WarningAmber,
          onClick = onNavigateToSubscription
        )
      }

      // Recent Activity / Result Preview if available
      if (recentAttempts.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Latest Performance",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AcademicNavy
          )
          Text(
            text = "Full Review",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = ProfessionalBlue,
            modifier = Modifier.clickable {
              val first = recentAttempts.firstOrNull()
              if (first != null) onNavigateToResults(first.id)
            }
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        recentAttempts.take(2).forEach { attempt ->
          RecentAttemptRow(
            attempt = attempt,
            onClick = { onNavigateToResults(attempt.id) }
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun CompactDashboardCard(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String,
  badge: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(iconTint.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = AcademicNavy
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )
    }
  }
}

@Composable
fun RecentAttemptRow(
  attempt: ExamAttemptEntity,
  onClick: () -> Unit
) {
  val dateFormatted = remember(attempt.timestamp) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    sdf.format(Date(attempt.timestamp))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(if (attempt.score >= 50) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${attempt.score}%",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (attempt.score >= 50) SuccessGreen else MaterialTheme.colorScheme.error
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = attempt.examTitle,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AcademicNavy
          )
          Text(
            text = "${attempt.correctAnswers}/${attempt.totalQuestions} Correct • $dateFormatted",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Icon(
        Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Review",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
fun rememberGreeting(): String {
  val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  return when {
    hour < 12 -> "Good morning"
    hour < 17 -> "Good afternoon"
    else -> "Good evening"
  }
}
