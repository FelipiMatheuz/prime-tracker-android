package com.felipimatheuz.primehunt

import com.felipimatheuz.primehunt.model.external.PrimeRelicApi
import com.felipimatheuz.primehunt.model.external.PrimeSetApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testConnectionSet() {
        PrimeSetApi().getSetData()
    }

    @Test
    fun testConnectionRelic() {
        PrimeRelicApi().getRelicData()
    }
}