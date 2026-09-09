package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PricingPlan
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ProGold
import com.example.ui.theme.ProGoldDark
import com.example.ui.theme.ProVipIndigo
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextHighEmphasis
import com.example.ui.theme.TextMuted

@Composable
fun PaywallDialog(
    isProUser: Boolean,
    onDismiss: () -> Unit,
    onUnlockPro: (Boolean) -> Unit,
    onOpenLegal: (String) -> Unit
) {
    var selectedPlan by remember { mutableStateOf("annual") }
    var showSuccessBadge by remember { mutableStateOf(false) }

    val plans = listOf(
        PricingPlan(
            id = "lifetime",
            name = "Lifetime Access",
            price = "$49.99",
            subtext = "One-time payment • Pay once, own forever",
            badge = "BEST VALUE",
            isPopular = false
        ),
        PricingPlan(
            id = "annual",
            name = "Annual VIP Plan",
            price = "$29.99 / year",
            subtext = "Includes 3-Day Free Trial • Just $2.49/mo",
            badge = "MOST POPULAR",
            isPopular = true
        ),
        PricingPlan(
            id = "monthly",
            name = "Monthly Pass",
            price = "$4.99 / month",
            subtext = "Cancel anytime • Flexible monthly billing",
            badge = null,
            isPopular = false
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepCharcoalBg),
            color = DeepCharcoalBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Top Close & Restore Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantMedium)
                            .testTag("paywall_close_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }

                    TextButton(
                        onClick = {
                            onUnlockPro(true)
                            showSuccessBadge = true
                        },
                        modifier = Modifier.testTag("restore_purchases_button")
                    ) {
                        Text(
                            text = if (isProUser) "PRO ACTIVE" else "Restore Purchases",
                            color = if (isProUser) EmeraldGreen else RadiantLavender,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ProVipIndigo, DeepIndigoContainer, Color(0xFF1E1035))
                            )
                        )
                        .border(
                            BorderStroke(
                                1.5.dp,
                                Brush.horizontalGradient(listOf(ProGold, RadiantLavender, ProGoldDark))
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(ProGold, ProGoldDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WorkspacePremium,
                                contentDescription = "Pro Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "DAYFLOW",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ProGold
                            ) {
                                Text(
                                    text = "PRO",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Optional Google Play plans for your published app. Source features are not locked.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextHighEmphasis.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Highlights
                Text(
                    text = "Optional Play Billing products:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProFeatureRow(
                    icon = Icons.Filled.AutoAwesome,
                    title = "Unlimited Habits & Routines",
                    subtitle = "No limits on schedules, custom times, or categories"
                )
                ProFeatureRow(
                    icon = Icons.Filled.ReceiptLong,
                    title = "Recurring Subscription Tracker",
                    subtitle = "Manage Netflix, Gym, Rent & bills with renewal alerts"
                )
                ProFeatureRow(
                    icon = Icons.Filled.Shield,
                    title = "4-Digit Passcode & App Lock",
                    subtitle = "Bank-grade privacy protection for personal ledgers"
                )
                ProFeatureRow(
                    icon = Icons.Filled.Widgets,
                    title = "1-Click Blueprint Starter Packs",
                    subtitle = "Install Millionaire Morning & 50/30/20 Wealth templates"
                )
                ProFeatureRow(
                    icon = Icons.Filled.Diamond,
                    title = "Advanced Full Backup & Device Migration",
                    subtitle = "Instant JSON / CSV export and offline data portability"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Pricing Cards
                Text(
                    text = "Select Your Plan:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                plans.forEach { plan ->
                    val isSelected = selectedPlan == plan.id
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DeepIndigoContainer else SurfaceSlatePurple
                        ),
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) ProGold else BorderPurple.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { selectedPlan = plan.id }
                            .testTag("plan_card_${plan.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ProGold else Color.Transparent)
                                            .border(
                                                1.5.dp,
                                                if (isSelected) ProGold else TextMuted,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = plan.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isSelected) Color.White else TextHighEmphasis
                                    )
                                }

                                plan.badge?.let { bText ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (plan.isPopular) ProGold else RadiantLavender
                                    ) {
                                        Text(
                                            text = bText,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 34.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = plan.subtext,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = plan.price,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = if (isSelected) ProGold else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CTA Button
                Button(
                    onClick = {
                        onUnlockPro(true)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("unlock_pro_cta_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProGold
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPlan == "annual") "Continue with annual plan" else "Continue with selected plan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Legal & Policy footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = RadiantLavender,
                        modifier = Modifier.clickable { onOpenLegal("terms") }
                    )
                    Text("  •  ", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = RadiantLavender,
                        modifier = Modifier.clickable { onOpenLegal("privacy") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DeepIndigoContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = RadiantLavender, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}
