package com.example.cryptolab_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CryptoTrackerScreen()
                }
            }
        }
    }
}

@Composable
fun CryptoTrackerScreen() {
    var btcPrice by remember { mutableStateOf("---") }
    var ethPrice by remember { mutableStateOf("---") }
    var solPrice by remember { mutableStateOf("---") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    fun fetchPrices() {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = withContext(Dispatchers.IO) {
                    URL("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,solana&vs_currencies=usd").readText()
                }

                val json = JSONObject(response)
                btcPrice = "$ ${json.getJSONObject("bitcoin").getString("usd")}"
                ethPrice = "$ ${json.getJSONObject("ethereum").getString("usd")}"
                solPrice = "$ ${json.getJSONObject("solana").getString("usd")}"
            } catch (e: Exception) {
                errorMessage = "Помилка мережі. Перевірте підключення."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchPrices()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Crypto Tracker", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(text = "Live Web API: CoinGecko", color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(40.dp))

        CryptoCard("Bitcoin (BTC)", btcPrice)
        Spacer(modifier = Modifier.height(16.dp))
        CryptoCard("Ethereum (ETH)", ethPrice)
        Spacer(modifier = Modifier.height(16.dp))
        CryptoCard("Solana (SOL)", solPrice)

        Spacer(modifier = Modifier.weight(1f))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { fetchPrices() }) {
                Text("Спробувати ще раз")
            }
        } else {
            Button(
                onClick = { fetchPrices() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Оновити")
                Spacer(Modifier.width(8.dp))
                Text("Оновити курси", fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CryptoCard(name: String, price: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = price,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}