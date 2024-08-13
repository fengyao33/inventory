package com.example.scan_barcode_kotlin

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class SharedViewModel : ViewModel() {
    var location by mutableStateOf("")
    var palletNo by mutableStateOf("")
    var partNo by mutableStateOf("")
    var fullPallet by mutableStateOf(false)
    var sQoc by mutableStateOf("") // 箱數
}