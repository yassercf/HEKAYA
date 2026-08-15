package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ReaderSettings
import com.example.data.models.ReaderTheme
import com.example.data.models.Story
import com.example.data.models.UserAccount
import com.example.data.models.availableChildRatings
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaGold
import com.example.ui.theme.HekayaLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryReaderScreen(
    story: Story?,
    pageIndex: Int,
    isAudioPlaying: Boolean,
    ttsStatus: String = "الراوي جاهز للقراءة 🎙️",
    isBookmarked: Boolean = false,
    bookmarkBannerMessage: String? = null,
    onDismissBookmarkBanner: () -> Unit = {},
    userAccount: UserAccount = UserAccount(),
    onToggleBookmark: () -> Unit = {},
    onTogglePageBookmark: (String, Int) -> Unit = { _, _ -> },
    onJumpToPage: (Int) -> Unit = {},
    readerSettings: ReaderSettings = ReaderSettings(),
    onSetTheme: (ReaderTheme) -> Unit = {},
    onSetFontSize: (Float) -> Unit = {},
    onIncreaseFontSize: () -> Unit = {},
    onDecreaseFontSize: () -> Unit = {},
    onToggleBold: (Boolean) -> Unit = {},
    onSetLineSpacing: (Float) -> Unit = {},
    onToggleNightMode: () -> Unit = {},
    onToggleAudio: () -> Unit,
    onReplayAudio: () -> Unit = {},
    onSetSpeechRate: (Float) -> Unit = {},
    onSetSpeechPitch: (Float) -> Unit = {},
    onNextPage: () -> Unit,
    onPrevPage: () -> Unit,
    onRateStory: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    onTriggerSecurityAlert: () -> Unit
) {
    val totalPages = story?.pages?.size ?: 1
    val currentPage = story?.pages?.getOrNull(pageIndex)

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showPagesIndexSheet by remember { mutableStateOf(false) }

    val savedBookmark = story?.let { userAccount.bookmarks[it.id] }
    val isCurrentPageBookmarked = savedBookmark?.pageIndex == pageIndex

    val userRatingId = story?.let { userAccount.storyRatings[it.id] }
    val userRatingOption = availableChildRatings.find { it.id == userRatingId }

    val currentTheme = readerSettings.theme
    val isNight = currentTheme.isDark

    val progressFraction = ((pageIndex + 1).toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(400),
        label = "readingProgress"
    )
    val progressPercentage = ((pageIndex + 1) * 100 / totalPages)

    // Animated color transitions for reader themes
    val animatedCardBg by animateColorAsState(
        targetValue = Color(currentTheme.cardBackground),
        animationSpec = tween(350),
        label = "cardBg"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = Color(currentTheme.textColor),
        animationSpec = tween(350),
        label = "textColor"
    )
    val animatedSecondaryTextColor by animateColorAsState(
        targetValue = Color(currentTheme.secondaryTextColor),
        animationSpec = tween(350),
        label = "secondaryTextColor"
    )
    val animatedAudioBarBg by animateColorAsState(
        targetValue = Color(currentTheme.audioBarBackground),
        animationSpec = tween(350),
        label = "audioBarBg"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isNight) Color(0xFF090D14) else Color.Black)
                .testTag("story_reader_screen")
        ) {
            // Background Full Story Illustration
            if (currentPage != null) {
                Image(
                    painter = painterResource(id = currentPage.imageRes),
                    contentDescription = "صفحة القصة $pageIndex",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Dark Top & Bottom Vignette for reading clarity
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                (if (isNight) Color.Black else Color.Black).copy(alpha = 0.6f),
                                Color.Transparent,
                                (if (isNight) Color(0xFF05080E) else Color.Black).copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Bar & Visual Reading Progress Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                // Top Continuous Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .testTag("reader_top_progress_bar"),
                    color = HekayaGold,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )

                // Top Action Controls Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }

                    // Story Title & Progress Chip (Clickable to open pages index)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .clickable { showPagesIndexSheet = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("reader_pages_index_trigger"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "فهرس الصفحات",
                            tint = HekayaGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = story?.title ?: "حكاية",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(HekayaGold.copy(alpha = 0.35f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$progressPercentage%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HekayaGold
                            )
                        }
                    }

                    // Action Controls: Bookmark (Page specific) + Rating + Night toggle + Font/Theme Sheet + Security
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Story Rating button
                        IconButton(
                            onClick = { showRatingDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (userRatingOption != null) Color(userRatingOption.colorHex).copy(alpha = 0.85f)
                                    else Color.Black.copy(alpha = 0.45f)
                                )
                                .testTag("reader_rating_button")
                        ) {
                            if (userRatingOption != null) {
                                Text(text = userRatingOption.emoji, fontSize = 16.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "تقييم القصة بالإيموجي",
                                    tint = HekayaGold
                                )
                            }
                        }

                        // Page Bookmark Ribbon Button (Saves Current Page)
                        IconButton(
                            onClick = {
                                story?.let { onTogglePageBookmark(it.id, pageIndex) }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (isCurrentPageBookmarked) HekayaGold else Color.Black.copy(alpha = 0.45f)
                                )
                                .border(
                                    width = if (isCurrentPageBookmarked) 2.dp else 0.dp,
                                    color = if (isCurrentPageBookmarked) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .testTag("reader_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isCurrentPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isCurrentPageBookmarked) "إزالة علامة هذه الصفحة" else "حفظ علامة القراءة في هذه الصفحة",
                                tint = if (isCurrentPageBookmarked) HekayaDarkBlue else Color.White
                            )
                        }

                        // Quick Night Mode Toggle Button
                        IconButton(
                            onClick = onToggleNightMode,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                                .testTag("quick_night_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (isNight) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isNight) "الوضع النهاري" else "الوضع الليلي",
                                tint = if (isNight) HekayaGold else Color.White
                            )
                        }

                        // Reading Appearance Customizer Button (Font size & theme)
                        IconButton(
                            onClick = { showSettingsSheet = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (showSettingsSheet) HekayaBlue else Color.Black.copy(alpha = 0.45f)
                                )
                                .testTag("reader_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "تخصيص الخط والوضع الليلي",
                                tint = Color.White
                            )
                        }

                        // Security Shield Protection icon
                        IconButton(
                            onClick = onTriggerSecurityAlert,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                                .testTag("security_trigger_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "حماية المحتوى",
                                tint = Color.White
                            )
                        }
                    }
                }

                // If saved bookmark exists on a different page of this story, show quick jump chip!
                if (savedBookmark != null && savedBookmark.pageIndex != pageIndex && story != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HekayaGold)
                            .clickable { onJumpToPage(savedBookmark.pageIndex) }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                            .testTag("jump_to_saved_bookmark_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = HekayaDarkBlue,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "لديك علامة محفوظة في الصفحة ${savedBookmark.pageIndex + 1} — انقر للانتقال إليها ↩",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaDarkBlue
                            )
                        }
                    }
                }

                // Visual Segmented Page Progress Track (clickable to jump between pages)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("reader_segmented_progress_bar"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalPages) {
                        val isCurrentOrRead = i <= pageIndex
                        val isCurrent = i == pageIndex
                        val isPageBookmarked = savedBookmark?.pageIndex == i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isCurrent) 6.dp else 4.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isPageBookmarked -> Color(0xFFFFB300)
                                        isCurrent -> HekayaGold
                                        isCurrentOrRead -> HekayaGold.copy(alpha = 0.7f)
                                        else -> Color.White.copy(alpha = 0.25f)
                                    }
                                )
                                .clickable { onJumpToPage(i) }
                        )
                    }
                }
            }

            // Floating Animated Bookmark Notification Toast
            AnimatedVisibility(
                visible = bookmarkBannerMessage != null,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp)
            ) {
                if (bookmarkBannerMessage != null) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.5.dp, HekayaGold),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bookmark_toast_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(HekayaGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = HekayaDarkBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = bookmarkBannerMessage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = onDismissBookmarkBanner,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إغلاق",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Narrative Card + Audio Bar + Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Themed Narrative Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("narrative_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = animatedCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    border = if (isNight) BorderStroke(1.dp, Color(0xFF334155)) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Audio Player & Sparkle AI narrator bar
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(animatedAudioBarBg)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isAudioPlaying) HekayaGold else HekayaBlue)
                                            .clickable { onToggleAudio() }
                                            .testTag("audio_toggle_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isAudioPlaying) "إيقاف مؤقت" else "تشغيل الصوت",
                                            tint = if (isAudioPlaying) Color(0xFF1E293B) else Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Replay button
                                    IconButton(
                                        onClick = onReplayAudio,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isNight) Color(0xFF334155) else Color.White.copy(alpha = 0.8f))
                                            .testTag("audio_replay_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "إعادة قراءة الصفحة",
                                            tint = if (isNight) Color.White else HekayaBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isAudioPlaying) "الراوي يقرأ الآن..." else "الراوي الذكي",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isNight) Color.White else HekayaDarkBlue
                                            )
                                            if (isAudioPlaying) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = null,
                                                    tint = HekayaGold,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = ttsStatus,
                                            fontSize = 10.sp,
                                            color = if (isNight) Color(0xFF94A3B8) else HekayaBlue,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Quick Speed Selector Chips
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(0.8f to "0.8x", 1.0f to "1.0x", 1.2f to "1.2x").forEach { (rate, label) ->
                                        val isCurrent = (readerSettings.speechRate - rate).let { if (it < 0) -it else it } < 0.05f
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isCurrent) HekayaBlue else (if (isNight) Color(0xFF334155) else Color.White.copy(alpha = 0.7f))
                                                )
                                                .clickable { onSetSpeechRate(rate) }
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 10.sp,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isCurrent) Color.White else (if (isNight) Color(0xFFCBD5E1) else HekayaDarkBlue)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Page Text with Dynamic Font Size, Weight & Line Spacing
                        Text(
                            text = currentPage?.text ?: "نص الصفحة...",
                            fontSize = readerSettings.fontSizeSp.sp,
                            lineHeight = (readerSettings.fontSizeSp * readerSettings.lineSpacingMultiplier).sp,
                            fontWeight = if (readerSettings.isBoldText) FontWeight.Bold else FontWeight.Medium,
                            color = animatedTextColor,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("story_page_text")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Reading Progress Bar
                        LinearProgressIndicator(
                            progress = { (pageIndex + 1).toFloat() / totalPages.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = HekayaBlue,
                            trackColor = if (isNight) Color(0xFF334155) else HekayaLightBlue,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Navigation Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onPrevPage,
                                enabled = pageIndex > 0,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (pageIndex > 0) {
                                            if (isNight) Color(0xFF334155) else HekayaLightBlue
                                        } else {
                                            if (isNight) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                                        }
                                    )
                                    .testTag("prev_page_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "الصفحة السابقة",
                                    tint = if (pageIndex > 0) {
                                        if (isNight) Color.White else HekayaBlue
                                    } else {
                                        Color.Gray.copy(alpha = 0.5f)
                                    }
                                )
                            }

                            Text(
                                text = "الصفحة ${pageIndex + 1} من $totalPages",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = animatedSecondaryTextColor
                            )

                            if (pageIndex < totalPages - 1) {
                                IconButton(
                                    onClick = onNextPage,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(HekayaBlue)
                                        .testTag("next_page_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "الصفحة التالية",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                // Final Page: Celebratory Rating Trigger Button
                                Button(
                                    onClick = { showRatingDialog = true },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (userRatingOption != null) HekayaGold else HekayaBlue
                                    ),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("finish_and_rate_story_btn")
                                ) {
                                    Text(
                                        text = if (userRatingOption != null) "${userRatingOption.emoji} تقييمك" else "إنهاء وتقييم 🎉",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (userRatingOption != null) HekayaDarkBlue else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Child Story Rating Modal Dialog
            if (showRatingDialog && story != null) {
                StoryRatingDialog(
                    story = story,
                    userAccount = userAccount,
                    currentRatingId = userRatingId,
                    onRateStory = { ratingId ->
                        onRateStory(story.id, ratingId)
                    },
                    onReadAgain = {
                        // Reset to first page
                        showRatingDialog = false
                    },
                    onBackToLibrary = {
                        showRatingDialog = false
                        onBack()
                    },
                    onDismiss = { showRatingDialog = false }
                )
            }

            // Reader Settings Customizer Sheet
            if (showSettingsSheet) {
                ReaderSettingsBottomSheet(
                    settings = readerSettings,
                    onSetTheme = onSetTheme,
                    onSetFontSize = onSetFontSize,
                    onIncreaseFontSize = onIncreaseFontSize,
                    onDecreaseFontSize = onDecreaseFontSize,
                    onToggleBold = onToggleBold,
                    onSetLineSpacing = onSetLineSpacing,
                    onSetSpeechRate = onSetSpeechRate,
                    onSetSpeechPitch = onSetSpeechPitch,
                    onDismiss = { showSettingsSheet = false }
                )
            }

            // Story Pages Index & Bookmarks Sheet
            if (showPagesIndexSheet && story != null) {
                StoryPagesBottomSheet(
                    story = story,
                    currentPageIndex = pageIndex,
                    savedBookmarkPageIndex = savedBookmark?.pageIndex,
                    onJumpToPage = { targetPage ->
                        onJumpToPage(targetPage)
                        showPagesIndexSheet = false
                    },
                    onToggleBookmarkOnPage = { targetPage ->
                        onTogglePageBookmark(story.id, targetPage)
                    },
                    onDismiss = { showPagesIndexSheet = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryPagesBottomSheet(
    story: Story,
    currentPageIndex: Int,
    savedBookmarkPageIndex: Int?,
    onJumpToPage: (Int) -> Unit,
    onToggleBookmarkOnPage: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFF0F172A),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF64748B))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HekayaGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = HekayaGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "فهرس صفحات القصة والعلامات",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "${story.title} • ${story.pages.size} صفحات",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (savedBookmarkPageIndex != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onJumpToPage(savedBookmarkPageIndex) }
                            .testTag("sheet_jump_saved_bookmark_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = HekayaGold),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(HekayaDarkBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = HekayaGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "علامة القراءة المحفوظة 🔖",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = HekayaDarkBlue
                                    )
                                    Text(
                                        text = "الصفحة ${savedBookmarkPageIndex + 1} من ${story.pages.size} — انقر للمتابعة",
                                        fontSize = 11.sp,
                                        color = HekayaDarkBlue.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "انتقال",
                                tint = HekayaDarkBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "جميع الصفحات:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // List of all pages
                story.pages.forEachIndexed { idx, page ->
                    val isCurrent = idx == currentPageIndex
                    val isBookmarked = idx == savedBookmarkPageIndex

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onJumpToPage(idx) }
                            .testTag("page_item_$idx"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isCurrent -> Color(0xFF1E293B)
                                isBookmarked -> Color(0xFF1B2A4A)
                                else -> Color(0xFF131C2E)
                            }
                        ),
                        border = when {
                            isBookmarked -> BorderStroke(1.5.dp, HekayaGold)
                            isCurrent -> BorderStroke(1.5.dp, HekayaBlue)
                            else -> null
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Page image thumbnail
                                Image(
                                    painter = painterResource(id = page.imageRes),
                                    contentDescription = "صورة صفحة ${idx + 1}",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "الصفحة ${idx + 1}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        if (isBookmarked) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(HekayaGold)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "علامة محفوظة 🔖",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = HekayaDarkBlue
                                                )
                                            }
                                        }
                                        if (isCurrent) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(HekayaBlue)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "أنت هنا 📍",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = page.text,
                                        fontSize = 11.sp,
                                        color = Color.LightGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Bookmark toggle button on this page
                            IconButton(
                                onClick = { onToggleBookmarkOnPage(idx) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isBookmarked) HekayaGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = if (isBookmarked) "إزالة العلامة" else "تثبيت علامة",
                                    tint = if (isBookmarked) HekayaGold else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsBottomSheet(
    settings: ReaderSettings,
    onSetTheme: (ReaderTheme) -> Unit,
    onSetFontSize: (Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: (Boolean) -> Unit,
    onSetLineSpacing: (Float) -> Unit,
    onSetSpeechRate: (Float) -> Unit = {},
    onSetSpeechPitch: (Float) -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFF1E293B),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF64748B))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = HekayaGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تخصيص القراءة والوضع الليلي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 1: Reading Theme / Night Modes
                Text(
                    text = "نمط الشاشة والألوان",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReaderTheme.entries.forEach { theme ->
                        val isSelected = settings.theme == theme
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSetTheme(theme) }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) HekayaGold else Color(0xFF475569),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = Color(theme.cardBackground))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(theme.textColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(theme.cardBackground),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = theme.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color(theme.textColor),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // SECTION 2: Font Size Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حجم الخط",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )

                    Text(
                        text = "${settings.fontSizeSp.toInt()} نقطة",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaGold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stepper + Slider row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDecreaseFontSize,
                        enabled = settings.fontSizeSp > 14f,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "تصغير الخط",
                            tint = if (settings.fontSizeSp > 14f) Color.White else Color.Gray
                        )
                    }

                    Slider(
                        value = settings.fontSizeSp,
                        onValueChange = { onSetFontSize(it) },
                        valueRange = 14f..30f,
                        steps = 7,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = HekayaGold,
                            activeTrackColor = HekayaBlue,
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )

                    IconButton(
                        onClick = onIncreaseFontSize,
                        enabled = settings.fontSizeSp < 30f,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "تكبير الخط",
                            tint = if (settings.fontSizeSp < 30f) Color.White else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Size Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "صغير" to 15f,
                        "افتراضي" to 18f,
                        "كبير" to 22f,
                        "كبير جداً" to 26f
                    ).forEach { (label, size) ->
                        val isCurrent = settings.fontSizeSp == size
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) HekayaBlue else Color(0xFF334155))
                                .clickable { onSetFontSize(size) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) Color.White else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // SECTION 3: Line Spacing & Text Weight
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Line Spacing
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تباعد الأسطر",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "عادي" to 1.4f,
                                "مريح" to 1.6f,
                                "واسع" to 1.9f
                            ).forEach { (label, mult) ->
                                val selected = settings.lineSpacingMultiplier == mult
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) HekayaBlue else Color.Transparent)
                                        .clickable { onSetLineSpacing(mult) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    // Bold Text Toggle
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "سماكة النص",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "انسيابي" to false,
                                "عريض" to true
                            ).forEach { (label, isBold) ->
                                val selected = settings.isBoldText == isBold
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) HekayaBlue else Color.Transparent)
                                        .clickable { onToggleBold(isBold) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) Color.White else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // SECTION 4: TTS Voice & Speech Settings
                Text(
                    text = "صوت ونبرة الراوي الذكي (TTS)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Speech Speed Slider & Chips
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = HekayaGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "سرعة نطق القصة",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${(settings.speechRate * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            0.75f to "هادئ وبطيء",
                            1.0f to "طبيعي متوازن",
                            1.25f to "سريع مرح"
                        ).forEach { (rate, label) ->
                            val isCurrent = (settings.speechRate - rate).let { if (it < 0) -it else it } < 0.05f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) HekayaBlue else Color(0xFF1E293B))
                                    .clickable { onSetSpeechRate(rate) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speech Pitch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نبرة الصوت",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = when {
                                settings.speechPitch > 1.15f -> "طفولية مرحة 🎈"
                                settings.speechPitch < 0.95f -> "عميقة دافئة 🌙"
                                else -> "متزنة 🌟"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = HekayaGold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            0.85f to "دافئة 🌙",
                            1.0f to "طبيعية 🌟",
                            1.3f to "طفولية 🎈"
                        ).forEach { (pitch, label) ->
                            val isCurrent = (settings.speechPitch - pitch).let { if (it < 0) -it else it } < 0.05f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) HekayaGold else Color(0xFF1E293B))
                                    .clickable { onSetSpeechPitch(pitch) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) Color(0xFF1E293B) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Live Preview Card inside BottomSheet
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(settings.theme.cardBackground)),
                    border = BorderStroke(1.dp, Color(0xFF475569))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "معاينة القراءة الفورية:",
                            fontSize = 11.sp,
                            color = Color(settings.theme.secondaryTextColor),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "كان ياما كان في قديم الزمان، تجربة قراءة مريحة وهادئة لعينيك في كل الأوقات.",
                            fontSize = settings.fontSizeSp.sp,
                            lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp,
                            fontWeight = if (settings.isBoldText) FontWeight.Bold else FontWeight.Medium,
                            color = Color(settings.theme.textColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

