package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.ActivityItem
import com.example.data.models.NewStoryDraft
import com.example.ui.theme.HekayaBadgeBg
import com.example.ui.theme.HekayaBadgeText
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaBorder
import com.example.ui.theme.HekayaCoral
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
import com.example.ui.viewmodel.AdminTab

@Composable
fun AdminDashboardScreen(
    currentTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    draft: NewStoryDraft,
    onUpdateTitle: (String) -> Unit,
    onUpdateAge: (String) -> Unit,
    onUpdatePremium: (Boolean) -> Unit,
    onToggleTag: (String) -> Unit,
    onUpdatePageText: (Int, String) -> Unit,
    onAddPage: () -> Unit,
    uploadProgress: Float?,
    onUploadClick: () -> Unit,
    onPublishClick: () -> Unit,
    isPublishSuccess: Boolean,
    activities: List<ActivityItem>,
    onBack: () -> Unit
) {
    val tabs = listOf(
        AdminTab.UPLOAD to "رفع قصة",
        AdminTab.CATEGORIZE to "تصنيف",
        AdminTab.REVIEW_PUBLISH to "مراجعة ونشر",
        AdminTab.ANALYTICS to "التحليلات"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HekayaSurfaceBlue)
                .testTag("admin_dashboard_screen")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar matching Images 11, 13, 15, 17
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
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع للمكتبة",
                            tint = HekayaDarkBlue
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "StorySafe Admin",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HekayaDarkBlue
                        )
                        Text(
                            text = "لوحة إدارة ومؤلفي حكاية",
                            fontSize = 11.sp,
                            color = HekayaTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(HekayaLightBlue)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "المؤلف",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaBlue
                        )
                    }
                }

                // Tab Bar
                ScrollableTabRow(
                    selectedTabIndex = tabs.indexOfFirst { it.first == currentTab },
                    containerColor = Color.White,
                    contentColor = HekayaBlue,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        val index = tabs.indexOfFirst { it.first == currentTab }
                        if (index in tabPositions.indices) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = HekayaBlue,
                                height = 3.dp
                            )
                        }
                    },
                    divider = { HorizontalDivider(color = HekayaDivider) }
                ) {
                    tabs.forEach { (tab, title) ->
                        val isSelected = currentTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { onTabSelected(tab) },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) HekayaBlue else HekayaTextSecondary
                                )
                            },
                            modifier = Modifier.testTag("admin_tab_${tab.name}")
                        )
                    }
                }

                // Tab Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (currentTab) {
                        AdminTab.UPLOAD -> AdminUploadTab(
                            draft = draft,
                            onUpdateTitle = onUpdateTitle,
                            onUpdatePageText = onUpdatePageText,
                            onAddPage = onAddPage,
                            uploadProgress = uploadProgress,
                            onUploadClick = onUploadClick,
                            onNext = { onTabSelected(AdminTab.CATEGORIZE) }
                        )

                        AdminTab.CATEGORIZE -> AdminCategorizeTab(
                            draft = draft,
                            onUpdateAge = onUpdateAge,
                            onUpdatePremium = onUpdatePremium,
                            onToggleTag = onToggleTag,
                            onNext = { onTabSelected(AdminTab.REVIEW_PUBLISH) }
                        )

                        AdminTab.REVIEW_PUBLISH -> AdminReviewPublishTab(
                            draft = draft,
                            onUpdatePageText = onUpdatePageText,
                            onPublishClick = onPublishClick,
                            isPublishSuccess = isPublishSuccess
                        )

                        AdminTab.ANALYTICS -> AdminAnalyticsTab(activities = activities)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: UPLOAD (.docx) & Extract Pages (Image 11)
// -------------------------------------------------------------
@Composable
private fun AdminUploadTab(
    draft: NewStoryDraft,
    onUpdateTitle: (String) -> Unit,
    onUpdatePageText: (Int, String) -> Unit,
    onAddPage: () -> Unit,
    uploadProgress: Float?,
    onUploadClick: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "رفع قصة جديدة (.docx)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HekayaDarkBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "يقوم النظام الذكي بتحليل الملف وتقسيمه تلقائياً إلى صفحات مصورة",
            fontSize = 12.sp,
            color = HekayaTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Upload Dropzone Card matching Image 11
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUploadClick() }
                .border(2.dp, HekayaBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(HekayaLightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "رفع ملف",
                        tint = HekayaBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "انقر هنا لاختيار ملف القصة (.docx / .pdf)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "الملف المرفوع حالياً: Nora_Enchanted_Forest.docx",
                    fontSize = 12.sp,
                    color = HekayaBlue
                )

                if (uploadProgress != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HekayaBlue,
                        trackColor = HekayaLightBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Analysis Success Banner matching Image 11
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = HekayaGreenBadge)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = HekayaGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "تم تحليل الملف واستخراج ${draft.pages.size} صفحات بنجاح ✓",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaGreenText
                    )
                    Text(
                        text = "يمكنك مراجعة وتعديل نصوص ورسومات الصفحات بالأسفل",
                        fontSize = 11.sp,
                        color = HekayaGreenText.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Title Input
        Text(
            text = "عنوان القصة",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = HekayaDarkBlue
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = draft.title,
            onValueChange = onUpdateTitle,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = HekayaBlue,
                unfocusedBorderColor = HekayaBorder
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Extracted Pages Editor List matching Image 11
        Text(
            text = "صفحات القصة المستخرجة",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = HekayaDarkBlue
        )
        Spacer(modifier = Modifier.height(10.dp))

        draft.pages.forEach { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الصفحة ${page.pageNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = page.text,
                            onValueChange = { onUpdatePageText(page.pageNumber, it) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(8.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = HekayaInputBg,
                                unfocusedContainerColor = HekayaInputBg,
                                focusedBorderColor = HekayaBlue,
                                unfocusedBorderColor = HekayaBorder
                            )
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onAddPage,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("+ إضافة صفحة يدوياً")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
        ) {
            Text("متابعة إلى التصنيف ←", fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// TAB 2: CATEGORIZE & TAGS (Image 13)
// -------------------------------------------------------------
@Composable
private fun AdminCategorizeTab(
    draft: NewStoryDraft,
    onUpdateAge: (String) -> Unit,
    onUpdatePremium: (Boolean) -> Unit,
    onToggleTag: (String) -> Unit,
    onNext: () -> Unit
) {
    val ageOptions = listOf("3-5 سنوات", "6-8 سنوات", "9-12 سنة")
    val availableTags = listOf("خيال", "حيوانات", "صداقة", "مغامرة", "تعليمي", "ما قبل النوم", "فضاء", "تاريخ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "تصنيف القصة وتحديد الجمهور",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HekayaDarkBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "حدد الفئة العمرية ونوع المحتوى لتسهيل اكتشافه من قبل الأطفال والأهالي",
            fontSize = 12.sp,
            color = HekayaTextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Age Options Card matching Image 13
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "الفئة العمرية المستهدفة",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ageOptions.forEach { age ->
                        val isSelected = draft.targetAge == age
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) HekayaBlue else HekayaInputBg)
                                .border(1.dp, if (isSelected) HekayaBlue else HekayaBorder, RoundedCornerShape(12.dp))
                                .clickable { onUpdateAge(age) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = age,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else HekayaTextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Premium Switch Card matching Image 13
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "محتوى مميز (Premium)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = HekayaGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "متاح حصرياً للمشتركين في الباقة المميزة",
                        fontSize = 11.sp,
                        color = HekayaTextSecondary
                    )
                }

                Switch(
                    checked = draft.isPremium,
                    onCheckedChange = onUpdatePremium,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = HekayaBlue
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tags Selection Card matching Image 13
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "وسوم وتصنيفات القصة",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Tags Wrapping Simulation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTags.take(4).forEach { tag ->
                        TagChip(
                            tag = tag,
                            isSelected = draft.selectedTags.contains(tag),
                            onClick = { onToggleTag(tag) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTags.drop(4).forEach { tag ->
                        TagChip(
                            tag = tag,
                            isSelected = draft.selectedTags.contains(tag),
                            onClick = { onToggleTag(tag) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
        ) {
            Text("متابعة إلى المراجعة والنشر ←", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TagChip(tag: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) HekayaBlue else HekayaLightBlue)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = tag,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else HekayaBlue
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: REVIEW & LIVE PHONE SIMULATOR (Image 15)
// -------------------------------------------------------------
@Composable
private fun AdminReviewPublishTab(
    draft: NewStoryDraft,
    onUpdatePageText: (Int, String) -> Unit,
    onPublishClick: () -> Unit,
    isPublishSuccess: Boolean
) {
    var previewPageIdx by remember { mutableStateOf(0) }
    val pages = draft.pages
    val currentPage = pages.getOrNull(previewPageIdx) ?: pages.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "معاينة تفاعلية ونشر القصة",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HekayaDarkBlue,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "هكذا ستبدو القصة للطفل على شاشة الهاتف الذكي",
            fontSize = 12.sp,
            color = HekayaTextSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Simulator Container matching Image 15
        Card(
            modifier = Modifier
                .width(290.dp)
                .height(440.dp)
                .border(6.dp, Color(0xFF1E293B), RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (currentPage != null) {
                    Image(
                        painter = painterResource(id = currentPage.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Top Notch Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                        .size(width = 80.dp, height = 12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                )

                // Simulated Floating Card inside simulator
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الراوي الذكي",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaBlue
                            )
                            Text(
                                text = "صفحة ${previewPageIdx + 1} من ${pages.size}",
                                fontSize = 10.sp,
                                color = HekayaTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = currentPage?.text ?: "",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = HekayaDarkBlue,
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "◄ السابق",
                                fontSize = 10.sp,
                                color = if (previewPageIdx > 0) HekayaBlue else Color.LightGray,
                                modifier = Modifier.clickable {
                                    if (previewPageIdx > 0) previewPageIdx--
                                }
                            )
                            Text(
                                text = "التالي ►",
                                fontSize = 10.sp,
                                color = if (previewPageIdx < pages.size - 1) HekayaBlue else Color.LightGray,
                                modifier = Modifier.clickable {
                                    if (previewPageIdx < pages.size - 1) previewPageIdx++
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Success Alert if Published
        if (isPublishSuccess) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = HekayaGreenBadge)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = HekayaGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تم نشر القصة بنجاح وإضافتها لمكتبة الأطفال! ✓",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaGreenText
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Action Buttons
        Button(
            onClick = onPublishClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admin_publish_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HekayaBlue)
        ) {
            Icon(imageVector = Icons.Default.Publish, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "نشر القصة للجمهور الآن",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("حفظ كمسودة خاصة", color = HekayaTextSecondary)
        }
    }
}

// -------------------------------------------------------------
// TAB 4: ANALYTICS OVERVIEW (StorySafe Admin - Images 17, 19, 21)
// -------------------------------------------------------------
@Composable
private fun AdminAnalyticsTab(activities: List<ActivityItem>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "StorySafe Admin - Analytics Overview",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HekayaDarkBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "إحصائيات تفاعل الأطفال ونمو الاشتراكات والمحتوى الأكثر قراءة",
            fontSize = 12.sp,
            color = HekayaTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4 KPI Cards matching Images 17, 19, 21
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMetricCard(
                modifier = Modifier.weight(1f),
                title = "إجمالي القراء",
                value = "24,592",
                subText = "+14% هذا الشهر",
                isPositive = true,
                icon = Icons.Default.People,
                iconColor = HekayaBlue
            )
            KpiMetricCard(
                modifier = Modifier.weight(1f),
                title = "المشتركون المميزون",
                value = "8,140",
                subText = "+22% نمو المشتركين",
                isPositive = true,
                icon = Icons.Default.Star,
                iconColor = HekayaGold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMetricCard(
                modifier = Modifier.weight(1f),
                title = "القصص المنشورة",
                value = "1,204",
                subText = "+8 قصص جديدة",
                isPositive = true,
                icon = Icons.Default.Category,
                iconColor = HekayaGreen
            )
            KpiMetricCard(
                modifier = Modifier.weight(1f),
                title = "متوسط وقت القراءة",
                value = "18 دقيقة",
                subText = "لكل جلسة طفل",
                isPositive = true,
                icon = Icons.Default.Timer,
                iconColor = HekayaBlue
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subscription Growth Bar Chart matching Images 17, 19
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "نمو الاشتراكات الشهرية (2024)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Custom Compose Bar Chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val months = listOf("يناير" to 0.35f, "فبراير" to 0.45f, "مارس" to 0.60f, "أبريل" to 0.75f, "مايو" to 0.88f, "يونيو" to 1.0f)
                    months.forEach { (m, heightFrac) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .fillMaxHeight(heightFrac)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(if (heightFrac == 1.0f) HekayaBlue else HekayaLightBlue)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = m,
                                fontSize = 10.sp,
                                color = HekayaTextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Popular Categories Card matching Image 21
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "التصنيفات الأكثر طلباً",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                CategoryProgressItem(name = "مغامرة وخيال", percent = 45, color = HekayaBlue)
                Spacer(modifier = Modifier.height(8.dp))
                CategoryProgressItem(name = "قصص قبل النوم", percent = 30, color = HekayaGold)
                Spacer(modifier = Modifier.height(8.dp))
                CategoryProgressItem(name = "تعليمي وعلوم", percent = 25, color = HekayaGreen)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Activities List matching Image 21
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "النشاطات والتحديثات الأخيرة",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue
                )
                Spacer(modifier = Modifier.height(10.dp))

                activities.forEach { act ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(HekayaLightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (act.iconType == "star") Icons.Default.Star else Icons.Default.People,
                                contentDescription = null,
                                tint = if (act.iconType == "star") HekayaGold else HekayaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = act.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HekayaDarkBlue
                            )
                            Text(
                                text = act.timeAgo,
                                fontSize = 10.sp,
                                color = HekayaTextMuted
                            )
                        }
                    }
                    if (act != activities.last()) {
                        HorizontalDivider(color = HekayaDivider)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subText: String,
    isPositive: Boolean,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = HekayaTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HekayaDarkBlue
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) HekayaGreenText else HekayaCoral
            )
        }
    }
}

@Composable
private fun CategoryProgressItem(name: String, percent: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = HekayaDarkBlue)
            Text(text = "$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = HekayaLightBlue,
            strokeCap = StrokeCap.Round
        )
    }
}
