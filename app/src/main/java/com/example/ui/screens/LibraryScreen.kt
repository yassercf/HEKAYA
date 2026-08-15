package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.AiStoryGenerationState
import com.example.data.models.Story
import com.example.data.models.StoryBookmark
import com.example.data.models.UserAccount
import com.example.ui.components.AiStoryGeneratorBottomSheet
import com.example.ui.theme.HekayaBadgeBg
import com.example.ui.theme.HekayaBadgeText
import com.example.ui.theme.HekayaBlue
import com.example.ui.theme.HekayaBorder
import com.example.ui.theme.HekayaDarkBlue
import com.example.ui.theme.HekayaGold
import com.example.ui.theme.HekayaGoldLight
import com.example.ui.theme.HekayaInputBg
import com.example.ui.theme.HekayaLightBlue
import com.example.ui.theme.HekayaSurfaceBlue
import com.example.ui.theme.HekayaTextMuted
import com.example.ui.theme.HekayaTextPrimary
import com.example.ui.theme.HekayaTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    stories: List<Story>,
    userAccount: UserAccount,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    selectedAgeFilter: String?,
    onAgeFilterSelect: (String?) -> Unit,
    favoriteStoryIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onStoryClick: (Story) -> Unit,
    onResumeStory: (Story) -> Unit = {},
    onOpenStoryWithPage: (Story, Int) -> Unit = { _, _ -> },
    aiGenerationState: AiStoryGenerationState = AiStoryGenerationState.Idle,
    onGenerateAiStory: (theme: String, heroName: String, companionName: String, ageGroup: String, moralLesson: String) -> Unit = { _, _, _, _, _ -> },
    onOpenGeneratedStory: (Story) -> Unit = {},
    onClearAiGenerationState: () -> Unit = {},
    onOpenProfile: () -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenParentalOverlay: () -> Unit = {},
    onTriggerSecurityAlert: () -> Unit
) {
    val currentAvatar = com.example.data.models.availableChildAvatars.find { it.id == userAccount.selectedAvatarId }
        ?: com.example.data.models.availableChildAvatars.first()

    // Rich Categories metadata with icons, labels and descriptions
    data class CategoryInfo(
        val key: String,
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val color: Color,
        val bgLight: Color,
        val description: String
    )

    val genreCategories = listOf(
        CategoryInfo("الكل", "الكل", Icons.Default.Explore, HekayaBlue, HekayaLightBlue, "جميع القصص المتنوعة"),
        CategoryInfo("المحفوظة ⭐", "المحفوظات", Icons.Default.Bookmark, HekayaGold, HekayaGoldLight, "قصصك المفضلة"),
        CategoryInfo("مغامرة", "مغامرة", Icons.Default.RocketLaunch, Color(0xFFE65100), Color(0xFFFFF3E0), "رحلات شيقة واستكشاف"),
        CategoryInfo("قبل النوم", "قبل النوم", Icons.Default.NightsStay, Color(0xFF4527A0), Color(0xFFEDE7F6), "قصص هادئة ومريحة"),
        CategoryInfo("خيالي", "خيالي وأساطير", Icons.Default.AutoAwesome, Color(0xFF7B1FA2), Color(0xFFF3E5F5), "عوالم سحرية وخيال بديع"),
        CategoryInfo("تعليمي", "تعليمي وعلوم", Icons.Default.Psychology, Color(0xFF00695C), Color(0xFFE0F2F1), "معارف واكتشافات مفيدة"),
        CategoryInfo("حيوانات", "حيوانات", Icons.Default.Pets, Color(0xFF2E7D32), Color(0xFFE8F5E9), "قصص الحيوانات اللطيفة"),
        CategoryInfo("صداقة", "صداقة وقيم", Icons.Default.Favorite, Color(0xFFC2185B), Color(0xFFFCE4EC), "التعاون والمحبة والوفاء")
    )
    val ageCategories = listOf("3-5 سنوات", "6-8 سنوات", "9-12 سنة")
    var showFilterSheet by remember { mutableStateOf(false) }
    var showAiGeneratorSheet by remember { mutableStateOf(false) }
    var currentNavTab by remember { mutableStateOf("library") }
    var savedSubTab by remember { mutableStateOf("all") } // "all" (favorites) or "bookmarks" (saved positions)

    val savedStoriesCount = stories.count { favoriteStoryIds.contains(it.id) }
    val bookmarkedStoriesCount = stories.count { userAccount.bookmarks.containsKey(it.id) }

    // Find latest story in progress or with bookmark for the Continue Reading Hero Card
    val continueReadingStory = remember(stories, userAccount.bookmarks, userAccount.readingProgress) {
        // Priority 1: Story with explicit bookmark
        val bookmarkedStory = userAccount.bookmarks.entries.maxByOrNull { it.value.savedAtTimestamp }?.let { entry ->
            stories.find { it.id == entry.key }
        }
        if (bookmarkedStory != null) {
            bookmarkedStory
        } else {
            // Priority 2: Story with reading progress > 0
            val inProgressStoryEntry = userAccount.readingProgress.entries.firstOrNull { it.value > 0 }
            inProgressStoryEntry?.let { entry -> stories.find { it.id == entry.key } }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("library_screen"),
            containerColor = HekayaSurfaceBlue,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 8.dp)
                ) {
                    // Header Bar matching Image 9
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand with security lock
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HekayaBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_hekaya_logo),
                                    contentDescription = "Hekaya",
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hekaya",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HekayaDarkBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "قفل أمان الأطفال",
                                tint = HekayaBlue,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onTriggerSecurityAlert() }
                            )
                        }

                        // Right Action Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Child Avatar Quick Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(currentAvatar.backgroundColorHex))
                                    .border(1.5.dp, HekayaBlue, RoundedCornerShape(20.dp))
                                    .clickable { onOpenProfile() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("top_bar_avatar_chip"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = currentAvatar.emoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = userAccount.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // AI Story Generator Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(HekayaGoldLight, Color(0xFFFEF3C7))
                                        )
                                    )
                                    .border(1.dp, HekayaGold, RoundedCornerShape(20.dp))
                                    .clickable { showAiGeneratorSheet = true }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .testTag("open_ai_generator_top_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تأليف AI ✨",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Premium Badge Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(HekayaGoldLight)
                                    .clickable { onOpenSubscription() }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = HekayaGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "المميز",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Parental Controls Quick Button
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(HekayaLightBlue)
                                    .clickable { onOpenParentalOverlay() }
                                    .padding(6.dp)
                                    .testTag("top_bar_parental_limit_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "الرقابة الأبوية والوقت",
                                    tint = HekayaBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = onOpenAdmin,
                                modifier = Modifier.testTag("open_admin_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "لوحة التحكم",
                                    tint = HekayaDarkBlue
                                )
                            }
                        }
                    }

                    // Search and Filter Bar (only show if not on profile/settings)
                    if (currentNavTab != "saved") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("search_story_input"),
                                placeholder = {
                                    Text(
                                        text = "ابحث عن قصة، موضوع، أو شخصية...",
                                        fontSize = 13.sp,
                                        color = HekayaTextMuted
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "بحث",
                                        tint = HekayaTextSecondary
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = HekayaInputBg,
                                    unfocusedContainerColor = HekayaInputBg,
                                    focusedBorderColor = HekayaBlue,
                                    unfocusedBorderColor = HekayaBorder
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selectedAgeFilter != null) HekayaLightBlue else HekayaInputBg)
                                    .border(1.dp, if (selectedAgeFilter != null) HekayaBlue else HekayaBorder, RoundedCornerShape(14.dp))
                                    .clickable { showFilterSheet = true }
                                    .testTag("filter_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "فلاتر",
                                    tint = if (selectedAgeFilter != null) HekayaBlue else HekayaDarkBlue
                                )
                            }
                        }

                        // Horizontal Categories Bar with rich genres & icons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            genreCategories.forEach { categoryInfo ->
                                val isSelected = selectedCategory == categoryInfo.key
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) categoryInfo.color else HekayaInputBg)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) categoryInfo.color else HekayaBorder,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onCategorySelect(categoryInfo.key) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("category_chip_${categoryInfo.key}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = categoryInfo.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else categoryInfo.color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (categoryInfo.key.contains("المحفوظة") && savedStoriesCount > 0) {
                                                "${categoryInfo.label} ($savedStoriesCount)"
                                            } else {
                                                categoryInfo.label
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else HekayaTextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Age Filter Chips if selected
                        if (selectedAgeFilter != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الفئة العمرية: $selectedAgeFilter",
                                    fontSize = 12.sp,
                                    color = HekayaBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "إلغاء التصفية ✕",
                                    fontSize = 12.sp,
                                    color = HekayaTextSecondary,
                                    modifier = Modifier.clickable { onAgeFilterSelect(null) }
                                )
                            }
                        }
                    } else {
                        // Header banner for Saved Stories tab with sub-filters (Favorites vs Saved Bookmarks)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "القصص المحفوظة والعلامات 🔖",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaDarkBlue
                                    )
                                    Text(
                                        text = "مجموعتك الخاصة وعلامات متابعة القراءة",
                                        fontSize = 12.sp,
                                        color = HekayaTextSecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(HekayaLightBlue)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (savedSubTab == "bookmarks") "$bookmarkedStoriesCount علامات" else "$savedStoriesCount قصة",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HekayaBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Sub-filter tabs for Saved
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = savedSubTab == "all",
                                    onClick = { savedSubTab = "all" },
                                    label = { Text("المفضلة ⭐ ($savedStoriesCount)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = HekayaBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = savedSubTab == "bookmarks",
                                    onClick = { savedSubTab = "bookmarks" },
                                    label = { Text("علامات التوقف 🔖 ($bookmarkedStoriesCount)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = HekayaGold,
                                        selectedLabelColor = HekayaDarkBlue
                                    )
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom Navigation Bar with Library, Saved Stories, Profile, Settings
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentNavTab == "library",
                        onClick = { currentNavTab = "library" },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "استكشاف القصص"
                            )
                        },
                        label = { Text("القصص", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HekayaBlue,
                            selectedTextColor = HekayaBlue,
                            indicatorColor = HekayaLightBlue
                        ),
                        modifier = Modifier.testTag("nav_tab_library")
                    )

                    NavigationBarItem(
                        selected = currentNavTab == "saved",
                        onClick = { currentNavTab = "saved" },
                        icon = {
                            Icon(
                                imageVector = if (currentNavTab == "saved") Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "القصص المحفوظة"
                            )
                        },
                        label = {
                            Text(
                                text = if (savedStoriesCount > 0) "المحفوظة ($savedStoriesCount)" else "المحفوظة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HekayaBlue,
                            selectedTextColor = HekayaBlue,
                            indicatorColor = HekayaLightBlue
                        ),
                        modifier = Modifier.testTag("nav_tab_saved")
                    )

                    NavigationBarItem(
                        selected = currentNavTab == "profile",
                        onClick = {
                            currentNavTab = "profile"
                            onOpenProfile()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "ملف القارئ"
                            )
                        },
                        label = { Text("ملفي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HekayaBlue,
                            selectedTextColor = HekayaBlue,
                            indicatorColor = HekayaLightBlue
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )

                    NavigationBarItem(
                        selected = currentNavTab == "settings",
                        onClick = {
                            currentNavTab = "settings"
                            onOpenSettings()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "الإعدادات"
                            )
                        },
                        label = { Text("الإعدادات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HekayaBlue,
                            selectedTextColor = HekayaBlue,
                            indicatorColor = HekayaLightBlue
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        ) { paddingValues ->
            val displayedStories = when {
                currentNavTab == "saved" && savedSubTab == "bookmarks" -> {
                    stories.filter { userAccount.bookmarks.containsKey(it.id) }
                }
                currentNavTab == "saved" -> {
                    stories.filter { favoriteStoryIds.contains(it.id) }
                }
                else -> {
                    stories
                }
            }

            if (displayedStories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(HekayaLightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentNavTab == "saved") Icons.Default.BookmarkBorder else Icons.Default.Search,
                                contentDescription = null,
                                tint = HekayaBlue,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (currentNavTab == "saved") {
                                if (savedSubTab == "bookmarks") "لا توجد علامات قراءة محفوظة بعد" else "لا توجد قصص محفوظة بعد"
                            } else "لم نجد قصصاً مطابقة للبحث",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (currentNavTab == "saved") {
                                if (savedSubTab == "bookmarks") {
                                    "أثناء قراءة أي قصة، اضغط على أيقونة الإشارة المرجعية 🔖 لحفظ موضع الصفحة والعودة إليها لاحقاً بسهولة!"
                                } else {
                                    "اضغط على أيقونة الإشارة المرجعية 🔖 بجانب أي قصة لحفظها في مكتبتك المفضلة!"
                                }
                            } else {
                                "جرّب البحث بكلمات أخرى أو اختر تصنيفاً مختلفاً"
                            },
                            fontSize = 13.sp,
                            color = HekayaTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        if (currentNavTab == "saved") {
                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.Button(
                                onClick = { currentNavTab = "library" },
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = HekayaBlue
                                )
                            ) {
                                Text("تصفح واستكشف القصص الآن", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // 2-Column Grid of Stories
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Continue Reading Hero Banner
                    if (currentNavTab == "library" && searchQuery.isEmpty() && selectedAgeFilter == null && selectedCategory == "الكل" && continueReadingStory != null) {
                        val bookmarkedPage = userAccount.bookmarks[continueReadingStory.id]?.pageIndex
                        val progressPage = userAccount.readingProgress[continueReadingStory.id] ?: 0
                        val resumeTargetPage = bookmarkedPage ?: progressPage

                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                            ContinueReadingHeroCard(
                                story = continueReadingStory,
                                targetPageIndex = resumeTargetPage,
                                isExplicitBookmark = bookmarkedPage != null,
                                onResumeClick = { onResumeStory(continueReadingStory) }
                            )
                        }
                    }

                    items(displayedStories, key = { it.id }) { story ->
                        val isFav = favoriteStoryIds.contains(story.id)
                        val ratingId = userAccount.storyRatings[story.id]
                        val bookmark = userAccount.bookmarks[story.id]
                        val progress = userAccount.readingProgress[story.id]

                        StoryCard(
                            story = story,
                            isFavorite = isFav,
                            userRatingId = ratingId,
                            savedBookmark = bookmark,
                            savedProgressPage = progress,
                            onToggleFavorite = { onToggleFavorite(story.id) },
                            onClick = { onStoryClick(story) },
                            onResume = { onResumeStory(story) }
                        )
                    }
                }
            }

            // Bottom Filter & Categorization Sheet
            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تصنيفات وفلاتر القصص 📚",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaDarkBlue
                            )
                            if (selectedAgeFilter != null || selectedCategory != "الكل") {
                                Text(
                                    text = "إعادة ضبط",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HekayaBlue,
                                    modifier = Modifier.clickable {
                                        onCategorySelect("الكل")
                                        onAgeFilterSelect(null)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Genre Categories
                        Text(
                            text = "اختر نوع القصة (النوع الأدبي):",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(genreCategories, key = { it.key }) { categoryInfo ->
                                val isSelected = selectedCategory == categoryInfo.key
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) categoryInfo.bgLight else HekayaInputBg)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) categoryInfo.color else HekayaBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            onCategorySelect(categoryInfo.key)
                                        }
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) categoryInfo.color else Color.White),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = categoryInfo.icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else categoryInfo.color,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = categoryInfo.label,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) categoryInfo.color else HekayaDarkBlue
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = categoryInfo.description,
                                            fontSize = 10.sp,
                                            color = HekayaTextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Age Filter Section
                        Text(
                            text = "الفئة العمرية المناسبة:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ageCategories.forEach { age ->
                                val isSelected = selectedAgeFilter == age
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) HekayaLightBlue else HekayaInputBg)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) HekayaBlue else HekayaBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            onAgeFilterSelect(if (isSelected) null else age)
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = age,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) HekayaBlue else HekayaTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        androidx.compose.material3.Button(
                            onClick = { showFilterSheet = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = HekayaBlue
                            )
                        ) {
                            Text("تطبيق التصنيفات والبحث", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // AI Fairy Tale Generator Bottom Sheet
            if (showAiGeneratorSheet || aiGenerationState !is AiStoryGenerationState.Idle) {
                AiStoryGeneratorBottomSheet(
                    defaultHeroName = userAccount.name,
                    generationState = aiGenerationState,
                    onGenerate = { theme, hero, companion, age, moral ->
                        onGenerateAiStory(theme, hero, companion, age, moral)
                    },
                    onOpenStory = { story ->
                        showAiGeneratorSheet = false
                        onOpenGeneratedStory(story)
                    },
                    onDismiss = {
                        showAiGeneratorSheet = false
                        onClearAiGenerationState()
                    }
                )
            }
        }
    }
}

@Composable
fun StoryCard(
    story: Story,
    isFavorite: Boolean,
    userRatingId: String? = null,
    savedBookmark: StoryBookmark? = null,
    savedProgressPage: Int? = null,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    onResume: (() -> Unit)? = null
) {
    val ratingOption = com.example.data.models.availableChildRatings.find { it.id == userRatingId }
    val resumePageIndex = savedBookmark?.pageIndex ?: savedProgressPage

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("story_card_${story.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Story Cover Container with Age Badge & Lock/Star Icon & Child Emoji Rating Badge & Bookmark ribbon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = story.coverRes),
                    contentDescription = story.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Age Pill Badge on top left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HekayaBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = story.ageRange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HekayaBadgeText
                    )
                }

                // Saved Bookmark Ribbon Badge
                if (savedBookmark != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(HekayaGold)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = HekayaDarkBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "علامة ص ${savedBookmark.pageIndex + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HekayaDarkBlue
                            )
                        }
                    }
                }

                // Child emoji rating badge if rated
                if (ratingOption != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.92f))
                            .border(1.dp, Color(ratingOption.colorHex), RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = ratingOption.emoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = ratingOption.label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HekayaDarkBlue
                            )
                        }
                    }
                }

                // Favorite Bookmark Toggle
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFavorite) HekayaGold.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.45f)
                        )
                        .clickable { onToggleFavorite() }
                        .testTag("bookmark_toggle_${story.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (story.isPremium && !isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "قصة مميزة",
                            tint = HekayaGold,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة للمفضلة",
                            tint = if (isFavorite) Color(0xFFE11D48) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Text Content matching Image 9
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Category Genre Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(HekayaLightBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = story.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = story.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HekayaDarkBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = story.subtitle,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = HekayaTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = HekayaGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${story.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HekayaDarkBlue
                        )
                    }

                    Text(
                        text = "${story.pages.size} صفحات",
                        fontSize = 11.sp,
                        color = HekayaTextMuted
                    )
                }

                // Resume Reading Button if bookmarked or has progress
                if (resumePageIndex != null && onResume != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Button(
                        onClick = onResume,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .testTag("resume_reading_button_${story.id}"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (savedBookmark != null) HekayaGold else HekayaLightBlue,
                            contentColor = HekayaDarkBlue
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (savedBookmark != null) Icons.Default.Bookmark else Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = HekayaDarkBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "تابع (ص ${resumePageIndex + 1})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContinueReadingHeroCard(
    story: Story,
    targetPageIndex: Int,
    isExplicitBookmark: Boolean,
    onResumeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onResumeClick() }
            .testTag("continue_reading_hero_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HekayaDarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Story Thumbnail
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = story.coverRes),
                    contentDescription = story.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (isExplicitBookmark) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(HekayaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = HekayaDarkBlue,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isExplicitBookmark) HekayaGold else HekayaBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isExplicitBookmark) "علامة محفوظة 🔖" else "متابعة القراءة 📖",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isExplicitBookmark) HekayaDarkBlue else Color.White
                        )
                    }
                    Text(
                        text = "وصلت لصفحة ${targetPageIndex + 1} من ${story.pages.size}",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = story.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress line
                val progressFraction = (targetPageIndex + 1f) / story.pages.size.coerceAtLeast(1)
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = HekayaGold,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }

            // Continue Button
            androidx.compose.material3.Button(
                onClick = onResumeClick,
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = HekayaGold,
                    contentColor = HekayaDarkBlue
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "واصل ↩",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
