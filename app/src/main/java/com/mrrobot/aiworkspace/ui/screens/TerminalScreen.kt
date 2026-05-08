package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun TerminalScreen() {

    ScreenShell {

        PageTitle("Live Terminal")

        Subtitle(
            "Safe mobile command simulation and build logs."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Panel(
            title = "Terminal Session",
            subtitle = "Mr. Robot secure shell"
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        Color.Black,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text =
                    "[00:00] Mr. Robot terminal initialized.\n\n" +
                    "[00:01] Workspace ready.\n\n" +
                    "[00:02] Awaiting command...",
                    color = Color(0xFFB388FF),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Run",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = "Copy Logs",
                onClick = {}
            )
        }
    }
}
