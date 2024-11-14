package com.example.walkapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walkapp.R

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit
) {
    val screens = listOf(
        ScreenData(
            imageRes = R.drawable.welcome_illustration,
            title = "Bem vindo ao Andandin!",
            subtitle = "Andandin é um app de caminhada com elementos de jogos"
        ),
        ScreenData(
            imageRes = R.drawable.walking_illustration,
            title = "Caminhe",
            subtitle = "Caminhe para ganhar pontos, quanto mais metros você percorrer mais pontos irá ganhar"
        ),
        ScreenData(
            imageRes = R.drawable.medalha_ouro_km_dia,
            title = "Ganhe medalhas",
            subtitle = "Ganhe medalhas ao completar desafios"
        ),
        ScreenData(
            imageRes = R.drawable.compete_illustration,
            title = "Compita com seus amigos",
            subtitle = "Compita contra o mundo e seus amigos, para ganhar mais pontos"
        ),
        ScreenData(
            imageRes = R.drawable.cooperate_illustration,
            title = "Coopere",
            subtitle = "Faça parte de um grupo para ganhar mais pontos, se uma pessoa do grupo caminhar as outras receberão 10% dos pontos ganhos"
        ),
        ScreenData(
            imageRes = R.drawable.narrative_illustration,
            title = "Libere histórias",
            subtitle = "Quanto mais pontos, maior seu nível, e é por meio do nível que você liberará histórias"
        ),
        ScreenData(
            imageRes = R.drawable.performance_illustration,
            title = "Acompanhe suas estatísticas",
            subtitle = "É possível acompanhar suas estatísticas por meio de gráficos"
        )
    )
    var currentIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val currentScreen = screens[currentIndex]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = currentScreen.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = currentScreen.title,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = currentScreen.subtitle,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LinearProgressIndicator(
            progress = { (currentIndex + 1) / screens.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onContinue) {
                Text(text = "Ignorar", color = MaterialTheme.colorScheme.secondary)
            }
            Button(
                onClick = {
                    if (currentIndex < screens.lastIndex) {
                        currentIndex++
                    } else {
                        onContinue()
                    }
                }
            ) {
                Text(text = "Próximo")
            }
        }
    }
}

data class ScreenData(
    val imageRes: Int,
    val title: String,
    val subtitle: String
)