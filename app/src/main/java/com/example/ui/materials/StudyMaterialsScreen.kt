package com.example.ui.materials

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.data.local.entity.StudyMaterialEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.AcademicRepository
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ProfessionalBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMaterialsScreen(
  user: UserEntity,
  academicRepository: AcademicRepository,
  onNavigateBack: () -> Unit,
  onNavigateToSubscription: () -> Unit
) {
  val materials by academicRepository.getAllMaterials().collectAsState(initial = emptyList())
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }
  var viewingMaterial by remember { mutableStateOf<StudyMaterialEntity?>(null) }
  var showLockedDialog by remember { mutableStateOf(false) }

  val categories = listOf("All", "Lecture Note", "Revision Summary", "Past Question")

  val filteredMaterials = materials.filter { mat ->
    val matchesCategory = (selectedCategory == "All" || mat.contentType.equals(selectedCategory, ignoreCase = true))
    val matchesSearch = mat.title.contains(searchQuery, ignoreCase = true) ||
        mat.description.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Study Materials",
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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      // Search field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search lecture notes, past questions...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("materials_search_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Filter chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { category ->
          FilterChip(
            selected = selectedCategory == category,
            onClick = { selectedCategory = category },
            label = { Text(category) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AcademicNavy,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // List of Materials
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Text(
            text = "${filteredMaterials.size} Materials Available for RAD 101",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        items(filteredMaterials) { material ->
          MaterialCard(
            material = material,
            isUserPremium = user.isPremium || user.role == "ADMIN",
            onClick = {
              if (material.isPremium && !user.isPremium && user.role != "ADMIN") {
                showLockedDialog = true
              } else {
                viewingMaterial = material
              }
            }
          )
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }

  // Material Reader Bottom Sheet
  if (viewingMaterial != null) {
    ModalBottomSheet(
      onDismissRequest = { viewingMaterial = null },
      sheetState = rememberModalBottomSheetState()
    ) {
      val mat = viewingMaterial!!
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (mat.isPremium) WarningAmber else SuccessGreen,
            contentColor = Color.White
          ) {
            Text(
              text = if (mat.isPremium) "PREMIUM ARCHIVE" else "FREE STUDY NOTE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }

          Text(
            text = mat.contentType,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = ProfessionalBlue
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = mat.title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = AcademicNavy
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = mat.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Document Body Container
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(16.dp)
        ) {
          Text(
            text = mat.contentBody,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = Color(0xFF1E293B)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = { viewingMaterial = null },
          modifier = Modifier.fillMaxWidth().height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Done Reading")
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }

  // Premium locked prompt
  if (showLockedDialog) {
    AlertDialog(
      onDismissRequest = { showLockedDialog = false },
      icon = {
        Icon(Icons.Default.Lock, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(36.dp))
      },
      title = {
        Text("Premium Study Material", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
      },
      text = {
        Text(
          "This lecture document and past question archive is exclusive to MEDRAD Study Squad Premium members. Upgrade to unlock full access across all devices.",
          style = MaterialTheme.typography.bodySmall
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showLockedDialog = false
            onNavigateToSubscription()
          },
          colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Upgrade Now (₦3,500)")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLockedDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun MaterialCard(
  material: StudyMaterialEntity,
  isUserPremium: Boolean,
  onClick: () -> Unit
) {
  val isLocked = material.isPremium && !isUserPremium

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .testTag("material_item_${material.id}"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, BorderSubtle)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(if (material.isPremium) Color(0xFFFEF3C7) else Color(0xFFE0EBF7)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.Description,
          contentDescription = null,
          tint = if (material.isPremium) WarningAmber else ProfessionalBlue,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = material.contentType,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = ProfessionalBlue
          )

          if (material.isPremium) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFFEF3C7)
            ) {
              Text(
                text = "PREMIUM",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                color = Color(0xFF92400E),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = material.title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
          color = AcademicNavy,
          maxLines = 1
        )

        Text(
          text = material.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2
        )
      }
    }
  }
}
