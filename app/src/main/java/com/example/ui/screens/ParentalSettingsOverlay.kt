package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.models.UserAccount
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaBorder
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaDivider
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
fun ParentalSettingsOverlay(
    userAccount: UserAccount,
    onDismiss: () -> Unit,
    onSetDailyReadingLimit: (Int) -> Unit,
    onSetDailyTimeLimitEnabled: (Boolean) -> Unit,
    onSetBedtimeModeEnabled: (Boolean) -> Unit,
    onResetTodayReadingTime: () -> Unit,
    onAddExtraReadingTime: (Int) -> Unit
) {
    // Parental Gate state: ensures only parents can adjust limits
    var isParentUnlocked by remember { mutableStateOf(false) }
    var mathAnswerInput by remember { mutableStateOf("") }
    var hasGateError by remember { mutableStateOf(false) }

    // Math challenge for parental verification (e.g. 7 * 8 = 56 or 6 + 9 = 15)
    val num1 = remember { 7 }
    val num2 = remember { 8 }
    val correctAnswer = remember { num1 * num2 } // 56

    var selectedLimitMinutes by remember { mutableIntStateOf(userAccount.dailyReadingLimitMinutes) }
    var isLimitEnabled by remember { mutableStateOf(userAccount.isDailyTimeLimitEnabled) }
    var isBedtimeEnabled by remember { mutableStateOf(userAccount.isBedtimeModeEnabled) }
    var eyeBreakReminder by remember { mutableStateOf(true) }

    val presetMinutes = listOf(15, 30, 45, 60, 90)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = Modifier.testTag("parental_settings_overlay")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HekayaLightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isParentUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = HekayaBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إعدادات ولي الأمر 🛡️",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaDarkBlue
                            )
                            Text(
                                text = "التحكم في وقت القراءة اليومي وتنبيهات الراحة",
                                fontSize = 12.sp,
                                color = HekayaTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("parental_overlay_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = HekayaDarkBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Parental Gate Challenge (if not yet unlocked)
                if (!isParentUnlocked) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parental_gate_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = HekayaSurfaceBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HekayaBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "بوابة التحقق لأولياء الأمور",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaDarkBlue
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "يرجى حل المسألة البسيطة التالية للتأكد من هوية ولي الأمر:",
                                fontSize = 13.sp,
                                color = HekayaTextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Math Question Box
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White)
                                    .border(1.5.dp, HekayaBlue, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "$num1 × $num2 = ؟",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HekayaBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = mathAnswerInput,
                                onValueChange = {
                                    mathAnswerInput = it
                                    hasGateError = false
                                },
                                placeholder = { Text("اكتب الإجابة هنا...", fontSize = 13.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .testTag("parental_gate_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HekayaBlue,
                                    unfocusedBorderColor = if (hasGateError) Color.Red else HekayaBorder
                                )
                            )

                            if (hasGateError) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "الإجابة غير صحيحة، حاول مجدداً",
                                    fontSize = 12.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (mathAnswerInput.trim() == correctAnswer.toString()) {
                                        isParentUnlocked = true
                                        hasGateError = false
                                    } else {
                                        hasGateError = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(46.dp)
                                    .testTag("parental_gate_unlock_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("فتح لوحة التحكم", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Full Parental Controls Panel
                    // 1. Daily Usage & Progress Tracker
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = HekayaSurfaceBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HekayaBorder)
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
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = HekayaBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "استهلاك اليوم لـ ${userAccount.name}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                }

                                Text(
                                    text = "${userAccount.todayReadingMinutes} من ${selectedLimitMinutes} دقيقة",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userAccount.todayReadingMinutes >= selectedLimitMinutes) Color(0xFFC62828) else HekayaBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val progressFraction = if (selectedLimitMinutes > 0) {
                                (userAccount.todayReadingMinutes.toFloat() / selectedLimitMinutes).coerceIn(0f, 1f)
                            } else 0f

                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = if (userAccount.todayReadingMinutes >= selectedLimitMinutes) {
                                    Color(0xFFE53935)
                                } else if (progressFraction > 0.8f) {
                                    Color(0xFFFB8C00)
                                } else {
                                    HekayaBlue
                                },
                                trackColor = HekayaBorder
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick adjustment buttons (Reset / Add +15m)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onResetTodayReadingTime,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("reset_today_timer_btn"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تصفير العداد", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onAddExtraReadingTime(15) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("add_15_minutes_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+15 دقيقة إضافية", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. Daily Reading Time Limit Configuration
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HekayaBorder)
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "تحديد الحد اليومي للقراءة",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                    Text(
                                        text = "إيقاف القراءة تلقائياً عند استهلاك الوقت المحدد",
                                        fontSize = 11.sp,
                                        color = HekayaTextSecondary
                                    )
                                }

                                Switch(
                                    checked = isLimitEnabled,
                                    onCheckedChange = {
                                        isLimitEnabled = it
                                        onSetDailyTimeLimitEnabled(it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = HekayaBlue
                                    ),
                                    modifier = Modifier.testTag("daily_limit_switch")
                                )
                            }

                            AnimatedVisibility(visible = isLimitEnabled) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = HekayaDivider)
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "الخيارات السريعة:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = HekayaDarkBlue
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Preset chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        presetMinutes.forEach { mins ->
                                            val isSelected = selectedLimitMinutes == mins
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) HekayaBlue else HekayaInputBg)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSelected) HekayaBlue else HekayaBorder,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable {
                                                        selectedLimitMinutes = mins
                                                        onSetDailyReadingLimit(mins)
                                                    }
                                                    .padding(vertical = 10.dp)
                                                    .testTag("time_limit_chip_$mins"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$mins د",
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else HekayaDarkBlue
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Custom Slider
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "تعديل دقيق:",
                                            fontSize = 12.sp,
                                            color = HekayaTextSecondary
                                        )
                                        Text(
                                            text = "$selectedLimitMinutes دقيقة يومياً",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HekayaBlue
                                        )
                                    }

                                    Slider(
                                        value = selectedLimitMinutes.toFloat(),
                                        onValueChange = {
                                            selectedLimitMinutes = it.toInt()
                                        },
                                        onValueChangeFinished = {
                                            onSetDailyReadingLimit(selectedLimitMinutes)
                                        },
                                        valueRange = 10f..120f,
                                        steps = 21,
                                        colors = SliderDefaults.colors(
                                            thumbColor = HekayaBlue,
                                            activeTrackColor = HekayaBlue,
                                            inactiveTrackColor = HekayaBorder
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("time_limit_slider")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Bedtime & Rest Protection Controls
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HekayaBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Bedtime Mode Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEDE7F6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bedtime,
                                            contentDescription = null,
                                            tint = Color(0xFF5E35B1),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "وضع وقت النوم الهادئ",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = HekayaDarkBlue
                                        )
                                        Text(
                                            text = "تعتيم الإضاءة بعد الساعة 8:30 مساءً",
                                            fontSize = 11.sp,
                                            color = HekayaTextSecondary
                                        )
                                    }
                                }

                                Switch(
                                    checked = isBedtimeEnabled,
                                    onCheckedChange = {
                                        isBedtimeEnabled = it
                                        onSetBedtimeModeEnabled(it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = HekayaBlue
                                    ),
                                    modifier = Modifier.testTag("bedtime_switch")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = HekayaDivider)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Eye Rest Reminder Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE8F5E9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "تنبيه راحة العين (20 دقيقة)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = HekayaDarkBlue
                                        )
                                        Text(
                                            text = "تذكير الطفل بأخذ استراحة قصيرة للنظر بعيداً",
                                            fontSize = 11.sp,
                                            color = HekayaTextSecondary
                                        )
                                    }
                                }

                                Switch(
                                    checked = eyeBreakReminder,
                                    onCheckedChange = { eyeBreakReminder = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = HekayaBlue
                                    ),
                                    modifier = Modifier.testTag("eye_break_switch")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Save / Apply Button
                    Button(
                        onClick = {
                            onSetDailyReadingLimit(selectedLimitMinutes)
                            onSetDailyTimeLimitEnabled(isLimitEnabled)
                            onSetBedtimeModeEnabled(isBedtimeEnabled)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_parental_settings_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حفظ وتطبيق الإعدادات",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Friendly dialog shown to the child when the daily reading time limit is reached.
 */
@Composable
fun DailyLimitReachedDialog(
    childName: String,
    onDismiss: () -> Unit,
    onOpenParentalGate: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("daily_limit_reached_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE7F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌙", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "انتهى وقت القراءة لليوم! 🌟",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaDarkBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "أحسنت يا $childName! لقد حققت هدف القراءة اليومي. حان وقت إراحة عينيك أو الاستعداد للنوم.",
                        fontSize = 13.sp,
                        color = HekayaTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("limit_reached_ok_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                    ) {
                        Text("حسناً، سأرتاح الآن", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenParentalGate()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("parent_extend_time_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("طلب تمديد الوقت من ولي الأمر", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
