package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChildAvatar
import com.example.data.models.UserAccount
import com.example.data.models.availableChildAvatars
import com.example.data.models.defaultReadingBadges
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaBorder
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaGold
import com.example.ui.theme.HekayaGoldLight
import com.example.ui.theme.HekayaGreen
import com.example.ui.theme.HekayaGreenBadge
import com.example.ui.theme.HekayaGreenText
import com.example.ui.theme.HekayaInputBg
import com.example.ui.theme.HekayaLightBlue
import com.example.ui.theme.HekayaSurfaceBlue
import com.example.ui.theme.HekayaTextMuted
import com.example.ui.theme.HekayaTextPrimary
import com.example.ui.theme.HekayaTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userAccount: UserAccount,
    onSelectAvatar: (String) -> Unit,
    onUpdateChildName: (String) -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenParentalOverlay: () -> Unit = {},
    onBack: () -> Unit
) {
    var showAvatarPicker by remember { mutableStateOf(false) }
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userAccount.name) }

    val currentAvatar = availableChildAvatars.find { it.id == userAccount.selectedAvatarId }
        ?: availableChildAvatars.first()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HekayaSurfaceBlue)
                .testTag("profile_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = HekayaDarkBlue
                        )
                    }

                    Text(
                        text = "ملف القارئ الصغير 🌟",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaDarkBlue
                    )

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("profile_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "الإعدادات",
                            tint = HekayaBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar and Profile Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Interactive Avatar with Customization Button
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color(currentAvatar.backgroundColorHex))
                                    .border(3.dp, HekayaBlue, CircleShape)
                                    .clickable { showAvatarPicker = true }
                                    .testTag("current_avatar_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentAvatar.emoji,
                                    fontSize = 52.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(HekayaBlue)
                                    .clickable { showAvatarPicker = true }
                                    .testTag("change_avatar_badge"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تغيير الشخصية",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Child Name and Edit Mode
                        if (isEditingName) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.85f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = editedName,
                                    onValueChange = { editedName = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("child_name_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HekayaBlue,
                                        unfocusedBorderColor = HekayaBorder
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (editedName.isNotBlank()) {
                                            onUpdateChildName(editedName.trim())
                                        }
                                        isEditingName = false
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(HekayaBlue)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "حفظ الاسم",
                                        tint = Color.White
                                    )
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    editedName = userAccount.name
                                    isEditingName = true
                                }
                            ) {
                                Text(
                                    text = userAccount.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HekayaDarkBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل الاسم",
                                    tint = HekayaTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Avatar character title badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HekayaLightBlue)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${currentAvatar.name} - ${currentAvatar.title}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Level & EXP Progress Bar
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(HekayaSurfaceBlue)
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = HekayaGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "المستوى ${userAccount.currentLevel}: ${userAccount.levelTitle}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                }

                                Text(
                                    text = "${userAccount.currentExp}/${userAccount.nextLevelExp} XP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { (userAccount.currentExp.toFloat() / userAccount.nextLevelExp).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = HekayaBlue,
                                trackColor = HekayaBorder
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reading Progress Summary Section
                Text(
                    text = "ملخص تقدم القراءة 📊",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Streak Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${userAccount.readingStreakDays} أيام",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HekayaDarkBlue
                            )
                            Text(
                                text = "حماسة القراءة المتتالية",
                                fontSize = 11.sp,
                                color = HekayaTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Completed Stories Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(HekayaLightBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = HekayaBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${userAccount.storiesReadCount}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HekayaDarkBlue
                            )
                            Text(
                                text = "قصة مكتملة",
                                fontSize = 11.sp,
                                color = HekayaTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Reading Time Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(HekayaGoldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${userAccount.readingMinutesThisWeek} دقيقة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HekayaDarkBlue
                            )
                            Text(
                                text = "وقت القراءة أسبوعياً",
                                fontSize = 11.sp,
                                color = HekayaTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Daily Reading Time Limit & Parental Goal Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
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
                                        .background(HekayaLightBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = HekayaBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "الحد اليومي للقراءة (أولياء الأمور)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                    Text(
                                        text = "تم قراءة ${userAccount.todayReadingMinutes} من أصل ${userAccount.dailyReadingLimitMinutes} دقيقة اليوم",
                                        fontSize = 11.sp,
                                        color = HekayaTextSecondary
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onOpenParentalOverlay,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("profile_parental_limit_btn")
                            ) {
                                Text("تعديل الحد", fontSize = 11.sp, color = HekayaBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val progressFraction = if (userAccount.dailyReadingLimitMinutes > 0) {
                            (userAccount.todayReadingMinutes.toFloat() / userAccount.dailyReadingLimitMinutes).coerceIn(0f, 1f)
                        } else 0f

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (userAccount.todayReadingMinutes >= userAccount.dailyReadingLimitMinutes) Color(0xFFE53935) else HekayaBlue,
                            trackColor = HekayaBorder
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Child Story Ratings Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تقييماتي للقصص المقروءة ⭐",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaDarkBlue
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HekayaGoldLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${userAccount.storyRatings.size} تقييمات",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (userAccount.storyRatings.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            userAccount.storyRatings.forEach { (storyId, ratingId) ->
                                val ratingOption = com.example.data.models.availableChildRatings.find { it.id == ratingId }
                                val storyTitle = when (storyId) {
                                    "forest_friends" -> "أصدقاء الغابة السحرية"
                                    "magic_carpet" -> "بساط الريح وعجائب العالم"
                                    "space_voyage" -> "رحلة إلى كوكب الألوان"
                                    "brave_knight" -> "الفارس الصغير والتنين الطيب"
                                    "ocean_discovery" -> "سر المحيط الأزرق العميق"
                                    else -> "قصة حكاية المميزة"
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "📖", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = storyTitle,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HekayaDarkBlue,
                                            maxLines = 1
                                        )
                                    }

                                    if (ratingOption != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(ratingOption.colorHex).copy(alpha = 0.25f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = ratingOption.emoji, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = ratingOption.label,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = HekayaDarkBlue
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "اقرأ قصة جديدة وقيّمها بالإيموجي لتظهر هنا! 🤩",
                                fontSize = 12.sp,
                                color = HekayaTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Reading Badges & Achievements Section
                Text(
                    text = "الأوسمة والإنجازات 🏆",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    defaultReadingBadges.forEach { badge ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF1F5F9)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (badge.isUnlocked) HekayaGoldLight else HekayaBorder
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badge.iconEmoji,
                                        fontSize = 24.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = badge.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (badge.isUnlocked) HekayaDarkBlue else HekayaTextMuted
                                        )
                                        if (badge.isUnlocked) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(HekayaGreenBadge)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "مكتمل ✓",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = HekayaGreenText
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = badge.description,
                                        fontSize = 12.sp,
                                        color = HekayaTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Customization Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = { showAvatarPicker = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("open_avatar_sheet_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                    ) {
                        Text(
                            text = "اختيار وتخصيص شخصية الأفاتار 🎨",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Avatar Customization Bottom Sheet
            if (showAvatarPicker) {
                ModalBottomSheet(
                    onDismissRequest = { showAvatarPicker = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "اختر شخصيتك المفضلة (الأفاتار) 🎭",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                        Text(
                            text = "اختر الرفيق اللطيف الذي سيرافقك في رحلات القراءة والاستكشاف",
                            fontSize = 12.sp,
                            color = HekayaTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(availableChildAvatars, key = { it.id }) { avatar ->
                                val isSelected = userAccount.selectedAvatarId == avatar.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) HekayaLightBlue else HekayaInputBg)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) HekayaBlue else HekayaBorder,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            onSelectAvatar(avatar.id)
                                        }
                                        .padding(12.dp)
                                        .testTag("avatar_option_${avatar.id}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(Color(avatar.backgroundColorHex)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = avatar.emoji, fontSize = 24.sp)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = avatar.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) HekayaBlue else HekayaDarkBlue
                                            )
                                            Text(
                                                text = avatar.title,
                                                fontSize = 10.sp,
                                                color = HekayaTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showAvatarPicker = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("confirm_avatar_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                        ) {
                            Text("تأكيد واختيار الشخصية", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
