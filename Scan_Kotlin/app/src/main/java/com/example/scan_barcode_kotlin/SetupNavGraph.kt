package com.example.scan_barcode_kotlin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onShowCamera: (Int) -> Unit,
    text: MutableState<String>,
    text2: MutableState<String>,
    text_l:  MutableState<String>,
    onClearText: () -> Unit,
    clearText_LnPaller: () -> Unit,
    clearText_2P: () -> Unit,
    clearText_ParNo: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable("Home") {
            HomeScreen(
                navController,
                sharedViewModel,
                onShowCamera = { onShowCamera(0) },
                text_l
            )
        }
        composable("ScanPalle") {
            ScanPalletScreen(
                navController,
                sharedViewModel,
                onShowCamera = { onShowCamera(1) },
                text,
                clearText_LnPaller
            )
        }
        composable("ScanPartNo") {
            ScanPartNoScreen(
                navController,
                sharedViewModel,
                onShowCamera = { onShowCamera(2) },
                text2,
                onClearText,
                clearText_2P
            )
        }
        composable("Submit") { SubmitScreen(navController, sharedViewModel, clearText_ParNo) }
        composable("Repeat") { RepeatScreen(navController, sharedViewModel) }
    }
}