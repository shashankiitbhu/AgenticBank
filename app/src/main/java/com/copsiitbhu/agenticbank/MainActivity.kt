package com.copsiitbhu.agenticbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.copsiitbhu.agenticbank.navigation.AppNavGraph
import com.copsiitbhu.agenticbank.ui.theme.AgenticBankTheme
import com.copsiitbhu.agenticbank.viewmodel.AgentViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            AgenticBankTheme {

                val viewModel: AgentViewModel = viewModel()

                AppNavGraph(
                    viewModel = viewModel
                )
            }
        }
    }
}
