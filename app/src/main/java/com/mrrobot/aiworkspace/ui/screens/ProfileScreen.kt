package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.runtime.Composable
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun ProfileScreen() {

    ScreenShell {

        Panel(
            title = "Profile",
            subtitle = "Profile module."
        ) {

            SoftText(
                text = "Profile system initialized."
            )
        }
    }
}
