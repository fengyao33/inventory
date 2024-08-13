//package com.example.myapplication
//
//import android.content.Context
//import androidx.compose.runtime.mutableStateOf
//import com.journeyapps.barcodescanner.ScanContract
//import com.journeyapps.barcodescanner.ScanOptions
//
//
//class BarcodeScan(context: Context) {
//    private var text = mutableStateOf("")
//
//    private  val  barCodeLauncher = registarForActivityResult(ScanContract()){}
//
//    private fun shoowCamera(){
//        val option = ScanOptions()
//        option.setDesiredBarcodeFormats(ScanOptions.CODE_128)
//        option.setPrompt("Scan Barcode")
//        option.setCameraId(0)
//        option.setBeepEnabled(false)
//        option.setOrientationLocked(false)
//    }
//
//}