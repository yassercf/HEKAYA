package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaBorder
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaGold
import com.example.ui.theme.HekayaGreen
import com.example.ui.theme.HekayaGreenBadge
import com.example.ui.theme.HekayaGreenText
import com.example.ui.theme.HekayaLightBlue
import com.example.ui.theme.HekayaSurfaceBlue
import com.example.ui.theme.HekayaTextMuted
import com.example.ui.theme.HekayaTextPrimary
import com.example.ui.theme.HekayaTextSecondary

@Composable
fun SubscriptionScreen(
    isCurrentPremium: Boolean,
    onUpgrade: () -> Unit,
    onBack: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HekayaSurfaceBlue)
                .testTag("subscription_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header matching Image 7
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("sub_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = HekayaDarkBlue
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hekaya",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = HekayaBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hero Title
                Text(
                    text = "ارتقِ بتجربة طفلك",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HekayaDarkBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "افتح عالماً كاملاً من القصص التفاعلية اللانهائية والخالية تماماً من الإعلانات بمحتوى تعليمي هادف وآمن.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = HekayaTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Free Plan Card matching Image 7
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "المستخدم المجاني",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaDarkBlue
                                )
                                Text(
                                    text = "مجاني دائماً",
                                    fontSize = 13.sp,
                                    color = HekayaTextSecondary
                                )
                            }

                            if (!isCurrentPremium) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(HekayaLightBlue)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "خطتك الحالية",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaBlue
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PlanFeatureRow(included = true, text = "قراءة حتى 5 قصص شهرياً")
                        PlanFeatureRow(included = true, text = "الوصول للتصنيفات الأساسية")
                        PlanFeatureRow(included = false, text = "قراءة صوتية ذكية غير محدودة")
                        PlanFeatureRow(included = false, text = "تحميل القصص بدون إنترنت")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Premium Plan Card with "Recommended" badge matching Image 7
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(2.dp, HekayaBlue, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "المشترك المميز",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HekayaDarkBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = HekayaGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HekayaGreenBadge)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "نوصي به",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaGreenText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "29 ريال",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HekayaBlue
                            )
                            Text(
                                text = " / شهرياً",
                                fontSize = 13.sp,
                                color = HekayaTextSecondary,
                                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PlanFeatureRow(included = true, text = "وصول غير محدود لجميع القصص والمغامرات")
                        PlanFeatureRow(included = true, text = "قراءة صوتية ذكية باللهجة العربية الفصحى")
                        PlanFeatureRow(included = true, text = "تنزيل وقراءة أوفلاين بدون إنترنت")
                        PlanFeatureRow(included = true, text = "لوحة تحكم وتحليلات ولي الأمر المتقدمة")
                        PlanFeatureRow(included = true, text = "محتوى خالٍ تماماً من أي إعلانات")

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onUpgrade,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("subscribe_action_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isCurrentPremium) "أنت مشترك بالفعل ✓" else "✨ اشترك الآن",
                                    fontSize = 16.sp,
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
}

@Composable
private fun PlanFeatureRow(included: Boolean, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (included) HekayaGreenBadge else Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (included) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (included) HekayaGreen else HekayaTextMuted,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = if (included) HekayaTextPrimary else HekayaTextMuted
        )
    }
}
