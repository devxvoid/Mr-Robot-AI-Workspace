package com.mrrobot.aiworkspace.ui.components

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

@Composable
fun TerminalPreviewCard() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.Black,
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {

        Text(
            text = "[01:02] Workspace initialized",
            color = Color(0xFF00FFB2),
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "[01:05] OpenRouter connection active",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "[01:09] Agent runtime online",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "[01:14] Workflow engine ready",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
    }
}
