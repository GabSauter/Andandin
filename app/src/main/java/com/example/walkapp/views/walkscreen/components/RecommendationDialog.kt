package com.example.walkapp.views.walkscreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecommendationDialog(showRecommendation: Boolean, closeDialog: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedAgeRange by remember { mutableStateOf("Selecione a faixa etária") }

    val recommendationText = when (selectedAgeRange) {
        "Crianças e adolescentes (5–17 anos)" -> "Crianças e adolescentes devem fazer pelo menos uma média de 60 minutos por dia de atividade física de moderada a vigorosa intensidade, ao longo da semana, a maior parte dessa atividade física deve ser aeróbica. Atividades aeróbicas de moderada a vigorosa intensidade, assim como aquelas que fortalecem os músculos e ossos devem ser incorporadas em pelo menos 3 dias na semana."
        "Adultos (18–64 anos)" -> "Adultos devem realizar pelo menos 150 a 300 minutos de atividade física aeróbica de moderada intensidade; ou pelo menos 75 a 150 minutos de atividade física aeróbica de vigorosa intensidade; ou uma combinação equivalente de atividade física de moderada e vigorosa intensidade ao longo da semana para benefícios substanciais à saúde. Adultos devem realizar também atividades de fortalecimento muscular de moderada intensidade ou maior que envolvam os principais grupos musculares dois ou mais dias por semana, pois estes proporcionam benefícios adicionais à saúde."
        "Idosos (65 anos ou mais)" -> "Idosos devem realizar pelo menos 150 a 300 minutos de atividade física aeróbica de moderada intensidade; ou pelo menos 75 a 150 minutos de atividade física aeróbica de vigorosa intensidade; ou uma combinação equivalente de atividades físicas de moderada e vigorosa intensidade ao longo da semana para benefícios substanciais à saúde. Idosos devem também fazer atividades de fortalecimento muscular de moderada intensidade ou maior que envolvam os principais grupos musculares em dois ou mais dias da semana, pois estas proporcionam benefícios adicionais para a saúde. Como parte da atividade física semanal, idosos devem realizar atividades físicas multicomponentes que enfatizem o equilíbrio funcional e o treinamento de força com moderada intensidade ou maior, em 3 ou mais dias da semana, para aumentar a capacidade funcional e prevenir quedas."
        else -> "Selecione uma faixa etária para ver as recomendações."
    }

    if (showRecommendation) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text("Recomendações da OMS") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Faixa etária:")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true }
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    shape = OutlinedTextFieldDefaults.shape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = OutlinedTextFieldDefaults.shape
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                selectedAgeRange,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown Icon"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {Text("Crianças e adolescentes (5–17 anos)")},
                                onClick = {
                                selectedAgeRange = "Crianças e adolescentes (5–17 anos)"
                                expanded = false
                            })
                            DropdownMenuItem(
                                text = {Text("Adultos (18–64 anos)")},
                                onClick = {
                                selectedAgeRange = "Adultos (18–64 anos)"
                                expanded = false
                            })
                            DropdownMenuItem(
                                text = {Text("Idosos (65 anos ou mais)")},
                                onClick = {
                                selectedAgeRange = "Idosos (65 anos ou mais)"
                                expanded = false
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(recommendationText)
                }
            },
            confirmButton = {
                TextButton(onClick = closeDialog) {
                    Text("Fechar")
                }
            }
        )
    }
}
