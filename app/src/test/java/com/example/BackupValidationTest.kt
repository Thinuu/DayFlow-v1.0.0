package com.example

import com.example.data.service.BackupValidationResult
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BackupValidationTest {

    @Test
    fun testValidBackupJsonStructure() {
        val root = JSONObject().apply {
            put("app", "DayFlow")
            put("schemaVersion", 1)
            put("exportedAt", "2026-08-26T12:00:00Z")
            put("accounts", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 1L)
                    put("name", "Checking Account")
                    put("type", "CHECKING")
                    put("initialBalance", 1500.0)
                    put("colorHex", 0xFF8B5CF6)
                    put("iconKey", "account_balance")
                    put("isDefault", true)
                    put("accountNumberMask", "•••• 4242")
                    put("isArchived", false)
                })
            })
            put("transactions", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 1L)
                    put("title", "Organic Market")
                    put("amount", 45.50)
                    put("type", "EXPENSE")
                    put("category", "GROCERIES")
                    put("paymentMethod", "CARD")
                    put("accountId", 1L)
                    put("dateEpochDay", 20600L)
                    put("timestampMillis", 1787654321000L)
                    put("note", "Weekly groceries")
                    put("isRecurring", false)
                    put("recurringFrequency", "")
                })
            })
            put("routines", JSONArray())
            put("tasks", JSONArray())
            put("savingsGoals", JSONArray())
            put("bills", JSONArray())
        }

        val jsonStr = root.toString()
        val parsed = JSONObject(jsonStr)

        assertTrue(parsed.has("app"))
        assertEquals(1, parsed.getInt("schemaVersion"))
        assertEquals(1, parsed.getJSONArray("accounts").length())
        assertEquals(1, parsed.getJSONArray("transactions").length())
    }

    @Test
    fun testCorruptedJsonDetection() {
        val corrupted = "{ invalid_json: true, ... [}"
        var failed = false
        try {
            JSONObject(corrupted)
        } catch (e: Exception) {
            failed = true
        }
        assertTrue(failed)
    }

    @Test
    fun testMissingAccountsArrayHandledGracefully() {
        val root = JSONObject().apply {
            put("app", "DayFlow")
            put("schemaVersion", 1)
        }

        val accounts = root.optJSONArray("accounts") ?: JSONArray()
        assertEquals(0, accounts.length())
    }

    @Test
    fun testValidationResultModel() {
        val success = BackupValidationResult(
            isValid = true,
            schemaVersion = 1,
            accountsCount = 3,
            transactionsCount = 45,
            routinesCount = 5,
            tasksCount = 10,
            savingsCount = 2,
            billsCount = 4
        )

        assertTrue(success.isValid)
        assertEquals(3, success.accountsCount)
        assertEquals(45, success.transactionsCount)
        assertEquals(null, success.errorMessage)

        val failure = BackupValidationResult(
            isValid = false,
            errorMessage = "Invalid JSON file or missing backup payload"
        )
        assertFalse(failure.isValid)
        assertEquals("Invalid JSON file or missing backup payload", failure.errorMessage)
    }
}
