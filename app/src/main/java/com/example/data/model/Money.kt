package com.example.data.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Safe monetary value object representing currency in minor units (integer cents).
 * Eliminates IEEE 754 floating-point rounding inaccuracies in financial calculations.
 */
data class Money(val cents: Long = 0L) : Comparable<Money> {

    operator fun plus(other: Money): Money = Money(Math.addExact(cents, other.cents))

    operator fun minus(other: Money): Money = Money(Math.subtractExact(cents, other.cents))

    operator fun times(multiplier: Long): Money = Money(Math.multiplyExact(cents, multiplier))

    operator fun times(multiplier: Double): Money {
        val bd = BigDecimal.valueOf(cents).multiply(BigDecimal.valueOf(multiplier))
        return Money(bd.setScale(0, RoundingMode.HALF_EVEN).toLong())
    }

    operator fun div(divisor: Long): Money {
        require(divisor != 0L) { "Cannot divide money by zero" }
        return Money(cents / divisor)
    }

    fun toDouble(): Double = cents / 100.0

    fun toBigDecimal(): BigDecimal = BigDecimal.valueOf(cents).divide(BigDecimal(100), 2, RoundingMode.HALF_EVEN)

    fun format(currencySymbol: String = "$", showSign: Boolean = false): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,##0.00", symbols)
        val absValue = kotlin.math.abs(cents) / 100.0
        val formattedNumber = formatter.format(absValue)
        return when {
            cents < 0 -> "-$currencySymbol$formattedNumber"
            cents > 0 && showSign -> "+$currencySymbol$formattedNumber"
            else -> "$currencySymbol$formattedNumber"
        }
    }

    override fun compareTo(other: Money): Int = cents.compareTo(other.cents)

    companion object {
        val ZERO = Money(0L)

        fun fromCents(cents: Long): Money = Money(cents)

        fun fromDouble(amount: Double): Money {
            if (amount.isNaN() || amount.isInfinite()) return ZERO
            val bd = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN)
            val cents = bd.multiply(BigDecimal(100)).toLong()
            return Money(cents)
        }

        fun fromString(amountStr: String): Money {
            val cleaned = amountStr.trim().replace(",", "").replace("$", "").replace("€", "").replace("£", "")
            return try {
                val bd = BigDecimal(cleaned).setScale(2, RoundingMode.HALF_EVEN)
                val cents = bd.multiply(BigDecimal(100)).toLong()
                Money(cents)
            } catch (e: Exception) {
                ZERO
            }
        }

        fun validateAmount(amount: Double): Result<Double> {
            return when {
                amount.isNaN() || amount.isInfinite() -> Result.failure(IllegalArgumentException("Amount must be a valid number"))
                amount <= 0.0 -> Result.failure(IllegalArgumentException("Amount must be greater than zero"))
                amount > 100_000_000.0 -> Result.failure(IllegalArgumentException("Amount exceeds maximum permitted limit"))
                else -> Result.success(amount)
            }
        }
    }
}
