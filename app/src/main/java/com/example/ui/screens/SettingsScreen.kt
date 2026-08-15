package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserAccount
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaDivider
import com.example.ui.theme.HekayaGold
import com.example.ui.theme.HekayaGoldLight
import com.example.ui.theme.HekayaGreen
import com.example.ui.theme.HekayaGreenBadge
import com.example.ui.theme.HekayaGreenText
import com.example.ui.theme.HekayaLightBlue
import com.example.ui.theme.HekayaSurfaceBlue
import com.example.ui.theme.HekayaTextSecondary

@Composable
fun SettingsScreen(
    userAccount: UserAccount,
    onUpgradeClick: () -> Unit,
    onTogglePlan: () -> Unit,
    onTriggerSecurityAlert: () -> Unit,
    onEditProfile: () -> Unit = {},
    onOpenParentalOverlay: () -> Unit = {},
    onBack: () -> Unit
) {
    var screenCaptureBlockEnabled by remember { mutableStateOf(true) }
    var bedtimeModeEnabled by remember { mutableStateOf(userAccount.isBedtimeModeEnabled) }

    val currentAvatar = com.example.data.models.availableChildAvatars.find { it.id == userAccount.selectedAvatarId }
        ?: com.example.data.models.availableChildAvatars.first()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HekayaSurfaceBlue)
                .testTag("settings_screen")
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
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = HekayaDarkBlue
                        )
                    }

                    Text(
                        text = "الرقابة الأبوية والإعدادات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaDarkBlue
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(currentAvatar.backgroundColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentAvatar.emoji,
                                    fontSize = 28.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userAccount.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (userAccount.isPremium) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(HekayaGoldLight)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "مشترك مميز",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF92400E)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${currentAvatar.name} - المستوى ${userAccount.currentLevel}",
                                    fontSize = 12.sp,
                                    color = HekayaTextSecondary
                                )
                            }

                            OutlinedButton(
                                onClick = onEditProfile,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("تخصيص", fontSize = 11.sp, color = HekayaBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Stats Bar with Bookmarked Stories count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HekayaSurfaceBlue)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = HekayaGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${userAccount.favoriteStoryIds.size}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                }
                                Text(
                                    text = "قصص محفوظة",
                                    fontSize = 11.sp,
                                    color = HekayaTextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = HekayaBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${userAccount.storiesReadCount}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                }
                                Text(
                                    text = "قصص مكتملة",
                                    fontSize = 11.sp,
                                    color = HekayaTextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = HekayaBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${userAccount.readingMinutesThisWeek} د",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                }
                                Text(
                                    text = "وقت القراءة",
                                    fontSize = 11.sp,
                                    color = HekayaTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subscription Status Card
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
                            Column {
                                Text(
                                    text = "حالة الاشتراك",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaDarkBlue
                                )
                                Text(
                                    text = if (userAccount.isPremium) "باقة حكاية المميزة (غير محدودة)" else "الباقة المجانية",
                                    fontSize = 12.sp,
                                    color = if (userAccount.isPremium) HekayaGreenText else HekayaTextSecondary
                                )
                            }

                            Button(
                                onClick = {
                                    if (userAccount.isPremium) onTogglePlan() else onUpgradeClick()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (userAccount.isPremium) HekayaLightBlue else HekayaBlue
                                )
                            ) {
                                Text(
                                    text = if (userAccount.isPremium) "تبديل للباقة المجانية" else "ترقية للمميز ✨",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userAccount.isPremium) HekayaBlue else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Security & Privacy Section
                Text(
                    text = "الأمان وحماية المحتوى (StorySafe)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "حماية تصوير الشاشة (DRM)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HekayaDarkBlue
                                )
                                Text(
                                    text = "منع التقاط الشاشة لحماية حقوق النشر والمؤلفين",
                                    fontSize = 11.sp,
                                    color = HekayaTextSecondary
                                )
                            }
                            Switch(
                                checked = screenCaptureBlockEnabled,
                                onCheckedChange = { screenCaptureBlockEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = HekayaBlue
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = HekayaDivider)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onTriggerSecurityAlert,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("اختبار تنبيه الأمان عند التصوير", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Time Limits & Parental Gate
                Text(
                    text = "وقت القراءة والراحة والرقابة الأبوية",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "وضع وقت النوم الهادئ",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HekayaDarkBlue
                                )
                                Text(
                                    text = "تعتيم الإضاءة وتشغيل الموسيقى الهادئة تلقائياً",
                                    fontSize = 11.sp,
                                    color = HekayaTextSecondary
                                )
                            }
                            Switch(
                                checked = bedtimeModeEnabled,
                                onCheckedChange = { bedtimeModeEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = HekayaBlue
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = HekayaDivider)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الحد اليومي للقراءة",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HekayaDarkBlue
                                )
                                Text(
                                    text = "مستهلك اليوم: ${userAccount.todayReadingMinutes} من ${userAccount.dailyReadingLimitMinutes} دقيقة",
                                    fontSize = 11.sp,
                                    color = HekayaTextSecondary
                                )
                            }

                            Text(
                                text = "${userAccount.dailyReadingLimitMinutes} دقيقة",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onOpenParentalOverlay,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("settings_open_parental_overlay_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HekayaLightBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = HekayaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تعديل الحد اليومي وضوابط الأمان",
                                fontSize = 13.sp,
                                color = HekayaBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
