package com.example.scan_barcode_kotlin

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class FindResponse(
    val success: Boolean,
    val has: Boolean,
//    val time: String ,
)

@Serializable
data class FindData(
    val DateTime:String,
    val Location:String,
    val PalletNo:String,
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPalletScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onShowCamera: () -> Unit,
    text: MutableState<String>,
    clearText_LnPaller: () -> Unit
) {

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // if you want to ignore unknown keys in the response
            })
        }
        install(HttpTimeout)
    }

    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formattedDateTime = currentDateTime.format(formatter)


    suspend fun performPostRequest(): String? {
        val postData = FindData(
            DateTime = formattedDateTime,
            Location = sharedViewModel.location,
            PalletNo = text.value,
        )
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = httpClient.post("http://inventory.sumeeko.com:3001/findlocation") {
                    contentType(ContentType.Application.Json)
                    setBody(postData)
                }

                if (response.status == HttpStatusCode.OK) {
                    val postResponse: FindResponse = response.body()
                    Log.d("postResponse", postResponse.toString())
                    Log.d("has", postResponse.toString())
                    if (postResponse.has) {
                        "The inventory already has information"
                    } else {
                        "No existing data found"
                    }
                } else {
                    "Unexpected response status: ${response.status}"
                }

            } catch (e: Exception) {
                Log.e("SubmitScreen", "Error performing POST request", e)
                e.message
            }
        }

    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

//    val shouldShowSnackbar = remember { mutableStateOf(false) }


    LaunchedEffect(text.value) {
        if (text.value.isNotBlank()) {
            val mes = performPostRequest()
            if (mes != null && mes.isNotBlank()){
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = mes,
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    BackHandler {
        navController.navigate("Home")
        clearText_LnPaller()
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Column(
        ){
            Text(
                text = "the location: ${sharedViewModel.location}",
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Please input the Paller or scan",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                    value = text.value,
                    onValueChange = { newValue -> text.value = newValue.trimStart { it == '0'} },
                    label = { Text("Pallet-No") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
//            }

            Spacer(modifier = Modifier.height(45.dp))
            Button(
                onClick = { onShowCamera() }, modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Scan Barcode")
            }

            Button(
                onClick = {
                    val palletNo = text.value

                    if (palletNo.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Pallet-No cannot be empty",
                                actionLabel = "OK",
                                duration = SnackbarDuration.Short
                            )
                        }
                        return@Button
                    }
                    sharedViewModel.palletNo = text.value
//                onClearText()
                    navController.navigate("ScanPartNo")
                }, modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Next", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(90.dp))

            Button(
                onClick = {
                    val palletNo = "X"
                    sharedViewModel.palletNo = palletNo
                    navController.navigate("ScanPartNo")
                }, modifier = Modifier.padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red // Correct syntax to set the background color to red
                )){
                Text(text = "No Pallet No", fontSize = 16.sp)
            }


        }

    }
}