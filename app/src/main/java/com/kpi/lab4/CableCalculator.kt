package com.kpi.lab4

import RadioButtonsField
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
import kotlin.math.max
import kotlin.math.sqrt


class CableCalculator {

    private var inputsMap by mutableStateOf(mapOf<String, String>())

    private var calculationResult by mutableStateOf("Показники ще не обчислено")

    private val economyCurrentDensityForMaterials = mapOf(
        Pair("copper", "paper") to Triple(3.0, 2.5, 2.0),
        Pair("copper", "rubber") to Triple(3.5, 3.1, 2.7),
        Pair("copper", "pvc") to Triple(3.5, 3.1, 2.7),
        Pair("aluminum", "paper") to Triple(1.6, 1.4, 1.2),
        Pair("aluminum", "rubber") to Triple(1.9, 1.7, 1.6),
        Pair("aluminum", "pvc") to Triple(1.9, 1.7, 1.6),
    )

    private val thermalStabilityForMaterials = mapOf(
        "paper" to 92,
        "plastic" to 75,
        "rubber" to 65,
    )

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
        val shortCircuitCurrent = inputsMap["shortCircuitCurrent"]?.toDoubleOrNull() ?: .0
        val shortCircuitTime = inputsMap["shortCircuitTime"]?.toDoubleOrNull() ?: .0
        val expectedLoad = inputsMap["expectedLoad"]?.toDoubleOrNull() ?: .0
        val maxLoadTime = inputsMap["maxLoadTime"]?.toDoubleOrNull() ?: .0

        val isolationMaterial = inputsMap["isolationMaterial"]
        val wireMaterial = inputsMap["wireMaterial"]

        if (isolationMaterial == null) {
            calculationResult = "Оберіть матеріал ізоляції"
            return
        }

        if (wireMaterial == null) {
            calculationResult = "Оберіть матеріал провідника"
            return
        }

        val normalCurrent = expectedLoad / 2 / sqrt(3.0) / 10
        val economyCurrentDensityForMaxLoadTimes =
            economyCurrentDensityForMaterials[Pair(wireMaterial, isolationMaterial)]

        val economyCurrentDensity = if (maxLoadTime < 3000) {
            economyCurrentDensityForMaxLoadTimes!!.first
        } else if (maxLoadTime < 5000) {
            economyCurrentDensityForMaxLoadTimes!!.second
        } else {
            economyCurrentDensityForMaxLoadTimes!!.third
        }

        val economyCrossSection = normalCurrent / economyCurrentDensity
        val thermalStableCrossSection = shortCircuitCurrent * sqrt(shortCircuitTime) /
                thermalStabilityForMaterials[isolationMaterial]!!
        val crossSection = max(economyCrossSection, thermalStableCrossSection)

        calculationResult = "Поперечний переріз провідника: %.2f".trimIndent().format(crossSection)
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
                label = "Струм КЗ",
                units = "А",
                value = inputsMap["shortCircuitCurrent"] ?: "",
                onValueChange = { onValueChange("shortCircuitCurrent", it) }
            )
            InputField(
                label = "Фіктивний час вимикання струму КЗ",
                units = "C",
                value = inputsMap["shortCircuitTime"] ?: "",
                onValueChange = { onValueChange("shortCircuitTime", it) }
            )
            InputField(
                label = "Розрахункове навантаження",
                units = "кВт",
                value = inputsMap["expectedLoad"] ?: "",
                onValueChange = { onValueChange("expectedLoad", it) }
            )
            InputField(
                label = "Час максимального навантаження",
                units = "год",
                value = inputsMap["maxLoadTime"] ?: "",
                onValueChange = { onValueChange("maxLoadTime", it) }
            )
            RadioButtonsField(
                label = "Матеріал ізоляції",
                options = listOf("pvc", "rubber", "paper"),
                optionNames = listOf("ПВХ", "Резина", "Папір"),
                value = inputsMap["isolationMaterial"] ?: "",
                onValueChange = { onValueChange("isolationMaterial", it) }
            )
            RadioButtonsField(
                label = "Матеріал провідника",
                options = listOf("copper", "aluminum"),
                optionNames = listOf("Мідь", "Алюміній"),
                value = inputsMap["wireMaterial"] ?: "",
                onValueChange = { onValueChange("wireMaterial", it) }
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
