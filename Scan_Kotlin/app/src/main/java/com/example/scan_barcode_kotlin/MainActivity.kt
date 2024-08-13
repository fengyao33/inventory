package com.example.scan_barcode_kotlin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.scan_barcode_kotlin.ui.theme.MyApplicationTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity() {

    private var text = mutableStateOf("")
    private var text2 = mutableStateOf("")
    private var text_l = mutableStateOf("")

    fun clearText() {
        text.value = ""
        text2.value = ""
    }

    fun clearText_LnPaller() {
        text_l.value = ""
        text.value = ""
    }

    fun clearText_2P() {
        text2.value = ""
        text.value = ""
    }

    fun clearText_ParNo() {
        text2.value = ""
    }

    private val barCodeLauncher_l = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            text_l.value = result.contents
        }
    }

    private val barCodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            text.value = result.contents
        }
    }
    private val barCodeLauncher2 = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            text2.value = result.contents
        }
    }

    private fun shoowCamera(barCodeOption: Int) {
        val option = ScanOptions()
        option.setDesiredBarcodeFormats(ScanOptions.CODE_39, ScanOptions.CODE_128)
        option.setPrompt("Scan Barcode")
        option.setCameraId(0)
        option.setBeepEnabled(false)
        option.setOrientationLocked(false)

        when (barCodeOption) {
            0 -> barCodeLauncher_l.launch(option)
            1 ->  barCodeLauncher.launch(option)
            2 ->  barCodeLauncher2.launch(option)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val sharedViewModel: SharedViewModel = viewModel()

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        onShowCamera = { option -> shoowCamera(option as Int) },
                        text = text,
                        text2 = text2,
                        text_l = text_l,
                        onClearText = { clearText() },
                        clearText_LnPaller = { clearText_LnPaller() },
                        clearText_2P = { clearText_2P() },
                        clearText_ParNo = { clearText_ParNo() }

                    )
                }
            }
        }
    }
}
