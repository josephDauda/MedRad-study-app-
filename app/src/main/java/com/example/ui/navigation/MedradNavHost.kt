package com.example.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MedradAppContainer
import com.example.data.local.entity.ExamEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.cbt.CbtScreen
import com.example.ui.courses.CoursesScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.materials.StudyMaterialsScreen
import com.example.ui.progress.ProgressScreen
import com.example.ui.results.ResultsScreen
import com.example.ui.subscription.SubscriptionScreen
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ProfessionalBlue
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.CbtViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.ResultsViewModel
import com.example.ui.viewmodel.SubscriptionViewModel
import kotlinx.coroutines.launch

enum class ScreenRoute {
  LOGIN,
  REGISTER,
  DASHBOARD,
  COURSES,
  MATERIALS,
  PROGRESS,
  SUBSCRIPTION,
  ADMIN,
  CBT_SESSION,
  RESULTS
}

@Composable
fun MedradNavHost(
  container: MedradAppContainer
) {
  val authViewModel = remember { AuthViewModel(container.authRepository) }
  val dashboardViewModel = remember {
    DashboardViewModel(container.cbtRepository, container.academicRepository, container.subscriptionRepository)
  }
  val cbtViewModel = remember { CbtViewModel(container.cbtRepository) }
  val resultsViewModel = remember { ResultsViewModel(container.cbtRepository, container.academicRepository) }
  val subscriptionViewModel = remember { SubscriptionViewModel(container.subscriptionRepository, container.authRepository) }
  val adminViewModel = remember { AdminViewModel(container.adminRepository, container.academicRepository) }

  val currentUser by authViewModel.currentUser.collectAsState()

  var currentRoute by remember { mutableStateOf(ScreenRoute.LOGIN) }
  var currentAttemptId by remember { mutableLongStateOf(0L) }

  // Check auth session
  if (currentUser != null && (currentRoute == ScreenRoute.LOGIN || currentRoute == ScreenRoute.REGISTER)) {
    currentRoute = ScreenRoute.DASHBOARD
  } else if (currentUser == null && currentRoute != ScreenRoute.REGISTER) {
    currentRoute = ScreenRoute.LOGIN
  }

  when (currentRoute) {
    ScreenRoute.LOGIN -> {
      LoginScreen(
        authViewModel = authViewModel,
        onNavigateToRegister = { currentRoute = ScreenRoute.REGISTER },
        onLoginSuccess = { currentRoute = ScreenRoute.DASHBOARD }
      )
    }

    ScreenRoute.REGISTER -> {
      RegisterScreen(
        authViewModel = authViewModel,
        onNavigateToLogin = { currentRoute = ScreenRoute.LOGIN },
        onRegisterSuccess = { currentRoute = ScreenRoute.DASHBOARD }
      )
    }

    ScreenRoute.CBT_SESSION -> {
      val user = currentUser
      if (user != null) {
        CbtScreen(
          user = user,
          cbtViewModel = cbtViewModel,
          onExamSubmitted = { attemptId ->
            currentAttemptId = attemptId
            currentRoute = ScreenRoute.RESULTS
          },
          onNavigateBack = { currentRoute = ScreenRoute.DASHBOARD },
          onNavigateToSubscription = { currentRoute = ScreenRoute.SUBSCRIPTION }
        )
      }
    }

    ScreenRoute.RESULTS -> {
      ResultsScreen(
        attemptId = currentAttemptId,
        resultsViewModel = resultsViewModel,
        onNavigateToDashboard = { currentRoute = ScreenRoute.DASHBOARD },
        onPracticeAgain = {
          val user = currentUser
          if (user != null) {
            cbtViewModel.startPractice(1L, user)
            currentRoute = ScreenRoute.CBT_SESSION
          }
        }
      )
    }

    ScreenRoute.ADMIN -> {
      val user = currentUser
      if (user != null && user.role == "ADMIN") {
        AdminDashboardScreen(
          user = user,
          adminViewModel = adminViewModel,
          onNavigateBack = { currentRoute = ScreenRoute.DASHBOARD }
        )
      } else {
        currentRoute = ScreenRoute.DASHBOARD
      }
    }

    else -> {
      // Main authenticated app frame with navigation
      val user = currentUser
      if (user != null) {
        MainAuthenticatedScaffold(
          user = user,
          currentRoute = currentRoute,
          onNavigate = { route -> currentRoute = route },
          onLogout = {
            authViewModel.logout()
            currentRoute = ScreenRoute.LOGIN
          },
          content = {
            when (currentRoute) {
              ScreenRoute.DASHBOARD -> DashboardScreen(
                user = user,
                dashboardViewModel = dashboardViewModel,
                onStartPractice = { courseId ->
                  cbtViewModel.startPractice(courseId, user)
                  currentRoute = ScreenRoute.CBT_SESSION
                },
                onStartExam = { exam ->
                  cbtViewModel.startMockExam(exam, user)
                  currentRoute = ScreenRoute.CBT_SESSION
                },
                onNavigateToMaterials = { currentRoute = ScreenRoute.MATERIALS },
                onNavigateToSubscription = { currentRoute = ScreenRoute.SUBSCRIPTION },
                onNavigateToResults = { attemptId ->
                  currentAttemptId = attemptId
                  currentRoute = ScreenRoute.RESULTS
                },
                onNavigateToCourses = { currentRoute = ScreenRoute.COURSES }
              )
              ScreenRoute.COURSES -> CoursesScreen(
                user = user,
                academicRepository = container.academicRepository,
                onNavigateBack = { currentRoute = ScreenRoute.DASHBOARD },
                onStartCoursePractice = { courseId ->
                  cbtViewModel.startPractice(courseId, user)
                  currentRoute = ScreenRoute.CBT_SESSION
                }
              )
              ScreenRoute.MATERIALS -> StudyMaterialsScreen(
                user = user,
                academicRepository = container.academicRepository,
                onNavigateBack = { currentRoute = ScreenRoute.DASHBOARD },
                onNavigateToSubscription = { currentRoute = ScreenRoute.SUBSCRIPTION }
              )
              ScreenRoute.PROGRESS -> ProgressScreen(
                user = user,
                cbtRepository = container.cbtRepository,
                onNavigateBack = { currentRoute = ScreenRoute.DASHBOARD },
                onPracticeCourse = { courseId ->
                  cbtViewModel.startPractice(courseId, user)
                  currentRoute = ScreenRoute.CBT_SESSION
                },
                onNavigateToResults = { attemptId ->
                  currentAttemptId = attemptId
                  currentRoute = ScreenRoute.RESULTS
                }
              )
              ScreenRoute.SUBSCRIPTION -> SubscriptionScreen(
                user = user,
                subscriptionViewModel = subscriptionViewModel,
                onNavigateBack = { currentRoute = ScreenRoute.DASHBOARD }
              )
              else -> {}
            }
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAuthenticatedScaffold(
  user: UserEntity,
  currentRoute: ScreenRoute,
  onNavigate: (ScreenRoute) -> Unit,
  onLogout: () -> Unit,
  content: @Composable () -> Unit
) {
  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val isTabletOrDesktop = maxWidth >= 600.dp

    if (isTabletOrDesktop) {
      // Desktop / Tablet layout with Left Sidebar (NavigationRail / Drawer)
      Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
          modifier = Modifier.width(80.dp),
          containerColor = AcademicNavy,
          contentColor = Color.White,
          header = {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(vertical = 12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Image(
                  painter = painterResource(id = R.drawable.medrad_logo),
                  contentDescription = "Logo",
                  modifier = Modifier.size(36.dp).clip(CircleShape),
                  contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
              }
            }
          }
        ) {
          NavigationRailItem(
            selected = currentRoute == ScreenRoute.DASHBOARD,
            onClick = { onNavigate(ScreenRoute.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Home", fontSize = 10.sp) },
            colors = railItemColors()
          )
          NavigationRailItem(
            selected = currentRoute == ScreenRoute.COURSES,
            onClick = { onNavigate(ScreenRoute.COURSES) },
            icon = { Icon(Icons.Default.School, contentDescription = "Courses") },
            label = { Text("Courses", fontSize = 10.sp) },
            colors = railItemColors()
          )
          NavigationRailItem(
            selected = currentRoute == ScreenRoute.MATERIALS,
            onClick = { onNavigate(ScreenRoute.MATERIALS) },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Materials") },
            label = { Text("Notes", fontSize = 10.sp) },
            colors = railItemColors()
          )
          NavigationRailItem(
            selected = currentRoute == ScreenRoute.PROGRESS,
            onClick = { onNavigate(ScreenRoute.PROGRESS) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
            label = { Text("Progress", fontSize = 10.sp) },
            colors = railItemColors()
          )
          NavigationRailItem(
            selected = currentRoute == ScreenRoute.SUBSCRIPTION,
            onClick = { onNavigate(ScreenRoute.SUBSCRIPTION) },
            icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "Premium") },
            label = { Text("Premium", fontSize = 10.sp) },
            colors = railItemColors()
          )
          if (user.role == "ADMIN") {
            NavigationRailItem(
              selected = currentRoute == ScreenRoute.ADMIN,
              onClick = { onNavigate(ScreenRoute.ADMIN) },
              icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
              label = { Text("Admin", fontSize = 10.sp) },
              colors = railItemColors()
            )
          }

          Spacer(modifier = Modifier.weight(1f))

          IconButton(
            onClick = onLogout,
            modifier = Modifier.padding(bottom = 16.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color(0xFFEF4444))
          }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
          content()
        }
      }
    } else {
      // Mobile Layout: Top Bar + Content + Bottom Navigation
      Scaffold(
        topBar = {
          TopAppBar(
            title = {
              Column {
                Text(
                  text = "MedRad Study app",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
                Text(
                  text = "${user.fullName} (${user.role})",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFFCBD5E1)
                )
              }
            },
            actions = {
              if (user.role == "ADMIN") {
                IconButton(
                  onClick = { onNavigate(ScreenRoute.ADMIN) },
                  modifier = Modifier.testTag("admin_portal_icon_button")
                ) {
                  Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Portal", tint = WarningAmber)
                }
              }
              IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
              }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AcademicNavy)
          )
        },
        bottomBar = {
          NavigationBar(
            containerColor = Color.White,
            tonalElevation = 8.dp
          ) {
            NavigationBarItem(
              selected = currentRoute == ScreenRoute.DASHBOARD,
              onClick = { onNavigate(ScreenRoute.DASHBOARD) },
              icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
              label = { Text("Dashboard") },
              colors = navItemColors()
            )
            NavigationBarItem(
              selected = currentRoute == ScreenRoute.COURSES,
              onClick = { onNavigate(ScreenRoute.COURSES) },
              icon = { Icon(Icons.Default.School, contentDescription = "Courses") },
              label = { Text("Courses") },
              colors = navItemColors()
            )
            NavigationBarItem(
              selected = currentRoute == ScreenRoute.MATERIALS,
              onClick = { onNavigate(ScreenRoute.MATERIALS) },
              icon = { Icon(Icons.Default.MenuBook, contentDescription = "Study Notes") },
              label = { Text("Notes") },
              colors = navItemColors()
            )
            NavigationBarItem(
              selected = currentRoute == ScreenRoute.PROGRESS,
              onClick = { onNavigate(ScreenRoute.PROGRESS) },
              icon = { Icon(Icons.Default.BarChart, contentDescription = "Performance") },
              label = { Text("Progress") },
              colors = navItemColors()
            )
            NavigationBarItem(
              selected = currentRoute == ScreenRoute.SUBSCRIPTION,
              onClick = { onNavigate(ScreenRoute.SUBSCRIPTION) },
              icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "Subscription") },
              label = { Text("Premium") },
              colors = navItemColors()
            )
          }
        }
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          content()
        }
      }
    }
  }
}

@Composable
fun navItemColors() = NavigationBarItemDefaults.colors(
  selectedIconColor = AcademicNavy,
  selectedTextColor = AcademicNavy,
  unselectedIconColor = Color(0xFF64748B),
  unselectedTextColor = Color(0xFF64748B),
  indicatorColor = Color(0xFFE0EBF7)
)

@Composable
fun railItemColors() = NavigationRailItemDefaults.colors(
  selectedIconColor = Color.White,
  selectedTextColor = Color.White,
  unselectedIconColor = Color(0xFF94A3B8),
  unselectedTextColor = Color(0xFF94A3B8),
  indicatorColor = ProfessionalBlue
)
