package com.respondingio.main

import com.respondingio.functions.utils.Time
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkTime () {

        val test = "TEST"
        assertEquals("Times must match.", Time.getCurrentUTC(), System.currentTimeMillis())

        val test2 = "TEST2"
        assertEquals("Times must match.", Time.getCurrentUTC(), System.currentTimeMillis() + 1)
    }
}
