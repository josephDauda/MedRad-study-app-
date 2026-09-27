package com.example.ui.cbt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ProfessionalBlue
import com.example.ui.theme.ProfessionalBlueLight
import com.example.ui.theme.ReviewPurple
import com.example.ui.theme.ReviewPurpleLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.CbtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CbtScreen(
  user: UserEntity,
  cbtViewModel: CbtViewModel,
  onExamSubmitted: (attemptId: Long) -> Unit,
  onNavigateBack: () -> Unit,
  onNavigateToSubscription: () -> Unit
) {
  val sessionState by cbtViewModel.sessionState.collectAsState()
  var showNavigatorSheet by remember { mutableStateOf(false) }

  // Navigate when submitted
  LaunchedEffect(sessionState.isSubmitted, sessionState.submittedAttemptId) {
    if (sessionState.isSubmitted && sessionState.submittedAttemptId != null) {
      onExamSubmitted(sessionState.submittedAttemptId!!)
    }
  }

  // Allowance Exhausted State check
  if (sessionState.isAllowanceExhausted && !sessionState.isExamMode) {
    AllowanceExhaustedDialog(
      onUpgrade = onNavigateToSubscription,
      onClose = onNavigateBack
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = sessionState.examTitle,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Text(
              text = if (sessionState.isExamMode) "Timed Mock Examination" else {
                if (user.isPremium || user.role == "ADMIN") "Unlimited Practice (Premium)"
                else "${sessionState.dailyAllowanceRemaining} free questions remaining today"
              },
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFCBD5E1)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        actions = {
          // Timer if exam mode
          if (sessionState.isExamMode) {
            val minutes = sessionState.timeRemainingSeconds / 60
            val seconds = sessionState.timeRemainingSeconds % 60
            val isUrgent = sessionState.timeRemainingSeconds < 180

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isUrgent) Color(0xFFEF4444) else AcademicNavy.copy(alpha = 0.8f),
              border = BorderStroke(1.dp, if (isUrgent) Color.White else BorderSubtle),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "%02d:%02d".format(minutes, seconds),
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
            }
          }

          // Navigator Button for mobile
          Button(
            onClick = { showNavigatorSheet = true },
            colors = ButtonDefaults.buttonColors(containerColor = ProfessionalBlue),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Text(
              text = "${sessionState.currentIndex + 1}/${sessionState.questions.size}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AcademicNavy)
      )
    },
    bottomBar = {
      // Bottom Action Controls
      CbtBottomBar(
        hasPrevious = sessionState.currentIndex > 0,
        hasNext = sessionState.currentIndex < sessionState.questions.size - 1,
        isMarked = sessionState.markedForReview.contains(sessionState.currentIndex),
        onPrevious = { cbtViewModel.previousQuestion() },
        onNext = { cbtViewModel.nextQuestion() },
        onToggleMark = { cbtViewModel.toggleMarkForReview() },
        onSubmit = { cbtViewModel.showSubmissionPrompt(true) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (sessionState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = AcademicNavy)
        }
      } else if (sessionState.questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("No questions currently available for this module.", style = MaterialTheme.typography.bodyMedium)
        }
      } else {
        val currentQuestion = sessionState.questions.getOrNull(sessionState.currentIndex)
        if (currentQuestion != null) {
          QuestionView(
            questionIndex = sessionState.currentIndex,
            totalQuestions = sessionState.questions.size,
            question = currentQuestion,
            selectedOption = sessionState.userAnswers[sessionState.currentIndex],
            isMarked = sessionState.markedForReview.contains(sessionState.currentIndex),
            onSelectOption = { option -> cbtViewModel.selectAnswer(option, user) },
            onToggleMark = { cbtViewModel.toggleMarkForReview() }
          )
        }
      }
    }
  }

  // Pre-submission confirmation dialog (Section 7)
  if (sessionState.showSubmissionDialog) {
    val total = sessionState.questions.size
    val answered = sessionState.userAnswers.size
    val unanswered = total - answered
    val marked = sessionState.markedForReview.size

    AlertDialog(
      onDismissRequest = { cbtViewModel.showSubmissionPrompt(false) },
      title = {
        Text("Submit Examination?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Review your progress before final evaluation. Once submitted, your scores and explanations will be generated.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Summary Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            SubmissionMetricItem("Answered", "$answered", SuccessGreen, SuccessGreenLight)
            SubmissionMetricItem("Unanswered", "$unanswered", ErrorRed, Color(0xFFFEE2E2))
            SubmissionMetricItem("Marked", "$marked", ReviewPurple, ReviewPurpleLight)
          }

          if (unanswered > 0) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "⚠️ You have $unanswered unanswered question(s).",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = WarningAmber
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { cbtViewModel.submitExam(user) },
          colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("confirm_submit_exam_button")
        ) {
          Text("Submit Now")
        }
      },
      dismissButton = {
        TextButton(onClick = { cbtViewModel.showSubmissionPrompt(false) }) {
          Text("Return to Exam")
        }
      }
    )
  }

  // Question Navigator Bottom Sheet
  if (showNavigatorSheet) {
    ModalBottomSheet(
      onDismissRequest = { showNavigatorSheet = false },
      sheetState = rememberModalBottomSheetState()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Question Navigator",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = AcademicNavy
          )
          Text(
            text = "${sessionState.userAnswers.size} / ${sessionState.questions.size} Answered",
            style = MaterialTheme.typography.labelMedium,
            color = ProfessionalBlue
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          LegendIndicator("Answered", SuccessGreen)
          LegendIndicator("Current", AcademicNavy)
          LegendIndicator("Marked", ReviewPurple)
          LegendIndicator("Unanswered", Color(0xFFCBD5E1))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
          columns = GridCells.Fixed(5),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
        ) {
          itemsIndexed(sessionState.questions) { index, _ ->
            val isCurrent = index == sessionState.currentIndex
            val isAnswered = sessionState.userAnswers.containsKey(index)
            val isMarked = sessionState.markedForReview.contains(index)

            val bgColor = when {
              isCurrent -> AcademicNavy
              isAnswered -> SuccessGreen
              isMarked -> ReviewPurple
              else -> Color(0xFFF1F5F9)
            }

            val textColor = when {
              isCurrent || isAnswered || isMarked -> Color.White
              else -> AcademicNavy
            }

            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .border(
                  width = if (isCurrent) 2.dp else 1.dp,
                  color = if (isCurrent) WarningAmber else Color.Transparent,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                  cbtViewModel.navigateTo(index)
                  showNavigatorSheet = false
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            showNavigatorSheet = false
            cbtViewModel.showSubmissionPrompt(true)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Finish & Submit Exam")
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun QuestionView(
  questionIndex: Int,
  totalQuestions: Int,
  question: QuestionEntity,
  selectedOption: String?,
  isMarked: Boolean,
  onSelectOption: (String) -> Unit,
  onToggleMark: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Top question meta row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = AcademicNavy,
          contentColor = Color.White
        ) {
          Text(
            text = "Question ${questionIndex + 1} of $totalQuestions",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        if (question.isDemo) {
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFFEF3C7),
            contentColor = Color(0xFF92400E)
          ) {
            Text(
              text = "Demo Question",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }

      IconButton(onClick = onToggleMark) {
        Icon(
          imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = "Mark for Review",
          tint = if (isMarked) ReviewPurple else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Question Prompt Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      border = BorderStroke(1.dp, BorderSubtle)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Text(
          text = question.questionText,
          style = MaterialTheme.typography.titleMedium.copy(lineHeight = 24.sp, fontWeight = FontWeight.Medium),
          color = AcademicNavy
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Select one answer option:",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    // Option Cards: A, B, C, D
    val options = listOf(
      "A" to question.optionA,
      "B" to question.optionB,
      "C" to question.optionC,
      "D" to question.optionD
    )

    options.forEach { (key, text) ->
      OptionCard(
        optionKey = key,
        optionText = text,
        isSelected = selectedOption.equals(key, ignoreCase = true),
        onSelect = { onSelectOption(key) }
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
  }
}

@Composable
fun OptionCard(
  optionKey: String,
  optionText: String,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  val borderColor = if (isSelected) ProfessionalBlue else BorderSubtle
  val containerColor = if (isSelected) ProfessionalBlueLight else MaterialTheme.colorScheme.surface

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onSelect() }
      .testTag("option_${optionKey}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(if (isSelected) ProfessionalBlue else Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = optionKey,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = if (isSelected) Color.White else AcademicNavy
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Text(
        text = optionText,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal),
        color = if (isSelected) AcademicNavy else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.weight(1f)
      )

      if (isSelected) {
        Icon(
          Icons.Default.CheckCircle,
          contentDescription = "Selected",
          tint = ProfessionalBlue,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun CbtBottomBar(
  hasPrevious: Boolean,
  hasNext: Boolean,
  isMarked: Boolean,
  onPrevious: () -> Unit,
  onNext: () -> Unit,
  onToggleMark: () -> Unit,
  onSubmit: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp,
    shadowElevation = 8.dp,
    border = BorderStroke(1.dp, BorderSubtle)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = onPrevious,
        enabled = hasPrevious,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("cbt_previous_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Prev")
      }

      OutlinedButton(
        onClick = onToggleMark,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = if (isMarked) ReviewPurple else MaterialTheme.colorScheme.onSurfaceVariant
        )
      ) {
        Icon(
          if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(if (isMarked) "Marked" else "Review")
      }

      if (hasNext) {
        Button(
          onClick = onNext,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
          modifier = Modifier.testTag("cbt_next_button")
        ) {
          Text("Next")
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
      } else {
        Button(
          onClick = onSubmit,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
          modifier = Modifier.testTag("cbt_submit_button")
        ) {
          Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Submit")
        }
      }
    }
  }
}

@Composable
fun SubmissionMetricItem(
  label: String,
  value: String,
  tintColor: Color,
  bgColor: Color
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .background(bgColor)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = tintColor
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = tintColor
      )
    }
  }
}

@Composable
fun LegendIndicator(label: String, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(10.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
  }
}

@Composable
fun AllowanceExhaustedDialog(
  onUpgrade: () -> Unit,
  onClose: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onClose,
    icon = {
      Icon(Icons.Default.Lock, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(36.dp))
    },
    title = {
      Text(
        "You've used today's free practice questions.",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
      )
    },
    text = {
      Text(
        "Free students receive 15 CBT practice questions every day. The allowance automatically resets tomorrow.\n\nTo practice without daily limits and unlock complete question banks, upgrade to a MEDRAD Premium Pass.",
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center
      )
    },
    confirmButton = {
      Button(
        onClick = onUpgrade,
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("View Premium Plans (₦3,500)")
      }
    },
    dismissButton = {
      TextButton(onClick = onClose) {
        Text("Done for Today")
      }
    }
  )
}
