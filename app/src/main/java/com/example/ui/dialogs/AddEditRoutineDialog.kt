package com.example.ui.dialogs

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.RoutineCategory
import com.example.data.model.RoutineEntity
import com.example.data.model.TimeOfDay
import com.example.ui.components.formatTimeMinutes
import com.example.ui.components.getIconForCategory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditRoutineDialog(
    routine: RoutineEntity?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        note: String,
        category: RoutineCategory,
        timeMinutes: Int,
        targetDaysOfWeekMask: Int,
        timeOfDay: TimeOfDay,
        iconKey: String,
        colorHex: Long
    ) -> Unit,
    onDelete: (RoutineEntity) -> Unit
) {
    val isEdit = routine != null
    var title by remember { mutableStateOf(routine?.title ?: "") }
    var note by remember { mutableStateOf(routine?.note ?: "") }
    var selectedCategory by remember {
        mutableStateOf(
            runCatching { RoutineCategory.valueOf(routine?.category ?: "") }.getOrDefault(RoutineCategory.WELLNESS)
        )
    }
    var selectedTimeOfDay by remember {
        mutableStateOf(
            runCatching { TimeOfDay.valueOf(routine?.timeOfDay ?: "") }.getOrDefault(TimeOfDay.MORNING)
        )
    }
    var timeMinutes by remember { mutableIntStateOf(routine?.timeMinutes ?: (8 * 60)) }
    var daysMask by remember { mutableIntStateOf(routine?.targetDaysOfWeekMask ?: 127) }

    val routineColors = remember {
        listOf(
            0xFF4F46E5, // Indigo
            0xFF10B981, // Emerald
            0xFFF59E0B, // Amber
            0xFFEC4899, // Pink
            0xFF8B5CF6, // Purple
            0xFF0284C7, // Blue
            0xFFEF4444  // Red
        )
    }
    var selectedColor by remember { mutableStateOf(routine?.colorHex ?: routineColors[0]) }

    val daysLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEdit) "Edit Routine" else "New Routine",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                if (isEdit) {
                    IconButton(
                        onClick = { onDelete(routine) },
                        modifier = Modifier.testTag("delete_routine_button")
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Routine",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Name *") },
                    placeholder = { Text("e.g. Morning 10-Min Meditation") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("routine_title_input")
                )

                // Note Field
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Details / Note (Optional)") },
                    placeholder = { Text("e.g. In quiet room with timer") },
                    maxLines = 2,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selection
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoutineCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName) },
                            leadingIcon = {
                                Icon(
                                    getIconForCategory(cat),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                // Time of Day
                Text(
                    text = "Time of Day",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeOfDay.entries.forEach { tod ->
                        val isSelected = selectedTimeOfDay == tod
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable {
                                    selectedTimeOfDay = tod
                                    // Adjust default time to match time of day
                                    timeMinutes = when (tod) {
                                        TimeOfDay.MORNING -> 8 * 60
                                        TimeOfDay.AFTERNOON -> 13 * 60
                                        TimeOfDay.EVENING -> 18 * 60
                                        TimeOfDay.NIGHT -> 21 * 60 + 30
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tod.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Time Picker (Quick slider)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Time",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = formatTimeMinutes(timeMinutes),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = timeMinutes.toFloat(),
                        onValueChange = { timeMinutes = (it / 15).toInt() * 15 },
                        valueRange = 0f..1425f, // 0 to 23:45 in 15 min steps
                        steps = (1425 / 15) - 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Days of the Week
                Text(
                    text = "Repeat on",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysLabels.forEachIndexed { index, label ->
                        val bit = 1 shl index
                        val isDaySelected = (daysMask and bit) != 0
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDaySelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable {
                                    daysMask = if (isDaySelected) daysMask and bit.inv() else daysMask or bit
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label.take(1),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDaySelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Theme Color Palette
                Text(
                    text = "Color Accent",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    routineColors.forEach { colorVal ->
                        val isSelected = selectedColor == colorVal
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .clickable { selectedColor = colorVal }
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            routine?.id ?: 0L,
                            title,
                            note,
                            selectedCategory,
                            timeMinutes,
                            if (daysMask == 0) 127 else daysMask,
                            selectedTimeOfDay,
                            selectedCategory.iconKey,
                            selectedColor
                        )
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_routine_button")
            ) {
                Text(if (isEdit) "Save Changes" else "Create Routine")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
