package com.example.scan_barcode_kotlin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun DetailsScreen(navController: NavHostController, sharedViewModel: SharedViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Details Screen", fontSize = 54.sp)
        Spacer(modifier = Modifier.height(45.dp))
        Text(text = "Storage: ${sharedViewModel.location}", fontSize = 44.sp)
        Spacer(modifier = Modifier.height(45.dp))
        Text(text = "PN: ${sharedViewModel.palletNo}", fontSize = 44.sp)

    }
}