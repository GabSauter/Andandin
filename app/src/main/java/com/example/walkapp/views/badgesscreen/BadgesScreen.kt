package com.example.walkapp.views.badgesscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.walkapp.R
import com.example.walkapp.viewmodels.BadgeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BadgesScreen(userId: String) {

    val badgeViewModel = koinViewModel<BadgeViewModel>()
    val badgesData by badgeViewModel.badges.collectAsState()
    val loading by badgeViewModel.loading.collectAsState()
    val error by badgeViewModel.error.collectAsState()

    LaunchedEffect(Unit){
        badgeViewModel.getBadges(userId)
    }

    if(badgesData == null && loading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if(error != null){
        Text("Houve um erro ao carregar as medalhas")
    } else{
        val badges = listOf(
            Badge(imageRes = R.drawable.medalha0, description = "Alcançado por caminhar uma distancia de 1 km", isUnlocked = badgesData?.badge1 ?: false),
            Badge(imageRes = R.drawable.medalha1, description = "Alcançado por caminhar uma distancia de 5 km", isUnlocked = badgesData?.badge2 ?: false),
            Badge(imageRes = R.drawable.medalha2, description = "Alcançado por caminhar uma distancia de 10 km", isUnlocked = badgesData?.badge3 ?: false),
            Badge(imageRes = R.drawable.medalha3, description = "Alcançado por caminhar uma distancia de 25 km", isUnlocked = badgesData?.badge4 ?: false),
            Badge(imageRes = R.drawable.medalha4, description = "Alcançado por caminhar uma distancia de 50 km", isUnlocked = badgesData?.badge5 ?: false),
            Badge(imageRes = R.drawable.medalha5, description = "Alcançado por caminhar uma distancia de 100 km", isUnlocked = badgesData?.badge6 ?: false),
            Badge(imageRes = R.drawable.medalha_bronze_km_dia, description = "Alcançado por caminhar uma distancia de 4 km no mesmo dia", isUnlocked = badgesData?.badge7 ?: false),
            Badge(imageRes = R.drawable.medalha_prata_km_dia, description = "Alcançado por caminhar uma distancia de 8 km no mesmo dia", isUnlocked = badgesData?.badge8 ?: false),
            Badge(imageRes = R.drawable.medalha_ouro_km_dia, description = "Alcançado por caminhar uma distancia de 16 km no mesmo dia", isUnlocked = badgesData?.badge9 ?: false),
        )

        var selectedBadge by remember { mutableStateOf<Badge?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(badges.size) { index ->
                    BadgeItem(badge = badges[index], onClick = { selectedBadge = it })
                }
            }
        }

        selectedBadge?.let { badge ->
            BadgeDialog(badge = badge, onDismiss = { selectedBadge = null })
        }
    }
}

@Composable
fun BadgeItem(badge: Badge, onClick: (Badge) -> Unit) {
    val painter = painterResource(id = badge.imageRes)
    val colorMatrix =
        if (badge.isUnlocked) ColorMatrix() else ColorMatrix().apply { setToSaturation(0f) }

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Image(
            painter = painter,
            contentDescription = badge.description,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(colorMatrix),
            modifier = Modifier
                .size(80.dp, 120.dp)
                .padding(8.dp)
                .clickable { onClick(badge) }
        )
    }
}

@Composable
fun BadgeDialog(badge: Badge, onDismiss: () -> Unit) {
    val colorMatrix =
        if (badge.isUnlocked) ColorMatrix() else ColorMatrix().apply { setToSaturation(0f) }
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = badge.imageRes),
                    contentDescription = badge.description,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier
                        .size(250.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = badge.description,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onDismiss() }) {
                    Text(text = "Fechar")
                }
            }
        }
    }
}

data class Badge(
    val imageRes: Int,
    val description: String,
    val isUnlocked: Boolean
)