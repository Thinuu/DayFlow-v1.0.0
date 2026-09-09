package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoutineCategory
import com.example.data.model.RoutineEntity
import com.example.data.model.RoutineWithStatus
import com.example.data.model.TimeOfDay
import com.example.ui.components.DateSelectorStrip
import com.example.ui.components.RoutineItemCard
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextHighEmphasis
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.DayFlowUiState
import java.time.LocalDate

fun getTimeOfDayIcon(timeOfDay: TimeOfDay): ImageVector {
    return when (timeOfDay) {
        TimeOfDay.MORNING -> Icons.Filled.WbSunny
        TimeOfDay.AFTERNOON -> Icons.Filled.WbTwilight
        TimeOfDay.EVENING -> Icons.Filled.Nightlight
        TimeOfDay.NIGHT -> Icons.Filled.Bedtime
    }
}

@Composable
fun RoutinesScreen(
    uiState: DayFlowUiState,
    onSelectDate: (LocalDate) -> Unit,
    onToggleRoutine: (routineId: Long, isDone: Boolean) -> Unit,
    onMarkAllRoutines: (Boolean) -> Unit,
    onEditRoutine: (RoutineEntity) -> Unit,
    onDeleteRoutine: (RoutineEntity) -> Unit,
    onAddRoutine: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTimeOfDayFilter by remember { mutableStateOf<TimeOfDay?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<RoutineCategory?>(null) }

    val filteredRoutines = remember(uiState.routinesForDate, selectedTimeOfDayFilter, selectedCategoryFilter, searchQuery) {
        uiState.routinesForDate.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.routine.title.contains(searchQuery, ignoreCase = true) ||
                    item.routine.note.contains(searchQuery, ignoreCase = true)
            val matchesTime = selectedTimeOfDayFilter == null || item.routine.timeOfDay == selectedTimeOfDayFilter?.name
            val matchesCategory = selectedCategoryFilter == null || item.routine.category == selectedCategoryFilter?.name
            matchesSearch && matchesTime && matchesCategory
        }
    }

    val groupedRoutines = remember(filteredRoutines) {
        TimeOfDay.entries.associateWith { tod ->
            filteredRoutines.filter { it.routine.timeOfDay == tod.name }
        }.filterValues { it.isNotEmpty() }
    }

    val completedCount = uiState.routinesForDate.count { it.isCompletedToday }
    val totalCount = uiState.routinesForDate.size
    val allDone = totalCount > 0 && completedCount == totalCount

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Title Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Habits & Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            color = RadiantLavender,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Daily Routines",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DeepIndigoContainer,
                        border = BorderStroke(1.dp, BorderPurple)
                    ) {
                        Text(
                            text = "$completedCount/$totalCount Done",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (allDone) EmeraldGreen else RadiantLavender,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Date Selector
            item {
                DateSelectorStrip(
                    selectedDate = uiState.selectedDate,
                    onSelectDate = onSelectDate
                )
            }

            // Search Bar & Bulk Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search routines...", color = TextMuted, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = RadiantLavender, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceSlatePurple,
                            unfocusedContainerColor = SurfaceSlatePurple,
                            focusedBorderColor = RadiantLavender,
                            unfocusedBorderColor = BorderPurple.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(52.dp).testTag("routine_search_input")
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceSlatePurple,
                        border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .height(52.dp)
                            .clickable { onMarkAllRoutines(!allDone) }
                            .testTag("routine_toggle_all_button")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (allDone) Icons.Filled.RestartAlt else Icons.Filled.DoneAll,
                                contentDescription = if (allDone) "Reset All" else "Mark All Done",
                                tint = if (allDone) RadiantLavender else EmeraldGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Filter Chips (Time of Day)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedTimeOfDayFilter == null,
                            onClick = { selectedTimeOfDayFilter = null },
                            label = { Text("All Times", fontWeight = if (selectedTimeOfDayFilter == null) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceSlatePurple,
                                labelColor = TextMuted,
                                selectedContainerColor = DeepIndigoContainer,
                                selectedLabelColor = RadiantLavender
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedTimeOfDayFilter == null,
                                borderColor = BorderPurple.copy(alpha = 0.5f),
                                selectedBorderColor = RadiantLavender
                            ),
                            modifier = Modifier.testTag("filter_all_times")
                        )
                    }
                    items(TimeOfDay.entries) { tod ->
                        val isSelected = selectedTimeOfDayFilter == tod
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTimeOfDayFilter = if (selectedTimeOfDayFilter == tod) null else tod
                            },
                            label = { Text(tod.displayName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                Icon(
                                    getTimeOfDayIcon(tod),
                                    contentDescription = null,
                                    tint = if (isSelected) RadiantLavender else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceSlatePurple,
                                labelColor = TextMuted,
                                selectedContainerColor = DeepIndigoContainer,
                                selectedLabelColor = RadiantLavender
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BorderPurple.copy(alpha = 0.5f),
                                selectedBorderColor = RadiantLavender
                            ),
                            modifier = Modifier.testTag("filter_${tod.name.lowercase()}")
                        )
                    }
                }
            }

            // Routines grouped by Time of Day
            if (filteredRoutines.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
                        border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No routines matching '$searchQuery'" else "No routines for this filter/date",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Build sustainable habits with scheduled daily check-ins.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = onAddRoutine,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderPurple),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantLavender)
                            ) {
                                Text("+ Create New Routine")
                            }
                        }
                    }
                }
            } else {
                groupedRoutines.forEach { (timeOfDay, routinesList) ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    getTimeOfDayIcon(timeOfDay),
                                    contentDescription = null,
                                    tint = RadiantLavender,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = timeOfDay.displayName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            val doneInGroup = routinesList.count { it.isCompletedToday }
                            Text(
                                text = "$doneInGroup/${routinesList.size} done",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    items(routinesList) { item ->
                        RoutineItemCard(
                            routineWithStatus = item,
                            onToggle = { onToggleRoutine(item.routine.id, item.isCompletedToday) },
                            onEdit = { onEditRoutine(item.routine) },
                            onDelete = { onDeleteRoutine(item.routine) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddRoutine,
            containerColor = RadiantLavender,
            contentColor = DeepIndigoContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("fab_add_routine")
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Routine")
        }
    }
}
