package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.DailyLimitReachedDialog
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ParentalSettingsOverlay
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SecurityAlertDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StoryLoadingScreen
import com.example.ui.screens.StoryReaderScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.theme.HekayaTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HekayaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HekayaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HekayaApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val adminTab by viewModel.adminTab.collectAsState()
    val stories by viewModel.filteredStories.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()
    val selectedStory by viewModel.selectedStory.collectAsState()
    val readerPageIndex by viewModel.readerPageIndex.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedAgeFilter by viewModel.selectedAgeFilter.collectAsState()
    val showSecurityAlert by viewModel.showSecurityAlert.collectAsState()
    val showUpgradePrompt by viewModel.showUpgradePrompt.collectAsState()
    val draft by viewModel.newStoryDraft.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val isPublishSuccess by viewModel.isPublishSuccess.collectAsState()
    val recentActivities by viewModel.recentActivities.collectAsState()
    val readerSettings by viewModel.readerSettings.collectAsState()
    val ttsStatus by viewModel.ttsStatus.collectAsState()
    val bookmarkBannerMessage by viewModel.bookmarkBannerMessage.collectAsState()
    val aiGenerationState by viewModel.aiGenerationState.collectAsState()

    var showParentalOverlay by remember { mutableStateOf(false) }

    // Handle system back navigation
    BackHandler(enabled = currentScreen != AppScreen.LIBRARY && currentScreen != AppScreen.LOGIN) {
        when (currentScreen) {
            AppScreen.STORY_READER, AppScreen.STORY_LOADING -> {
                viewModel.stopAudioNarration()
                viewModel.navigateTo(AppScreen.LIBRARY)
            }
            AppScreen.ADMIN_DASHBOARD, AppScreen.SUBSCRIPTION, AppScreen.SETTINGS -> {
                viewModel.navigateTo(AppScreen.LIBRARY)
            }
            else -> viewModel.navigateTo(AppScreen.LIBRARY)
        }
    }

    Crossfade(targetState = currentScreen, label = "screen_navigation") { screen ->
        when (screen) {
            AppScreen.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = { viewModel.navigateTo(AppScreen.LIBRARY) },
                    onAdminLogin = {
                        viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                    }
                )
            }

            AppScreen.LIBRARY -> {
                LibraryScreen(
                    stories = stories,
                    userAccount = userAccount,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    selectedCategory = selectedCategory,
                    onCategorySelect = { viewModel.selectCategory(it) },
                    selectedAgeFilter = selectedAgeFilter,
                    onAgeFilterSelect = { viewModel.selectAgeFilter(it) },
                    favoriteStoryIds = userAccount.favoriteStoryIds,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onStoryClick = { viewModel.openStory(it) },
                    onResumeStory = { viewModel.resumeStory(it) },
                    onOpenStoryWithPage = { story, page -> viewModel.openStory(story, page) },
                    aiGenerationState = aiGenerationState,
                    onGenerateAiStory = { theme, hero, companion, age, moral ->
                        viewModel.generateFairyTaleWithGenkit(theme, hero, companion, age, moral)
                    },
                    onOpenGeneratedStory = { story ->
                        viewModel.openGeneratedStory(story)
                    },
                    onClearAiGenerationState = {
                        viewModel.clearAiGenerationState()
                    },
                    onOpenProfile = { viewModel.navigateTo(AppScreen.PROFILE) },
                    onOpenSubscription = { viewModel.navigateTo(AppScreen.SUBSCRIPTION) },
                    onOpenAdmin = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) },
                    onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    onOpenParentalOverlay = { showParentalOverlay = true },
                    onTriggerSecurityAlert = { viewModel.triggerSecurityAlert() }
                )
            }

            AppScreen.STORY_LOADING -> {
                StoryLoadingScreen(
                    story = selectedStory,
                    onBack = { viewModel.navigateTo(AppScreen.LIBRARY) }
                )
            }

            AppScreen.STORY_READER -> {
                val isStoryBookmarked = selectedStory?.let { userAccount.favoriteStoryIds.contains(it.id) } ?: false
                StoryReaderScreen(
                    story = selectedStory,
                    pageIndex = readerPageIndex,
                    isAudioPlaying = isAudioPlaying,
                    ttsStatus = ttsStatus,
                    isBookmarked = isStoryBookmarked,
                    userAccount = userAccount,
                    bookmarkBannerMessage = bookmarkBannerMessage,
                    onDismissBookmarkBanner = { viewModel.dismissBookmarkBanner() },
                    onTogglePageBookmark = { storyId, pageIndex -> viewModel.togglePageBookmark(storyId, pageIndex) },
                    onJumpToPage = { viewModel.jumpToPage(it) },
                    onToggleBookmark = {
                        selectedStory?.let { viewModel.toggleFavorite(it.id) }
                    },
                    readerSettings = readerSettings,
                    onSetTheme = { viewModel.setReaderTheme(it) },
                    onSetFontSize = { viewModel.setReaderFontSize(it) },
                    onIncreaseFontSize = { viewModel.increaseFontSize() },
                    onDecreaseFontSize = { viewModel.decreaseFontSize() },
                    onToggleBold = { viewModel.toggleReaderBold(it) },
                    onSetLineSpacing = { viewModel.setReaderLineSpacing(it) },
                    onToggleNightMode = { viewModel.toggleReaderNightMode() },
                    onToggleAudio = { viewModel.toggleAudioNarration() },
                    onReplayAudio = { viewModel.replayCurrentPage() },
                    onSetSpeechRate = { viewModel.setSpeechRate(it) },
                    onSetSpeechPitch = { viewModel.setSpeechPitch(it) },
                    onNextPage = { viewModel.nextPage() },
                    onPrevPage = { viewModel.prevPage() },
                    onRateStory = { storyId, ratingId ->
                        viewModel.rateStory(storyId, ratingId)
                    },
                    onBack = {
                        viewModel.stopAudioNarration()
                        viewModel.navigateTo(AppScreen.LIBRARY)
                    },
                    onTriggerSecurityAlert = { viewModel.triggerSecurityAlert() }
                )
            }

            AppScreen.SUBSCRIPTION -> {
                SubscriptionScreen(
                    isCurrentPremium = userAccount.isPremium,
                    onUpgrade = { viewModel.upgradeToPremium() },
                    onBack = { viewModel.navigateTo(AppScreen.LIBRARY) }
                )
            }

            AppScreen.ADMIN_DASHBOARD -> {
                AdminDashboardScreen(
                    currentTab = adminTab,
                    onTabSelected = { viewModel.setAdminTab(it) },
                    draft = draft,
                    onUpdateTitle = { viewModel.updateDraftTitle(it) },
                    onUpdateAge = { viewModel.updateDraftTargetAge(it) },
                    onUpdatePremium = { viewModel.updateDraftPremium(it) },
                    onToggleTag = { viewModel.toggleDraftTag(it) },
                    onUpdatePageText = { p, txt -> viewModel.updateDraftPageText(p, txt) },
                    onAddPage = { viewModel.addDraftPage() },
                    uploadProgress = uploadProgress,
                    onUploadClick = { viewModel.simulateDocxUpload() },
                    onPublishClick = { viewModel.publishDraft() },
                    isPublishSuccess = isPublishSuccess,
                    activities = recentActivities,
                    onBack = { viewModel.navigateTo(AppScreen.LIBRARY) }
                )
            }

            AppScreen.SETTINGS -> {
                SettingsScreen(
                    userAccount = userAccount,
                    onUpgradeClick = { viewModel.navigateTo(AppScreen.SUBSCRIPTION) },
                    onTogglePlan = { viewModel.togglePlan() },
                    onTriggerSecurityAlert = { viewModel.triggerSecurityAlert() },
                    onEditProfile = { viewModel.navigateTo(AppScreen.PROFILE) },
                    onOpenParentalOverlay = { showParentalOverlay = true },
                    onBack = { viewModel.navigateTo(AppScreen.LIBRARY) }
                )
            }

            AppScreen.PROFILE -> {
                ProfileScreen(
                    userAccount = userAccount,
                    onSelectAvatar = { viewModel.selectAvatar(it) },
                    onUpdateChildName = { viewModel.updateChildName(it) },
                    onOpenSubscription = { viewModel.navigateTo(AppScreen.SUBSCRIPTION) },
                    onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    onOpenParentalOverlay = { showParentalOverlay = true },
                    onBack = { viewModel.navigateTo(AppScreen.LIBRARY) }
                )
            }
        }
    }

    // Parental Settings Overlay
    if (showParentalOverlay) {
        ParentalSettingsOverlay(
            userAccount = userAccount,
            onDismiss = { showParentalOverlay = false },
            onSetDailyReadingLimit = { viewModel.setDailyReadingLimit(it) },
            onSetDailyTimeLimitEnabled = { viewModel.setDailyTimeLimitEnabled(it) },
            onSetBedtimeModeEnabled = { viewModel.setBedtimeModeEnabled(it) },
            onResetTodayReadingTime = { viewModel.resetTodayReadingTime() },
            onAddExtraReadingTime = { viewModel.addReadingTime(it) }
        )
    }

    // Security Alert Dialog matching Image 1
    if (showSecurityAlert) {
        SecurityAlertDialog(
            onDismiss = { viewModel.dismissSecurityAlert() }
        )
    }

    // Upgrade Required Modal if user clicked premium story while free
    if (showUpgradePrompt) {
        SubscriptionScreen(
            isCurrentPremium = userAccount.isPremium,
            onUpgrade = { viewModel.upgradeToPremium() },
            onBack = { viewModel.dismissUpgradePrompt() }
        )
    }
}
