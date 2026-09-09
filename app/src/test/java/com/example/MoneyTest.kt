package com.example

import com.example.data.model.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyTest {

    @Test
    fun testAddition() {
        val m1 = Money.fromCents(1500) // $15.00
        val m2 = Money.fromCents(2550) // $25.50
        val result = m1 + m2
        assertEquals(4050L, result.cents)
        assertEquals(40.50, result.toDouble(), 0.001)
    }

    @Test
    fun testSubtraction() {
        val m1 = Money.fromCents(5000) // $50.00
        val m2 = Money.fromCents(1234) // $12.34
        val result = m1 - m2
        assertEquals(3766L, result.cents)
        assertEquals(37.66, result.toDouble(), 0.001)
    }

    @Test
    fun testMultiplication() {
        val base = Money.fromCents(1999) // $19.99
        val times3 = base * 3L
        assertEquals(5997L, times3.cents)

        val rate = base * 1.5
        assertEquals(2998L, rate.cents)
    }

    @Test
    fun testDivision() {
        val total = Money.fromCents(10000) // $100.00
        val split = total / 4L
        assertEquals(2500L, split.cents)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDivisionByZero() {
        val total = Money.fromCents(5000)
        total / 0L
    }

    @Test
    fun testZeroAndNegativeValues() {
        val zero = Money.ZERO
        assertEquals(0L, zero.cents)
        assertEquals(0.0, zero.toDouble(), 0.0)

        val negative = Money.fromCents(-1500)
        assertEquals(-15.00, negative.toDouble(), 0.001)
        assertEquals("-$15.00", negative.format("$"))
    }

    @Test
    fun testLargeAmounts() {
        val large = Money.fromDouble(5_000_000.75)
        assertEquals(500000075L, large.cents)
        assertEquals(5000000.75, large.toDouble(), 0.001)
    }

    @Test
    fun testFromStringConversion() {
        val m1 = Money.fromString("$1,234.56")
        assertEquals(123456L, m1.cents)

        val m2 = Money.fromString("€99.90")
        assertEquals(9990L, m2.cents)

        val invalid = Money.fromString("invalid_abc")
        assertEquals(0L, invalid.cents)
    }

    @Test
    fun testFormatting() {
        val positive = Money.fromCents(2500)
        assertEquals("$25.00", positive.format("$", showSign = false))
        assertEquals("+$25.00", positive.format("$", showSign = true))
        assertEquals("€25.00", positive.format("€"))

        val zero = Money.ZERO
        assertEquals("$0.00", zero.format("$"))
    }

    @Test
    fun testValidation() {
        val valid = Money.validateAmount(50.0)
        assertTrue(valid.isSuccess)

        val negative = Money.validateAmount(-10.0)
        assertFalse(negative.isSuccess)

        val zero = Money.validateAmount(0.0)
        assertFalse(zero.isSuccess)

        val excessive = Money.validateAmount(500_000_000.0)
        assertFalse(excessive.isSuccess)
    }

    @Test
    fun testComparison() {
        val m10 = Money.fromCents(1000)
        val m20 = Money.fromCents(2000)
        assertTrue(m10 < m20)
        assertTrue(m20 > m10)
        assertEquals(0, m10.compareTo(Money.fromCents(1000)))
    }
}
