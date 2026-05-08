package com.mrrobot.aiworkspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mrrobot.aiworkspace.navigation.AppNavGraph
import com.mrrobot.aiworkspace.ui.theme.MrRobotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MrRobotTheme {
                AppNavGraph()
            }
        }
    }
}
