package com.mrrobot.aiworkspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val NeonCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFF8B5CF6)
val NeonGreen = Color(0xFF22C55E)
val DeepBg = Color(0xFF030712)
val Panel = Color(0xFF111827)
val PanelSoft = Color(0xFF0B1020)
val SoftText = Color(0xFFB8C0CC)
val Danger = Color(0xFFFF6B6B)

@Composable
fun ScreenShell(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF07111F),
                        Color(0xFF111827)
                    )
                )
            )
    ) {
        AmbientGlow()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            content = content
        )
    }
}

@Composable
fun AmbientGlow() {
    val infinite = rememberInfiniteTransition(label = "ambient_glow")
    val alpha by infinite.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-80).dp, y = 20.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            NeonCyan.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 120.dp, y = 60.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            NeonPurple.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
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
                brush = Brush.horizontalGradient(
                    listOf(
                        NeonCyan.copy(.45f),
                        NeonPurple.copy(.38f),
                        NeonGreen.copy(.25f)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel.copy(alpha = .88f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        content = {
            Column(
                modifier = Modifier.padding(18.dp),
                content = content
            )
        }
    )
}

@Composable
fun PremiumHeader(
    title: String,
    subtitle: String,
    badge: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 34.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    color = SoftText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            if (badge != null) {
                Surface(
                    color = NeonCyan.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = badge,
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp
    )
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        color = SoftText,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

@Composable
fun CyberButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonCyan,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PremiumMetric(
    label: String,
    value: String,
    description: String
) {
    GlassCard {
        Text(
            text = value,
            color = NeonCyan,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Subtitle(description)
    }
}

@Composable
fun StatusPill(
    text: String,
    color: Color = NeonCyan
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
