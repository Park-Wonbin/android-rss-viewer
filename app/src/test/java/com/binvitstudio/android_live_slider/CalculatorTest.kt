package com.binvitstudio.android_live_slider

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorTest {

    private var calculator: Calculator? = null

    @Before
    fun setUp() {
        calculator = Calculator()
    }

    @Test
    fun addTest() {
        val result = calculator!!.add(15, 10)
        assertEquals(25, result)
    }

    @Test
    fun subtractTest() {
        val result = calculator!!.subtract(15, 10)
        assertEquals(5, result)
    }
}