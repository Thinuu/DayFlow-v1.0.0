package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.ui.theme.BorderPurple
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.DeepIndigoContainer
import com.example.ui.theme.ExpenseNegativeCoral
import com.example.ui.theme.RadiantLavender
import com.example.ui.theme.SurfaceSlatePurple
import com.example.ui.theme.SurfaceVariantMedium
import com.example.ui.theme.TextMuted

private const val MAX_ATTEMPTS = 5
private const val LOCKOUT_DURATION_MS = 30_000L // 30 seconds

/**
 * Full-screen PIN lock screen. Enforces attempt throttling:
 * - After [MAX_ATTEMPTS] consecutive wrong entries, input is blocked for
 *   [LOCKOUT_DURATION_MS] milliseconds.
 * - The "Emergency Unlock" demo bypass has been removed; there is no
 *   mechanism to unlock the app without the correct PIN.
 *
 * @param correctPinHash  SHA-256 hash of the stored PIN (hex string).
 * @param onUnlocked      Called when the entered PIN's hash matches [correctPinHash].
 */
@Composable
fun PinLockScreen(
    correctPinHash: String,
    onUnlocked: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var failedAttempts by remember { mutableIntStateOf(0) }
    var lockedUntilMs by remember { mutableLongStateOf(0L) }

    fun isLockedOut(): Boolean = System.currentTimeMillis() < lockedUntilMs

    fun remainingLockSeconds(): Int =
        ((lockedUntilMs - System.currentTimeMillis()) / 1000L).toInt().coerceAtLeast(0)

    fun handleDigit(digit: String) {
        if (isLockedOut()) return
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            isError = false

            if (newPin.length == 4) {
                val enteredHash = hashPin(newPin)
                if (enteredHash == correctPinHash) {
                    failedAttempts = 0
                    onUnlocked()
                } else {
                    failedAttempts++
                    isError = true
                    enteredPin = ""
                    if (failedAttempts >= MAX_ATTEMPTS) {
                        lockedUntilMs = System.currentTimeMillis() + LOCKOUT_DURATION_MS
                        failedAttempts = 0
                    }
                }
            }
        }
    }

    fun handleBackspace() {
        if (isLockedOut()) return
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            isError = false
        }
    }

    val locked = isLockedOut()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoalBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DeepIndigoContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = RadiantLavender,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "DayFlow Protected",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (locked) {
                Text(
                    text = "Too many attempts. Try again in ${remainingLockSeconds()}s.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ExpenseNegativeCoral,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = if (isError) "Incorrect PIN — ${MAX_ATTEMPTS - failedAttempts} attempts left"
                           else "Enter 4-digit passcode to unlock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isError) ExpenseNegativeCoral else TextMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // PIN Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val isFilled = index < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    locked -> SurfaceVariantMedium
                                    isFilled -> RadiantLavender
                                    else -> SurfaceVariantMedium
                                }
                            )
                            .border(
                                1.5.dp,
                                if (isError) ExpenseNegativeCoral else BorderPurple,
                                CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Keypad
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "DEL")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                keypad.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            when (key) {
                                "" -> Spacer(modifier = Modifier.size(68.dp))
                                "DEL" -> Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(if (locked) SurfaceVariantMedium else SurfaceSlatePurple)
                                        .clickable(enabled = !locked) { handleBackspace() }
                                        .testTag("pin_key_del"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Backspace,
                                        contentDescription = "Delete",
                                        tint = if (locked) TextMuted else RadiantLavender,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                else -> Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(if (locked) SurfaceVariantMedium else SurfaceSlatePurple)
                                        .clickable(enabled = !locked) { handleDigit(key) }
                                        .testTag("pin_key_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (locked) TextMuted else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * SHA-256 hash of [pin]. Returns lowercase hex string.
 * Used both here (to verify) and in MainViewModel (to store).
 */
fun hashPin(pin: String): String {
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}
