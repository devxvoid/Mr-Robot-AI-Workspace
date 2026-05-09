package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.runtime.Composable
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun TerminalScreen() {

    ScreenShell {

        Panel(
            title = "Terminal",
            subtitle = "Terminal module."
        ) {

            SoftText(
                text = "Terminal system initialized."
            )
        }
    }
}
