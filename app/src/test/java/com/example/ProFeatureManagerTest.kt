package com.example

import com.example.data.service.ProFeature
import com.example.data.service.ProFeatureManager
import org.junit.Assert.assertTrue
import org.junit.Test

class ProFeatureManagerTest {

    @Test
    fun allFeaturesAreAvailableWithoutLicenseGating() {
        ProFeature.entries.forEach { feature ->
            assertTrue(
                "Feature ${feature.name} must be available without a Pro flag",
                ProFeatureManager.canAccess(feature, isProUser = false)
            )
            assertTrue(
                "Feature ${feature.name} remains available when a Play Billing flag is true",
                ProFeatureManager.canAccess(feature, isProUser = true)
            )
        }
    }

    @Test
    fun accountSavingsAndRoutineLimitsAreNotEnforced() {
        assertTrue(ProFeatureManager.isWithinAccountLimit(currentAccountCount = 2, isProUser = false))
        assertTrue(ProFeatureManager.isWithinAccountLimit(currentAccountCount = 50, isProUser = false))
        assertTrue(ProFeatureManager.isWithinSavingsGoalLimit(currentGoalCount = 100, isProUser = false))
        assertTrue(ProFeatureManager.isWithinRoutineLimit(currentRoutineCount = 100, isProUser = false))
    }
}
