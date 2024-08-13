package com.example.scan_barcode_kotlin

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.material3.TextField

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPartNoScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onShowCamera: () -> Unit,
    text2: MutableState<String>,
    onClearText: () -> Unit,
    clearText_2P: () -> Unit
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }
//    val scope = CoroutineScope(Dispatchers.Main)
    val scope = rememberCoroutineScope()


    BackHandler {
        navController.navigate("ScanPalle")
        clearText_2P()
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
            Text(
                text = "Pallet-No: ${sharedViewModel.palletNo}",
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
                text = "Please input the Part-No or scan",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = text2.value,
                onValueChange = { newValue -> text2.value = newValue.trimStart { it == '0'} },
                label = { Text("Part-No") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onShowCamera() }, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Scan Barcode")
            }
            Button(onClick = {
                val partNo = text2.value
                if (partNo.isBlank()) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "part-No cannot be empty",
                            actionLabel = "OK",
                            duration = SnackbarDuration.Short
                        )
                    }
                    return@Button
                }
                sharedViewModel.partNo = text2.value
                onClearText()
                navController.navigate("Submit")
            }, modifier = Modifier.padding(bottom = 16.dp)) {
                Text(text = "Next", fontSize = 16.sp)
            }
        }


    }
}