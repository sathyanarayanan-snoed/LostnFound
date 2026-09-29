package com.example.lostnfound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lostnfound.ui.theme.AccentGreen
import com.example.lostnfound.ui.theme.AccentGreenContainer
import com.example.lostnfound.ui.theme.WarningAmber
import com.example.lostnfound.ui.theme.WarningAmberContainer

@Composable
fun TimeElapsedBadge(
    reportedAt: Long,
    modifier: Modifier = Modifier
) {
    val elapsed = System.currentTimeMillis() - reportedAt
    val minutes = elapsed / 60000
    val hours = minutes / 60
    val days = hours / 24

    val text = when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> "${days / 7}w ago"
    }

    val isExpiringSoon = days >= 5
    val bgColor = if (isExpiringSoon) WarningAmberContainer else AccentGreenContainer
    val textColor = if (isExpiringSoon) WarningAmber else AccentGreen

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor.copy(alpha = 0.95f),
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
