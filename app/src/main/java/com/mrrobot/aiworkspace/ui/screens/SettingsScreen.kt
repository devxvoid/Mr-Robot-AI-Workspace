package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun SettingsScreen() {

    ScreenShell {

        PageTitle("Settings")

        Subtitle("Professional AI workspace module.")

        Spacer(modifier = Modifier.height(18.dp))

        Panel(
            title = "Settings Module",
            subtitle = "Stable Compose implementation"
        ) {

            StatusPill("READY")

            SoftText(
                "This module has been stabilized by the self-healing workflow."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Continue",
                onClick = {}
            )
        }
    }
}
