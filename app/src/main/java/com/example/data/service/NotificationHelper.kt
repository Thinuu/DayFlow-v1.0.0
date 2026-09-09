package com.example.data.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

/**
 * Central notification utility for DayFlow.
 *
 * All notification channels are created in [createNotificationChannels], which is
 * called once from [MainActivity.onCreate]. Channels are idempotent on re-creation.
 *
 * Notification delivery is guarded by [canPostNotifications] so no call site needs
 * to check API levels or permissions manually. On Android < 13 the permission check
 * is skipped (it is not required by the OS). On Android 13+ the user must have
 * granted POST_NOTIFICATIONS at runtime; if they deny it the app continues working
 * normally and no notification is shown.
 *
 * Channel IDs are stable — do NOT rename them after publishing, as users who have
 * customised channel settings would lose those preferences.
 */
object NotificationHelper {

    const val CHANNEL_HABITS_ID = "dayflow_habits_channel"
    const val CHANNEL_BILLS_ID = "dayflow_bills_channel"
    const val CHANNEL_BUDGET_ID = "dayflow_budget_channel"

    /**
     * Returns true if the app is allowed to post notifications on the current device.
     *
     * - Android < 13 (API 32-): POST_NOTIFICATIONS permission did not exist; always allowed.
     * - Android 13+ (API 33+): checks the runtime permission grant state.
     */
    fun canPostNotifications(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission not required on older Android versions
            true
        }
    }

    /**
     * Creates the three notification channels required by the app.
     *
     * Safe to call multiple times — the system is idempotent for existing channels.
     * Must be called before any notification is posted (called from MainActivity.onCreate).
     * On Android < 8.0 (API 26) this is a no-op.
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val habitChannel = NotificationChannel(
                CHANNEL_HABITS_ID,
                context.getString(R.string.notification_channel_habits_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_habits_desc)
            }

            val billChannel = NotificationChannel(
                CHANNEL_BILLS_ID,
                context.getString(R.string.notification_channel_bills_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_bills_desc)
            }

            val budgetChannel = NotificationChannel(
                CHANNEL_BUDGET_ID,
                context.getString(R.string.notification_channel_budget_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_budget_desc)
            }

            notificationManager.createNotificationChannel(habitChannel)
            notificationManager.createNotificationChannel(billChannel)
            notificationManager.createNotificationChannel(budgetChannel)
        }
    }

    /**
     * Shows a routine reminder notification.
     *
     * Silently no-ops if the user has not granted POST_NOTIFICATIONS (Android 13+)
     * or if notifications are disabled at the channel level.
     */
    fun showHabitReminder(context: Context, routineTitle: String, timeDesc: String) {
        if (!canPostNotifications(context)) return
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1001,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_HABITS_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Routine Time: $routineTitle")
                .setContentText("Scheduled for $timeDesc. Check off your routine to preserve your streak!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(1001, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission revoked between check and notify — safe to ignore
        }
    }

    /**
     * Shows a bill due-date reminder notification.
     *
     * Silently no-ops if the user has not granted POST_NOTIFICATIONS (Android 13+).
     */
    fun showBillReminder(context: Context, billTitle: String, amountStr: String, dueDay: Int) {
        if (!canPostNotifications(context)) return
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1002,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_BILLS_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Upcoming Bill: $billTitle ($amountStr)")
                .setContentText("Due on the ${dueDay}th of this month. Don't forget to pay and mark as paid.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(1002, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission revoked between check and notify — safe to ignore
        }
    }

    /**
     * Shows a budget threshold alert notification.
     *
     * Silently no-ops if the user has not granted POST_NOTIFICATIONS (Android 13+).
     *
     * @param percentUsed Value 0–100 representing the percentage of monthly budget consumed.
     */
    fun showBudgetAlert(context: Context, percentUsed: Int, currencySymbol: String, budgetLimit: Double) {
        if (!canPostNotifications(context)) return
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1003,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val title = if (percentUsed >= 100) "Monthly Budget Exceeded!" else "Budget Alert: $percentUsed% Used"
            val body = "You have used $percentUsed% of your $currencySymbol${budgetLimit.toInt()} monthly budget."

            val notification = NotificationCompat.Builder(context, CHANNEL_BUDGET_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(1003, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission revoked between check and notify — safe to ignore
        }
    }
}
