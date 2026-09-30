package com.k3n3sh.gsecom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.k3n3sh.gsecom.presentation.navigation.AppNavHost
import com.k3n3sh.gsecom.presentation.theme.GsEcomTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GsEcomTheme {
                AppNavHost()
            }
        }
    }
}
