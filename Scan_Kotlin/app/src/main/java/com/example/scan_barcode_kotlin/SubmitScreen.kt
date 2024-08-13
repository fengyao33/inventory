package com.example.scan_barcode_kotlin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Serializable
data class PostResponse(
    val success: Boolean,
    val scan: PostData
)
@Serializable
data class PostData(
    val DateTime: String,
    val Location: String,
    val PalletNo: String,
    val PartNo: String,
    val FullPallet: Boolean,
    val SQoc: String
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    clearText_ParNo: () -> Unit
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
    var isLoading by remember { mutableStateOf(false) }

    suspend fun performPostRequest(): Boolean {
        val postData = PostData(
            DateTime = formattedDateTime,
            Location = sharedViewModel.location,
            PalletNo = sharedViewModel.palletNo,
            PartNo = sharedViewModel.partNo,
            FullPallet = sharedViewModel.fullPallet,
            SQoc = sharedViewModel.sQoc,
        )
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = httpClient.post("http://inventory.sumeeko.com:3001/scan") {
                    contentType(ContentType.Application.Json)
                    setBody(postData)
                }

                if (response.status == HttpStatusCode.OK) {
                    val postResponse: PostResponse = response.body()
                    Log.d("postResponse", postResponse.toString())
                    postResponse.success
                } else {
                    Log.e("SubmitScreen", "Unexpected response status: ${response.status}")
                    false
                }

            } catch (e: Exception) {
                Log.e("SubmitScreen", "Error performing POST request", e)
                e.printStackTrace()
                false
            }
        }

    }

    var quantityOfBoxes by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()

    BackHandler {
        navController.navigate("ScanPartNo")
        clearText_ParNo()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ){
        Column(
        ){
            Text(
                text = "the location: ${sharedViewModel.location}",
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Pallet-No: ${sharedViewModel.palletNo}",
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Part-No: ${sharedViewModel.partNo}",
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                        .background(Color.Black)
                )
                Text(
                    text = "Pallet Full? ${sharedViewModel.fullPallet}",
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = sharedViewModel.fullPallet,
                    onCheckedChange = { sharedViewModel.fullPallet = !sharedViewModel.fullPallet },
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = quantityOfBoxes?.toString() ?: "",
                    onValueChange = { newValue ->
                        val newQuantity = newValue.toIntOrNull()
                        if (newQuantity != null && newQuantity in 0..60) {
                            quantityOfBoxes = newQuantity
                            sharedViewModel.sQoc = newQuantity.toString()
                        }else{
                            quantityOfBoxes = 0
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    label = { Text("Quantity Of carton", fontSize = 8.sp) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
//                            quantityOfBoxes = 0
                        }
                    ),
                    visualTransformation = VisualTransformation.None
                )
            }

//            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                isLoading = true
                CoroutineScope(Dispatchers.Main).launch {
                    val success = performPostRequest()
                    Log.d("success", success.toString())
                    if (success) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "success",
                                actionLabel = "OK",
                                duration = SnackbarDuration.Short
                            )
                        }
                        delay(1500L)
                        navController.navigate("Repeat")
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "false",
                                actionLabel = "OK",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }

                    //submit
                    quantityOfBoxes = 0
                    isLoading = false
                }

            }) {
//                Text(text = "Next", fontSize = 20.sp)
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Next", fontSize = 20.sp)
                }
            }


        }
    }

}

