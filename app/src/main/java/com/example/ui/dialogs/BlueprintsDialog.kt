package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.BlueprintPack
import com.example.data.model.BlueprintRoutine
import com.example.data.model.RoutineCategory
import com.example.data.model.TimeOfDay
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ProGold
import com.example.ui.theme.ProGoldDark
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextHighEmphasis
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueprintsDialog(
    onDismiss: () -> Unit,
    onInstallBlueprint: (BlueprintPack) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sampleBlueprints = remember {
        listOf(
            BlueprintPack(
                id = "morning_power",
                title = "Millionaire Morning Routine",
                tag = "HIGH PRODUCTIVITY",
                description = "Evidence-backed high performance routine to dominate the first 2 hours of your day.",
                iconKey = "wb_sunny",
                colorHex = 0xFFFFB74D,
                routines = listOf(
                    BlueprintRoutine(
                        title = "Hydrate 500ml Water",
                        note = "Rehydrate cells with lemon & electrolytes",
                        category = RoutineCategory.WELLNESS,
                        timeMinutes = 5,
                        timeOfDay = TimeOfDay.MORNING,
                        iconKey = "water_drop",
                        colorHex = 0xFF80D8FF
                    ),
                    BlueprintRoutine(
                        title = "20-Min Morning Sunlight & Walk",
                        note = "Set circadian rhythm & dopamine baseline",
                        category = RoutineCategory.FITNESS,
                        timeMinutes = 20,
                        timeOfDay = TimeOfDay.MORNING,
                        iconKey = "wb_sunny",
                        colorHex = 0xFFFFB74D
                    ),
                    BlueprintRoutine(
                        title = "Cold Shower Protocol",
                        note = "Boost norepinephrine and mental grit",
                        category = RoutineCategory.WELLNESS,
                        timeMinutes = 5,
                        timeOfDay = TimeOfDay.MORNING,
                        iconKey = "spa",
                        colorHex = 0xFF81C784
                    ),
                    BlueprintRoutine(
                        title = "Deep Focus Work Block (90m)",
                        note = "Zero distractions on #1 highest-leverage task",
                        category = RoutineCategory.PRODUCTIVITY,
                        timeMinutes = 90,
                        timeOfDay = TimeOfDay.MORNING,
                        iconKey = "laptop",
                        colorHex = 0xFFD0BCFF
                    )
                ),
                targetBudget = 2200.0
            ),
            BlueprintPack(
                id = "atomic_habits",
                title = "Atomic Habit Builder",
                tag = "SELF IMPROVEMENT",
                description = "James Clear-inspired 1% incremental daily micro-habits that compound over a lifetime.",
                iconKey = "menu_book",
                colorHex = 0xFF4DD0E1,
                routines = listOf(
                    BlueprintRoutine(
                        title = "Read 10 Pages of Non-Fiction",
                        note = "Continuous learning & compound knowledge",
                        category = RoutineCategory.LEARNING,
                        timeMinutes = 15,
                        timeOfDay = TimeOfDay.EVENING,
                        iconKey = "menu_book",
                        colorHex = 0xFF4DD0E1
                    ),
                    BlueprintRoutine(
                        title = "10-Minute Vipassana Mindfulness",
                        note = "Calm anxiety and reset attentional focus",
                        category = RoutineCategory.MINDFULNESS,
                        timeMinutes = 10,
                        timeOfDay = TimeOfDay.EVENING,
                        iconKey = "self_improvement",
                        colorHex = 0xFFB388FF
                    ),
                    BlueprintRoutine(
                        title = "Daily Gratitude Journal",
                        note = "Log 3 specific wins & thankfulness",
                        category = RoutineCategory.MINDFULNESS,
                        timeMinutes = 5,
                        timeOfDay = TimeOfDay.NIGHT,
                        iconKey = "edit_note",
                        colorHex = 0xFFFF80AB
                    )
                ),
                targetBudget = 1500.0
            ),
            BlueprintPack(
                id = "wealth_503020",
                title = "50/30/20 Wealth Blueprint",
                tag = "FINANCIAL FREEDOM",
                description = "Strict budgeting architecture: 50% Needs, 30% Wants, 20% Automated Investments & Debt Paydown.",
                iconKey = "workspace_premium",
                colorHex = 0xFF00E676,
                routines = listOf(
                    BlueprintRoutine(
                        title = "Weekly Expense & Receipt Audit",
                        note = "Review all card swipes against 50/30/20 target",
                        category = RoutineCategory.PRODUCTIVITY,
                        timeMinutes = 15,
                        timeOfDay = TimeOfDay.EVENING,
                        iconKey = "receipt_long",
                        colorHex = 0xFF00E676
                    ),
                    BlueprintRoutine(
                        title = "Check Investment Allocation",
                        note = "Confirm DCA index fund contributions",
                        category = RoutineCategory.PRODUCTIVITY,
                        timeMinutes = 10,
                        timeOfDay = TimeOfDay.MORNING,
                        iconKey = "trending_up",
                        colorHex = 0xFF69F0AE
                    )
                ),
                targetBudget = 1800.0
            ),
            BlueprintPack(
                id = "fitness_shred",
                title = "Iron Discipline & Fitness",
                tag = "HEALTH & BODY",
                description = "Daily physical training protocol with active recovery, hydration targets, and sleep discipline.",
                iconKey = "fitness_center",
                colorHex = 0xFFFF5252,
                routines = listOf(
                    BlueprintRoutine(
                        title = "45-Minute Strength / HIIT Session",
                        note = "Progressive overload or heart-rate cardio",
                        category = RoutineCategory.FITNESS,
                        timeMinutes = 45,
                        timeOfDay = TimeOfDay.MORNING,
                        iconKey = "fitness_center",
                        colorHex = 0xFFFF5252
                    ),
                    BlueprintRoutine(
                        title = "Mobility & Foam Rolling Stretch",
                        note = "Joint health and fascia recovery",
                        category = RoutineCategory.WELLNESS,
                        timeMinutes = 15,
                        timeOfDay = TimeOfDay.NIGHT,
                        iconKey = "spa",
                        colorHex = 0xFFFF7043
                    ),
                    BlueprintRoutine(
                        title = "No Blue Light 60m Pre-Bed",
                        note = "Melatonin optimization & deep sleep",
                        category = RoutineCategory.WELLNESS,
                        timeMinutes = 60,
                        timeOfDay = TimeOfDay.NIGHT,
                        iconKey = "nightlight",
                        colorHex = 0xFF7E57C2
                    )
                ),
                targetBudget = 1600.0
            )
        )
    }

    var installedPackId by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1C1B1F),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(BorderPurple)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "1-Click Habit Systems",
                        style = MaterialTheme.typography.titleMedium,
                        color = RadiantLavender,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Starter Blueprints",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.clip(CircleShape).background(SurfaceVariantMedium)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Install scientifically proven daily routines and budgeting limits into your calendar instantly with a single tap.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            sampleBlueprints.forEach { pack ->
                val isInstalled = installedPackId == pack.id
                BlueprintCard(
                    pack = pack,
                    isInstalled = isInstalled,
                    onInstall = {
                        installedPackId = pack.id
                        onInstallBlueprint(pack)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun BlueprintCard(
    pack: BlueprintPack,
    isInstalled: Boolean,
    onInstall: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSlatePurple),
        border = BorderStroke(1.dp, BorderPurple.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("blueprint_card_${pack.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DeepIndigoContainer,
                    border = BorderStroke(1.dp, BorderPurple)
                ) {
                    Text(
                        text = pack.tag,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = RadiantLavender,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${pack.routines.size} Routines",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ProGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pack.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pack.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Routine preview chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                pack.routines.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceVariantMedium.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(r.colorHex))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = r.title,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = TextHighEmphasis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${r.timeMinutes}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onInstall()
                },
                enabled = !isInstalled,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInstalled) EmeraldGreen else RadiantLavender,
                    disabledContainerColor = EmeraldGreen.copy(alpha = 0.8f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("install_blueprint_${pack.id}")
            ) {
                if (isInstalled) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pack Installed to Habits", color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Filled.Download, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Install Blueprint Pack", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
