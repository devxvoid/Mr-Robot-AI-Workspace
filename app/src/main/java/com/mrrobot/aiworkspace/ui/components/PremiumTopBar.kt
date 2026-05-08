package com.mrrobot.aiworkspace.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrrobot.aiworkspace.ui.theme.TextPrimary
import com.mrrobot.aiworkspace.ui.theme.TextSecondary

@Composable
fun PremiumTopBar(
    title: String,
    subtitle: String
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 15.sp
        )
    }
}
