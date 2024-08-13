package com.example.scan_barcode_kotlin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onShowCamera: () -> Unit,
    text_l: MutableState<String>,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
//    val scope = CoroutineScope(Dispatchers.Main)
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Please input the location or scan", fontSize = 24.sp)
            TextField(
                value = text_l.value,
                onValueChange = { newValue -> text_l.value = newValue.trimStart { it == '0'} },
                modifier = Modifier.padding(vertical = 50.dp),
                placeholder = {
                    Text(text = "input the location")
                }
            )
            Button(
                onClick = { onShowCamera() }, modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Scan Barcode")
            }
            Button(onClick = {
                val localNum = text_l.value

                if (localNum.isBlank()) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Location cannot be empty",
                            actionLabel = "OK",
                            duration = SnackbarDuration.Short
                        )
                    }
                    return@Button
                }
                sharedViewModel.location = text_l.value
                navController.navigate("ScanPalle")
            }) {
                Text(text = "Next", fontSize = 16.sp)
            }
        }
    }
}