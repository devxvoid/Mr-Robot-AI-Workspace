package com.mrrobot.aiworkspace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val NeonCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFF8B5CF6)
val DeepBg = Color(0xFF05070A)
val Panel = Color(0xFF111827)
val SoftText = Color(0xFFB8C0CC)

@Composable
fun ScreenShell(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepBg, Color(0xFF0B1020), Color(0xFF111827))
                )
            )
            .padding(18.dp),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(NeonCyan.copy(.45f), NeonPurple.copy(.45f))),
                shape = RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Panel.copy(alpha = .92f)),
        content = {
            Column(modifier = Modifier.padding(20.dp), content = content)
        }
    )
}

@Composable
fun Title(text: String) {
    Text(text = text, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun Subtitle(text: String) {
    Text(text = text, color = SoftText, fontSize = 14.sp)
}

@Composable
fun CyberButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}
