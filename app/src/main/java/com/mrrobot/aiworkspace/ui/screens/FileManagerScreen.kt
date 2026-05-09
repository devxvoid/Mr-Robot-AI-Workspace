package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.runtime.Composable
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun FileManagerScreen() {

    ScreenShell {

        Panel(
            title = "FileManager",
            subtitle = "FileManager module."
        ) {

            SoftText(
                text = "FileManager system initialized."
            )
        }
    }
}
