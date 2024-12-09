package com.kpi.lab4

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt


class HpnemCalculator {

    private var inputsMap by mutableStateOf(mapOf<String, String>())

    private var calculationResult by mutableStateOf("Показники ще не обчислено")

    @Composable
    fun View() {
        InputsScreen(
            inputsMap = inputsMap,
            onValueChange = { key, value ->
                inputsMap = inputsMap.toMutableMap().apply { this[key] = value }
            },
            calculationResult = calculationResult,
            onCalculate = { calculate() }
        )
    }

    private fun calculate() {
        val r = inputsMap["resistance"]?.toDoubleOrNull() ?: .0
        val reactance = inputsMap["reactance"]?.toDoubleOrNull() ?: .0

        val xt = 11.1*115*115/100/6.3
        val x = reactance + xt
        val z = sqrt(r*r + x*x)
        val i3p = 115*1000/sqrt(3.0)/z
        val i2p = i3p*sqrt(3.0)/2
        val k = 11.0*11/115/115

        val r10kv = r*k
        val x10kv = x*k
        val z10kv = sqrt(r10kv*r10kv + x10kv*x10kv)
        val i3p10kv = 11*1000/sqrt(3.0)/z10kv
        val i2p10kv = i3p10kv*sqrt(3.0)/2

        calculationResult = """
            Трифазний струм КЗ на шинах 110 кВ: %.2f
            Двофазний струм КЗ на шинах 110 кВ: %.2f
            Трифазний струм КЗ на шинах 10 кВ: %.2f
            Двофазний струм КЗ на шинах 10 кВ: %.2f
        """.trimIndent().format(
            i3p,
            i2p,
            i3p10kv,
            i2p10kv
        )
    }

    @Composable
    private fun InputsScreen(
        inputsMap: Map<String, String>,
        onValueChange: (String, String) -> Unit,
        calculationResult: String,
        onCalculate: () -> Unit
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(
                label = "Активний опір",
                units = "Ом",
                value = inputsMap["resistance"] ?: "",
                onValueChange = { onValueChange("resistance", it) }
            )
            InputField(
                label = "Реактивний опір",
                units = "Ом",
                value = inputsMap["reactance"] ?: "",
                onValueChange = { onValueChange("reactance", it) }
            )

            Text(
                calculationResult,
                modifier = Modifier.padding(8.dp)
            )

            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .height(72.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = { onCalculate() }
            ) {
                Text(
                    "Calculate",
                    fontSize = 24.sp
                )
            }
        }
    }
}
