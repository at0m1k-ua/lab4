package com.kpi.lab4

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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


class CalculatorScreen3 {

    private var inputsMap by mutableStateOf(mapOf(
        "power" to "",
        "deviation" to ""
    ))

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
        val powerAmt = inputsMap["power"]?.toDoubleOrNull() ?: .0

        calculationResult =
            """
                %.2f watts
            """.trimIndent().format(powerAmt)
    }

    @Composable
    private fun InputsScreen(
        inputsMap: Map<String, String>,
        onValueChange: (String, String) -> Unit,
        calculationResult: String,
        onCalculate: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                InputField(
                    label = "Середньодобова потужність 3",
                    units = "МВт",
                    value = inputsMap["power"] ?: "",
                    onValueChange = { onValueChange("power", it) }
                )
                InputField(
                    label = "Середньоквадратичне відхилення",
                    units = "МВт",
                    value = inputsMap["deviation"] ?: "",
                    onValueChange = { onValueChange("deviation", it) }
                )
            }

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
