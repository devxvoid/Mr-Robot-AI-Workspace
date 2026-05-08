package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun ProfileScreen() {
    ScreenShell {
        Title("Profile")
        Subtitle("Mr. Robot operator profile.")
        Spacer(Modifier.height(16.dp))
        GlassCard {
            Title("devxvoid")
            Subtitle("Role: App Developer")
            Subtitle("Principle: When we lose our principles, we invite chaos.")
        }
    }
}
