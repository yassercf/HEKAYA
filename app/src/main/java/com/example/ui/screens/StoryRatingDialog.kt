package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.ChildRatingOption
import com.example.data.models.Story
import com.example.data.models.UserAccount
import com.example.data.models.availableChildAvatars
import com.example.data.models.availableChildRatings
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaBorder
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaGold
import com.example.ui.theme.HekayaGoldLight
import com.example.ui.theme.HekayaLightBlue
import com.example.ui.theme.HekayaTextSecondary

@Composable
fun StoryRatingDialog(
    story: Story,
    userAccount: UserAccount,
    currentRatingId: String? = null,
    onRateStory: (String) -> Unit,
    onReadAgain: () -> Unit,
    onBackToLibrary: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRatingId by remember {
        mutableStateOf(currentRatingId ?: userAccount.storyRatings[story.id])
    }
    var hasSubmittedRating by remember { mutableStateOf(false) }

    val currentAvatar = availableChildAvatars.find { it.id == userAccount.selectedAvatarId }
        ?: availableChildAvatars.first()

    val selectedOption = availableChildRatings.find { it.id == selectedRatingId }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("story_rating_dialog"),
                color = Color.White,
                tonalElevation = 6.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with Dismiss button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(currentAvatar.backgroundColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentAvatar.emoji, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تقييم البطل ${userAccount.name}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaDarkBlue
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .testTag("close_rating_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Celebratory Story Finished Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        HekayaLightBlue,
                                        HekayaGoldLight.copy(alpha = 0.6f)
                                    )
                                )
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "🎉", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "مبروك! أنهيت قراءة القصة",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HekayaDarkBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "⭐", fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "\"${story.title}\"",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaBlue,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "كيف كانت تجربتك ومشاعرك مع هذه القصة؟",
                                fontSize = 12.sp,
                                color = HekayaTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6 Interactive Emoji Cards in a 3-column Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(availableChildRatings) { option ->
                            val isSelected = selectedRatingId == option.id
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.06f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "emoji_scale"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) HekayaBlue else HekayaBorder,
                                label = "border_color"
                            )
                            val containerColor by animateColorAsState(
                                targetValue = if (isSelected) HekayaLightBlue else Color(0xFFF8FAFC),
                                label = "container_color"
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(scale)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        selectedRatingId = option.id
                                        hasSubmittedRating = true
                                        onRateStory(option.id)
                                    }
                                    .testTag("rating_option_${option.id}"),
                                colors = CardDefaults.cardColors(containerColor = containerColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = option.emoji,
                                        fontSize = 32.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = option.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = if (isSelected) HekayaDarkBlue else Color(0xFF475569),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = option.description,
                                        fontSize = 9.sp,
                                        color = HekayaTextSecondary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reaction & Praise Feedback Box when rating selected
                    AnimatedVisibility(
                        visible = selectedOption != null,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        if (selectedOption != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(selectedOption.colorHex).copy(alpha = 0.22f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = selectedOption.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = selectedOption.praiseMessage,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HekayaDarkBlue
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = HekayaGold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "+40 نقطة خبرة وإنجاز جديد!",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF92400E)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Read Again
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onReadAgain()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("rate_dialog_read_again_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إعادة القراءة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Back to Library / Confirm
                        Button(
                            onClick = {
                                selectedRatingId?.let { onRateStory(it) }
                                onDismiss()
                                onBackToLibrary()
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("rate_dialog_confirm_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRatingId != null) HekayaBlue else HekayaDarkBlue
                            )
                        ) {
                            Icon(
                                imageVector = if (selectedRatingId != null) Icons.Default.Check else Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedRatingId != null) "حفظ والعودة للمكتبة" else "العودة للمكتبة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
