package com.example.ui.admin

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CourseEntity
import com.example.data.local.entity.ExamEntity
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudyMaterialEntity
import com.example.data.local.entity.SubscriptionEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ProfessionalBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  user: UserEntity,
  adminViewModel: AdminViewModel,
  onNavigateBack: () -> Unit
) {
  // Security verification: Role-based authorization
  if (user.role != "ADMIN") {
    Scaffold { padding ->
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("Access Denied. Only university administrators can access this portal.")
      }
    }
    return
  }

  LaunchedEffect(Unit) {
    adminViewModel.loadStats()
  }

  val stats by adminViewModel.stats.collectAsState()
  val questions by adminViewModel.allQuestions.collectAsState()
  val exams by adminViewModel.allExams.collectAsState()
  val materials by adminViewModel.allMaterials.collectAsState()
  val students by adminViewModel.allStudents.collectAsState()
  val subscriptions by adminViewModel.allSubscriptions.collectAsState()
  val courses by adminViewModel.allCourses.collectAsState()

  var selectedSection by remember { mutableStateOf("Overview") }
  val sections = listOf("Overview", "Questions", "Exam Builder", "Study Materials", "Academic Setup", "Students", "Subscriptions")

  var showAddQuestionDialog by remember { mutableStateOf(false) }
  var showAddExamDialog by remember { mutableStateOf(false) }
  var showAddMaterialDialog by remember { mutableStateOf(false) }
  var showAddAcademicDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Administrator Management Console",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Text(
              text = "MEDRAD Platform Governance",
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AcademicNavy)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Horizontal Tab Navigation
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(sections) { sec ->
          FilterChip(
            selected = selectedSection == sec,
            onClick = { selectedSection = sec },
            label = { Text(sec) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AcademicNavy,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      HorizontalDivider(color = BorderSubtle)

      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
      ) {
        when (selectedSection) {
          "Overview" -> AdminOverviewView(
            stats = stats,
            onQuickAddQuestion = { showAddQuestionDialog = true },
            onQuickAddExam = { showAddExamDialog = true },
            onQuickAddMaterial = { showAddMaterialDialog = true },
            onQuickAddAcademic = { showAddAcademicDialog = true }
          )
          "Questions" -> AdminQuestionsView(
            questions = questions,
            onAddQuestion = { showAddQuestionDialog = true },
            onDeleteQuestion = { adminViewModel.deleteQuestion(it) }
          )
          "Exam Builder" -> AdminExamsView(
            exams = exams,
            onAddExam = { showAddExamDialog = true },
            onDeleteExam = { adminViewModel.deleteExam(it) }
          )
          "Study Materials" -> AdminMaterialsView(
            materials = materials,
            onAddMaterial = { showAddMaterialDialog = true },
            onDeleteMaterial = { adminViewModel.deleteMaterial(it) }
          )
          "Academic Setup" -> AdminAcademicSetupView(
            courses = courses,
            onAddCourse = { showAddAcademicDialog = true }
          )
          "Students" -> AdminStudentsView(students = students)
          "Subscriptions" -> AdminSubscriptionsView(subscriptions = subscriptions)
        }
      }
    }
  }

  // Modals for creating items
  if (showAddQuestionDialog) {
    AddQuestionDialog(
      courses = courses,
      onDismiss = { showAddQuestionDialog = false },
      onSave = { courseId, topicId, text, a, b, c, d, correct, explanation, diff, isPrem ->
        adminViewModel.createQuestion(courseId, topicId, text, a, b, c, d, correct, explanation, diff, isPrem) {
          showAddQuestionDialog = false
        }
      }
    )
  }

  if (showAddExamDialog) {
    AddExamDialog(
      courses = courses,
      onDismiss = { showAddExamDialog = false },
      onSave = { courseId, title, desc, duration, count, isPrem ->
        adminViewModel.createExam(courseId, title, desc, duration, count, isPrem) {
          showAddExamDialog = false
        }
      }
    )
  }

  if (showAddMaterialDialog) {
    AddMaterialDialog(
      courses = courses,
      onDismiss = { showAddMaterialDialog = false },
      onSave = { courseId, topicId, title, desc, type, body, isPrem ->
        adminViewModel.createMaterial(courseId, topicId, title, desc, type, body, isPrem) {
          showAddMaterialDialog = false
        }
      }
    )
  }

  if (showAddAcademicDialog) {
    AddAcademicHierarchyDialog(
      onDismiss = { showAddAcademicDialog = false },
      onSaveCourse = { code, title, dept, level, sem, desc ->
        adminViewModel.createCourse(1L, code, title, level, sem, desc) {
          showAddAcademicDialog = false
        }
      }
    )
  }
}

@Composable
fun AdminOverviewView(
  stats: com.example.data.repository.AdminDashboardStats,
  onQuickAddQuestion: () -> Unit,
  onQuickAddExam: () -> Unit,
  onQuickAddMaterial: () -> Unit,
  onQuickAddAcademic: () -> Unit
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
  currencyFormat.maximumFractionDigits = 0

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "Platform Statistics & Key Metrics",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = AcademicNavy
      )
    }

    // 2x2 Stats Grid
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Total Students",
          value = "${stats.totalStudents}",
          subtitle = "${stats.activeStudents} active learners",
          icon = Icons.Default.People,
          color = AcademicNavy
        )
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Premium Subs",
          value = "${stats.premiumSubscribers}",
          subtitle = "${stats.activeSubscriptions} active passes",
          icon = Icons.Default.WorkspacePremium,
          color = WarningAmber
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Exams Completed",
          value = "${stats.examsCompleted}",
          subtitle = "${stats.questionsAttempted} items answered",
          icon = Icons.Default.Assignment,
          color = ProfessionalBlue
        )
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Est. Revenue",
          value = "₦${stats.totalRevenueNaira.toInt()}",
          subtitle = "Paystack/Verified",
          icon = Icons.Default.AttachMoney,
          color = SuccessGreen
        )
      }
    }

    // Quick Admin Actions
    item {
      Text(
        text = "Quick Creation Tools",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = AcademicNavy,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onQuickAddQuestion,
          modifier = Modifier.weight(1f).height(46.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Question", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
        }

        Button(
          onClick = onQuickAddExam,
          modifier = Modifier.weight(1f).height(46.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ProfessionalBlue),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Build Exam", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
        }
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onQuickAddMaterial,
          modifier = Modifier.weight(1f).height(46.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Upload Note", style = MaterialTheme.typography.bodySmall)
        }

        OutlinedButton(
          onClick = onQuickAddAcademic,
          modifier = Modifier.weight(1f).height(46.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Course", style = MaterialTheme.typography.bodySmall)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun AdminStatCard(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, BorderSubtle)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = color)
      Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun AdminQuestionsView(
  questions: List<QuestionEntity>,
  onAddQuestion: () -> Unit,
  onDeleteQuestion: (QuestionEntity) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(12.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Question Bank (${questions.size} items)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = AcademicNavy
      )
      Button(
        onClick = onAddQuestion,
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Create Question")
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(questions) { q ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, BorderSubtle)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = if (q.isDemo) Color(0xFFFEF3C7) else AcademicNavy
                ) {
                  Text(
                    text = if (q.isDemo) "DEMO QUESTION" else "VERIFIED QUESTION",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = if (q.isDemo) Color(0xFF92400E) else Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                  text = "Correct: ${q.correctAnswer}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = SuccessGreen
                )
              }

              IconButton(onClick = { onDeleteQuestion(q) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(18.dp))
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(q.questionText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = AcademicNavy)

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "A) ${q.optionA} • B) ${q.optionB} • C) ${q.optionC} • D) ${q.optionD}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun AdminExamsView(
  exams: List<ExamEntity>,
  onAddExam: () -> Unit,
  onDeleteExam: (ExamEntity) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(12.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Mock Examination Builder",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = AcademicNavy
      )
      Button(
        onClick = onAddExam,
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Build Exam")
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(exams) { exam ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, BorderSubtle)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(exam.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AcademicNavy)
              Text("${exam.totalQuestions} Questions • ${exam.durationMinutes} Minutes Duration", style = MaterialTheme.typography.bodySmall, color = ProfessionalBlue)
              Text(exam.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }

            IconButton(onClick = { onDeleteExam(exam) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun AdminMaterialsView(
  materials: List<StudyMaterialEntity>,
  onAddMaterial: () -> Unit,
  onDeleteMaterial: (StudyMaterialEntity) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(12.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Study Materials & Notes",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = AcademicNavy
      )
      Button(
        onClick = onAddMaterial,
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Upload Note")
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(materials) { mat ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, BorderSubtle)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(mat.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AcademicNavy)
              Text("${mat.contentType} • ${if (mat.isPremium) "Premium" else "Free"}", style = MaterialTheme.typography.bodySmall, color = ProfessionalBlue)
              Text(mat.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }

            IconButton(onClick = { onDeleteMaterial(mat) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun AdminAcademicSetupView(
  courses: List<CourseEntity>,
  onAddCourse: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Spacer(modifier = Modifier.height(12.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Academic Hierarchy Configuration",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = AcademicNavy
      )
      Button(
        onClick = onAddCourse,
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add Course")
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "University → Faculty → Department → Level → Semester → Course → Topic",
      style = MaterialTheme.typography.bodySmall,
      color = ProfessionalBlue
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, BorderSubtle)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text("Active Universities", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AcademicNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Text("1. University of Maiduguri (UNIMAID) - Active", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Active Faculties & Departments", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AcademicNavy)
        Spacer(modifier = Modifier.height(6.dp))
        Text("• College of Medical Sciences -> Medical Radiography", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Configured Courses (${courses.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AcademicNavy)
        Spacer(modifier = Modifier.height(6.dp))
        courses.forEach { c ->
          Text("• ${c.code}: ${c.title} (${c.level} - ${c.semester})", style = MaterialTheme.typography.bodySmall)
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
fun AdminStudentsView(students: List<UserEntity>) {
  Column(modifier = Modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = "Enrolled Students (${students.size})",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = AcademicNavy
    )
    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(students) { s ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, BorderSubtle)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(s.fullName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AcademicNavy)
              Text(s.email, style = MaterialTheme.typography.bodySmall, color = ProfessionalBlue)
              Text("${s.university} • ${s.department} (${s.level})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (s.isPremium) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)
            ) {
              Text(
                text = if (s.isPremium) "PREMIUM" else "FREE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (s.isPremium) Color(0xFF92400E) else Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
fun AdminSubscriptionsView(subscriptions: List<SubscriptionEntity>) {
  Column(modifier = Modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = "Active Subscriptions & Orders (${subscriptions.size})",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = AcademicNavy
    )
    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(subscriptions) { sub ->
        val dateFormatted = remember(sub.createdAt) {
          SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(sub.createdAt))
        }

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, BorderSubtle)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(sub.planName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AcademicNavy)
              Text("₦${sub.amount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = SuccessGreen)
            }

            Text("Ref: ${sub.paymentReference}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Date: $dateFormatted • Status: ${sub.subscriptionStatus}", style = MaterialTheme.typography.bodySmall, color = ProfessionalBlue)
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

// Dialogs for Admin Add actions
@Composable
fun AddQuestionDialog(
  courses: List<CourseEntity>,
  onDismiss: () -> Unit,
  onSave: (Long, Long, String, String, String, String, String, String, String, String, Boolean) -> Unit
) {
  var questionText by remember { mutableStateOf("") }
  var optionA by remember { mutableStateOf("") }
  var optionB by remember { mutableStateOf("") }
  var optionC by remember { mutableStateOf("") }
  var optionD by remember { mutableStateOf("") }
  var correctAnswer by remember { mutableStateOf("A") }
  var explanation by remember { mutableStateOf("") }
  var isPremium by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create New Question", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedTextField(
          value = questionText,
          onValueChange = { questionText = it },
          label = { Text("Question Text") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = optionC, onValueChange = { optionC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = optionD, onValueChange = { optionD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = correctAnswer,
          onValueChange = { correctAnswer = it.uppercase() },
          label = { Text("Correct Answer (A, B, C, or D)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = explanation,
          onValueChange = { explanation = it },
          label = { Text("Clinical Explanation & Rationale") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Premium Question:", style = MaterialTheme.typography.bodySmall)
          Switch(checked = isPremium, onCheckedChange = { isPremium = it })
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (questionText.isNotBlank() && optionA.isNotBlank() && optionB.isNotBlank()) {
            onSave(1L, 1L, questionText, optionA, optionB, optionC, optionD, correctAnswer, explanation, "Medium", isPremium)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
      ) {
        Text("Save Question")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun AddExamDialog(
  courses: List<CourseEntity>,
  onDismiss: () -> Unit,
  onSave: (Long, String, String, Int, Int, Boolean) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var duration by remember { mutableStateOf("30") }
  var count by remember { mutableStateOf("15") }
  var isPremium by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Build Examination", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
    text = {
      Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Exam Title (e.g. RAD 101 Mock 2)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (Minutes)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = count, onValueChange = { count = it }, label = { Text("Number of Questions") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Premium Exam:", style = MaterialTheme.typography.bodySmall)
          Switch(checked = isPremium, onCheckedChange = { isPremium = it })
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onSave(1L, title, description, duration.toIntOrNull() ?: 30, count.toIntOrNull() ?: 15, isPremium)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
      ) {
        Text("Save Exam")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun AddMaterialDialog(
  courses: List<CourseEntity>,
  onDismiss: () -> Unit,
  onSave: (Long, Long, String, String, String, String, Boolean) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var type by remember { mutableStateOf("Lecture Note") }
  var body by remember { mutableStateOf("") }
  var isPremium by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Upload Study Note / Material", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
    text = {
      Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (Lecture Note, Past Question, etc)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Document Content / Outline") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Premium Material:", style = MaterialTheme.typography.bodySmall)
          Switch(checked = isPremium, onCheckedChange = { isPremium = it })
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onSave(1L, 1L, title, description, type, body, isPremium)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
      ) {
        Text("Save Material")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun AddAcademicHierarchyDialog(
  onDismiss: () -> Unit,
  onSaveCourse: (String, String, String, String, String, String) -> Unit
) {
  var code by remember { mutableStateOf("") }
  var title by remember { mutableStateOf("") }
  var dept by remember { mutableStateOf("Medical Radiography") }
  var level by remember { mutableStateOf("100 Level") }
  var sem by remember { mutableStateOf("First Semester") }
  var desc by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Academic Course", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
    text = {
      Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. RAD 102)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Course Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = dept, onValueChange = { dept = it }, label = { Text("Department") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Level") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = sem, onValueChange = { sem = it }, label = { Text("Semester") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (code.isNotBlank() && title.isNotBlank()) {
            onSaveCourse(code, title, dept, level, sem, desc)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy)
      ) {
        Text("Save Course")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
