package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextHighEmphasis
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDialog(
    type: String, // "privacy" or "terms"
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isPrivacy = type == "privacy"

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isPrivacy) "Data & Security" else "Legal Agreement",
                        style = MaterialTheme.typography.titleMedium,
                        color = RadiantLavender,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isPrivacy) "Privacy Policy" else "Terms of Service",
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

            Spacer(modifier = Modifier.height(18.dp))

            if (isPrivacy) {
                Text(
                    text = "1. Zero Cloud Data Collection\n" +
                            "DayFlow operates under a strict offline-first architecture. All your daily habits, streaks, financial transactions, and budgets are stored securely on your local device via encrypted SQLite database. No personal financial logs are sent to third-party ad trackers or remote servers.\n\n" +
                            "2. Local Authentication & Security\n" +
                            "When you configure a 4-Digit Passcode or Biometric App Lock, authentication verification is performed strictly locally on your Android OS hardware security module.\n\n" +
                            "3. User Data Rights & Export\n" +
                            "You retain 100% ownership of your data. You can export complete CSV and JSON ledgers at any time or erase all records instantly from the settings menu.\n\n" +
                            "4. Optional In-App Subscriptions\n" +
                            "If the publisher enables Google Play Billing, purchases are processed through Google Play with standard store protection. Application features included with this source code are not locked behind an Envato license.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHighEmphasis.copy(alpha = 0.9f),
                    lineHeight = 22.sp
                )
            } else {
                Text(
                    text = "1. Acceptance of Terms\n" +
                            "By downloading or using DayFlow, you agree to these Terms. DayFlow is provided as a productivity and personal finance tracking companion.\n\n" +
                            "2. Financial Disclaimer\n" +
                            "DayFlow is an organization and budget planning tool and does not provide certified financial, legal, or investment advisory services. Budget insights and habit scores are calculated based on user-entered ledger records.\n\n" +
                            "3. Optional Google Play Subscriptions\n" +
                            "If the publisher enables Play Billing, annual and monthly subscriptions renew automatically unless cancelled at least 24 hours before the end of the current billing cycle in Google Play subscription settings.\n\n" +
                            "4. License & Updates\n" +
                            "We reserve the right to deliver continuous feature improvements, UI enhancements, and security patches to all active users.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHighEmphasis.copy(alpha = 0.9f),
                    lineHeight = 22.sp
                )
            }
        }
    }
}
