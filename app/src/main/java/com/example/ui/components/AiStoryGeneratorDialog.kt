package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AiStoryGenerationState
import com.example.data.models.Story
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaGold
import com.example.ui.theme.HekayaGreen
import com.example.ui.theme.HekayaInputBg
import com.example.ui.theme.HekayaLightBlue
import com.example.ui.theme.HekayaSurfaceBlue
import com.example.ui.theme.HekayaTextMuted

private val quickThemes = listOf(
    "مغامرة في قلعة السحاب ☁️",
    "تنين الياقوت والمدينة المفقودة 🐲",
    "سفينة الفضاء والأرنب الفضولي 🚀",
    "أسرار أعماق المحيط المرجاني 🐬",
    "الغابة المسحورة والشجرة المتكلمة 🌳",
    "حديقة الفراشات الذهبية 🦋"
)

private val ageOptions = listOf("3-5 سنوات", "6-8 سنوات", "9-12 سنة")

private val moralLessons = listOf(
    "التعاون والشجاعة",
    "الصدق والأمانة",
    "مساعدة الآخرين",
    "حب الاستكشاف والمعرفة",
    "الصبر والعمل الجماعي"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiStoryGeneratorBottomSheet(
    defaultHeroName: String = "",
    generationState: AiStoryGenerationState,
    onGenerate: (theme: String, heroName: String, companionName: String, ageGroup: String, moralLesson: String) -> Unit,
    onOpenStory: (Story) -> Unit,
    onDismiss: () -> Unit
) {
    var themeText by remember { mutableStateOf("") }
    var heroNameText by remember { mutableStateOf(defaultHeroName.ifBlank { "سارة" }) }
    var companionText by remember { mutableStateOf("الأرنب بندق") }
    var selectedAge by remember { mutableStateOf("6-8 سنوات") }
    var selectedMoral by remember { mutableStateOf("التعاون والشجاعة") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val infiniteTransition = rememberInfiniteTransition(label = "magic_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .testTag("ai_story_generator_sheet"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_ai_generator_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = HekayaDarkBlue
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "مؤلف القصص الذكي",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(HekayaGold, Color(0xFFFFB300)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (generationState is AiStoryGenerationState.Generating) rotationAngle else 0f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(36.dp))
                }

                Text(
                    text = "مدعوم بـ Firebase Genkit AI • أصنع قصة خيالية مخصصة لطفلك في ثوانٍ",
                    fontSize = 12.sp,
                    color = HekayaTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // State content handling
                when (generationState) {
                    is AiStoryGenerationState.Generating -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = HekayaInputBg)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(HekayaBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = HekayaBlue,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = HekayaGold,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .rotate(rotationAngle)
                                    )
                                }

                                Text(
                                    text = "جاري تأليف حكايتك الخيالية... 🪄",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaDarkBlue,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = generationState.stepMessage,
                                    fontSize = 13.sp,
                                    color = HekayaTextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    is AiStoryGenerationState.Success -> {
                        val story = generationState.generatedStory
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HekayaGreen.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "🎉 تم تأليف الحكاية بنجاح!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaGreen
                                )

                                Text(
                                    text = story.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaDarkBlue,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = story.subtitle,
                                    fontSize = 13.sp,
                                    color = HekayaTextMuted,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "عدد الصفحات: ${story.pages.size} صفحات • الفئة: ${story.ageRange}",
                                    fontSize = 12.sp,
                                    color = HekayaDarkBlue
                                )

                                Button(
                                    onClick = { onOpenStory(story) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("open_generated_story_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ابدأ قراءة القصة الآن 📖",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    is AiStoryGenerationState.Error -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                        ) {
                            Text(
                                text = "⚠️ ${generationState.errorMessage}",
                                fontSize = 13.sp,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(14.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        RenderInputForm(
                            themeText = themeText,
                            onThemeChange = { themeText = it },
                            heroNameText = heroNameText,
                            onHeroNameChange = { heroNameText = it },
                            companionText = companionText,
                            onCompanionChange = { companionText = it },
                            selectedAge = selectedAge,
                            onAgeChange = { selectedAge = it },
                            selectedMoral = selectedMoral,
                            onMoralChange = { selectedMoral = it },
                            onSubmit = {
                                onGenerate(themeText, heroNameText, companionText, selectedAge, selectedMoral)
                            }
                        )
                    }

                    AiStoryGenerationState.Idle -> {
                        RenderInputForm(
                            themeText = themeText,
                            onThemeChange = { themeText = it },
                            heroNameText = heroNameText,
                            onHeroNameChange = { heroNameText = it },
                            companionText = companionText,
                            onCompanionChange = { companionText = it },
                            selectedAge = selectedAge,
                            onAgeChange = { selectedAge = it },
                            selectedMoral = selectedMoral,
                            onMoralChange = { selectedMoral = it },
                            onSubmit = {
                                onGenerate(themeText, heroNameText, companionText, selectedAge, selectedMoral)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RenderInputForm(
    themeText: String,
    onThemeChange: (String) -> Unit,
    heroNameText: String,
    onHeroNameChange: (String) -> Unit,
    companionText: String,
    onCompanionChange: (String) -> Unit,
    selectedAge: String,
    onAgeChange: (String) -> Unit,
    selectedMoral: String,
    onMoralChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme Input
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = HekayaGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "موضوع الحكاية أو فكرتها:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue
                )
            }

            OutlinedTextField(
                value = themeText,
                onValueChange = onThemeChange,
                placeholder = { Text("مثال: مغامرة في أرض الحلوى والنجوم...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_theme_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HekayaBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                maxLines = 2
            )

            // Quick Theme Suggestions Chips
            Text(
                text = "أفكار مقترحة سريعة:",
                fontSize = 12.sp,
                color = HekayaTextMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickThemes.forEach { suggestion ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onThemeChange(suggestion) }
                            .testTag("ai_theme_chip_$suggestion"),
                        color = if (themeText == suggestion) HekayaLightBlue else HekayaInputBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (themeText == suggestion) HekayaBlue else Color(0xFFE2E8F0)
                        )
                    ) {
                        Text(
                            text = suggestion,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = HekayaDarkBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Hero Name & Companion Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = HekayaBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "اسم البطل / البطلة:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaDarkBlue
                    )
                }

                OutlinedTextField(
                    value = heroNameText,
                    onValueChange = onHeroNameChange,
                    placeholder = { Text("مثال: سارة") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_hero_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HekayaBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = HekayaGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "الرفيق المساعد (اختياري):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaDarkBlue
                    )
                }

                OutlinedTextField(
                    value = companionText,
                    onValueChange = onCompanionChange,
                    placeholder = { Text("مثال: الأرنب بندق") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_companion_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HekayaBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )
            }
        }

        // Age Group Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "الفئة العمرية المناسبة:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = HekayaDarkBlue
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ageOptions.forEach { age ->
                    val isSelected = selectedAge == age
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onAgeChange(age) }
                            .testTag("ai_age_chip_$age"),
                        color = if (isSelected) HekayaBlue else HekayaInputBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) HekayaBlue else Color(0xFFE2E8F0)
                        )
                    ) {
                        Text(
                            text = age,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else HekayaDarkBlue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Moral Values Selection
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "القيمة الأخلاقية أو الدرس المستفاد:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = HekayaDarkBlue
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                moralLessons.forEach { moral ->
                    val isSelected = selectedMoral == moral
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onMoralChange(moral) }
                            .testTag("ai_moral_chip_$moral"),
                        color = if (isSelected) HekayaLightBlue else HekayaInputBg,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) HekayaBlue else Color(0xFFE2E8F0)
                        )
                    ) {
                        Text(
                            text = moral,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) HekayaDarkBlue else HekayaTextMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Generate Action Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_ai_story_submit_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HekayaGold,
                contentColor = HekayaDarkBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = HekayaDarkBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "تأليف الحكاية بالذكاء الاصطناعي 🪄✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = HekayaDarkBlue
            )
        }
    }
}
