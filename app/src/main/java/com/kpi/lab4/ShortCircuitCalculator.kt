package com.kpi.lab4

import RadioButtonsField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import java.lang.Double.parseDouble
import kotlin.math.max
import kotlin.math.sqrt


class ShortCircuitCalculator {

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
        val shortCircuitPower = inputsMap["shortCircuitPower"]?.toDoubleOrNull() ?: .0

        val u1 = 10.5
        val u2 = 6.3

        val xc = u1*u1/shortCircuitPower
        val xt = u1*u1*u1/100/u2

        val sumX = xc + xt
        val initialCurrent = u1 / sqrt(3.0) / sumX

        calculationResult = "Струм КЗ: %.2f".trimIndent().format(initialCurrent)
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
                label = "Потужність КЗ",
                units = "МВт",
                value = inputsMap["shortCircuitPower"] ?: "",
                onValueChange = { onValueChange("shortCircuitPower", it) }
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
