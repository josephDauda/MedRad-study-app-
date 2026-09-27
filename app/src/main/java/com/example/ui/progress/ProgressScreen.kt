package com.example.ui.progress

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ExamAttemptEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.CbtRepository
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ProfessionalBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
  user: UserEntity,
  cbtRepository: CbtRepository,
  onNavigateBack: () -> Unit,
  onPracticeCourse: (courseId: Long) -> Unit,
  onNavigateToResults: (attemptId: Long) -> Unit
) {
  val attempts by cbtRepository.getUserAttempts(user.id).collectAsState(initial = emptyList())

  val totalExamsCompleted = attempts.size
  val totalQuestionsAttempted = attempts.sumOf { it.totalQuestions }
  val totalCorrect = attempts.sumOf { it.correctAnswers }
  val accuracy = if (totalQuestionsAttempted > 0) (totalCorrect * 100) / totalQuestionsAttempted else 0
  val averageScore = if (attempts.isNotEmpty()) attempts.sumOf { it.score } / attempts.size else 0

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Academic Performance",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AcademicNavy)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Student Progress Overview",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = AcademicNavy
        )
        Text(
          text = "${user.fullName} • RAD 101 Track",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Summary Metric Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          MetricCard(
            modifier = Modifier.weight(1f),
            title = "Avg. Score",
            value = "$averageScore%",
            subtitle = if (averageScore >= 50) "Passing Grade" else "Needs Improvement",
            color = if (averageScore >= 50) SuccessGreen else WarningAmber
          )
          MetricCard(
            modifier = Modifier.weight(1f),
            title = "Accuracy",
            value = "$accuracy%",
            subtitle = "$totalCorrect of $totalQuestionsAttempted correct",
            color = ProfessionalBlue
          )
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          MetricCard(
            modifier = Modifier.weight(1f),
            title = "Attempts",
            value = "$totalExamsCompleted",
            subtitle = "Exams & Practices",
            color = AcademicNavy
          )
          MetricCard(
            modifier = Modifier.weight(1f),
            title = "Questions Done",
            value = "$totalQuestionsAttempted",
            subtitle = "Evaluated items",
            color = Color(0xFF0D9488)
          )
        }
      }

      // Actionable Academic Insights (Section 14)
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, BorderSubtle)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Topic Mastery Breakdown",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = AcademicNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            TopicMasteryBar(
              topicName = "Discovery of X-rays (Röntgen 1895)",
              percentage = 85,
              status = "Strong",
              color = SuccessGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            TopicMasteryBar(
              topicName = "Principles of Radiation Protection (ALARA)",
              percentage = 78,
              status = "Good",
              color = ProfessionalBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            TopicMasteryBar(
              topicName = "X-ray Tube Construction & Anode Dynamics",
              percentage = 45,
              status = "Struggling",
              color = ErrorRed
            )

            Spacer(modifier = Modifier.height(10.dp))

            TopicMasteryBar(
              topicName = "Medical Ethics & Patient Care in Imaging",
              percentage = 68,
              status = "Moderate",
              color = WarningAmber
            )
          }
        }
      }

      // Next Recommendation Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
          border = BorderStroke(1.dp, Color(0xFFFDE68A))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Highlight, contentDescription = null, tint = Color(0xFF92400E))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "What should you practice next?",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF92400E)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Your results indicate weaker accuracy on 'X-ray Tube Construction & Heat Dissipation'. We recommend doing a focused 15-question drill on Tube Components.",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF78350F)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { onPracticeCourse(1L) },
              colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Practice Weak Topics Now")
            }
          }
        }
      }

      // History of attempts
      item {
        Text(
          text = "Examination History",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = AcademicNavy,
          modifier = Modifier.padding(top = 4.dp)
        )
      }

      if (attempts.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
              Text("No exams or practices recorded yet. Take your first CBT practice!", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      } else {
        items(attempts) { attempt ->
          AttemptHistoryItem(
            attempt = attempt,
            onClick = { onNavigateToResults(attempt.id) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun MetricCard(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  subtitle: String,
  color: Color
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, BorderSubtle)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = color
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )
    }
  }
}

@Composable
fun TopicMasteryBar(
  topicName: String,
  percentage: Int,
  status: String,
  color: Color
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = topicName,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = AcademicNavy,
        modifier = Modifier.weight(1f)
      )
      Text(
        text = "$percentage% ($status)",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = color
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { percentage / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = Color(0xFFF1F5F9)
    )
  }
}

@Composable
fun AttemptHistoryItem(
  attempt: ExamAttemptEntity,
  onClick: () -> Unit
) {
  val dateFormatted = remember(attempt.timestamp) {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(attempt.timestamp))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, BorderSubtle)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(
          text = attempt.examTitle,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = AcademicNavy
        )
        Text(
          text = "$dateFormatted • ${attempt.correctAnswers}/${attempt.totalQuestions} Correct",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (attempt.score >= 50) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
      ) {
        Text(
          text = "${attempt.score}%",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = if (attempt.score >= 50) SuccessGreen else ErrorRed,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }
  }
}
