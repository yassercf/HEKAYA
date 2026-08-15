package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.FairyTaleAiGenerator
import com.example.data.models.ActivityItem
import com.example.data.models.AiStoryGenerationState
import com.example.data.models.NewStoryDraft
import com.example.data.models.ReaderSettings
import com.example.data.models.ReaderTheme
import com.example.data.models.Story
import com.example.data.models.UserAccount
import com.example.data.repository.StoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppScreen {
    LOGIN,
    LIBRARY,
    STORY_LOADING,
    STORY_READER,
    SUBSCRIPTION,
    ADMIN_DASHBOARD,
    SETTINGS,
    PROFILE
}

enum class AdminTab {
    UPLOAD,
    CATEGORIZE,
    REVIEW_PUBLISH,
    ANALYTICS
}

class MainViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository = StoryRepository()
    private val aiGenerator = FairyTaleAiGenerator()

    val userAccount: StateFlow<UserAccount> = repository.userAccount
    val newStoryDraft: StateFlow<NewStoryDraft> = repository.newStoryDraft
    val recentActivities: StateFlow<List<ActivityItem>> = repository.recentActivities

    private val _aiGenerationState = MutableStateFlow<AiStoryGenerationState>(AiStoryGenerationState.Idle)
    val aiGenerationState: StateFlow<AiStoryGenerationState> = _aiGenerationState.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.LIBRARY)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _adminTab = MutableStateFlow(AdminTab.UPLOAD)
    val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

    private val _selectedStory = MutableStateFlow<Story?>(null)
    val selectedStory: StateFlow<Story?> = _selectedStory.asStateFlow()

    private val _readerPageIndex = MutableStateFlow(0)
    val readerPageIndex: StateFlow<Int> = _readerPageIndex.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedAgeFilter = MutableStateFlow<String?>(null)
    val selectedAgeFilter: StateFlow<String?> = _selectedAgeFilter.asStateFlow()

    private val _showSecurityAlert = MutableStateFlow(false)
    val showSecurityAlert: StateFlow<Boolean> = _showSecurityAlert.asStateFlow()

    private val _showUpgradePrompt = MutableStateFlow(false)
    val showUpgradePrompt: StateFlow<Boolean> = _showUpgradePrompt.asStateFlow()

    private val _uploadProgress = MutableStateFlow<Float?>(null)
    val uploadProgress: StateFlow<Float?> = _uploadProgress.asStateFlow()

    private val _isPublishSuccess = MutableStateFlow(false)
    val isPublishSuccess: StateFlow<Boolean> = _isPublishSuccess.asStateFlow()

    private val _readerSettings = MutableStateFlow(ReaderSettings())
    val readerSettings: StateFlow<ReaderSettings> = _readerSettings.asStateFlow()

    private val _bookmarkBannerMessage = MutableStateFlow<String?>(null)
    val bookmarkBannerMessage: StateFlow<String?> = _bookmarkBannerMessage.asStateFlow()

    // Filtered Stories
    val filteredStories: StateFlow<List<Story>> = combine(
        repository.stories,
        _searchQuery,
        _selectedCategory,
        _selectedAgeFilter,
        repository.userAccount
    ) { stories, query, category, age, userAccount ->
        stories.filter { story ->
            val matchesQuery = query.isBlank() ||
                    story.title.contains(query, ignoreCase = true) ||
                    story.subtitle.contains(query, ignoreCase = true) ||
                    story.tags.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = when {
                category == "الكل" -> true
                category.contains("المحفوظة") -> userAccount.favoriteStoryIds.contains(story.id)
                else -> story.category == category || story.tags.contains(category)
            }

            val matchesAge = age == null || story.ageRange.contains(age)

            matchesQuery && matchesCategory && matchesAge
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // TTS
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var readingJob: Job? = null

    private val _ttsStatus = MutableStateFlow("الراوي جاهز للقراءة 🎙️")
    val ttsStatus: StateFlow<String> = _ttsStatus.asStateFlow()

    init {
        tts = TextToSpeech(application, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!ttsReady) {
                // Fallback to default locale
                tts?.setLanguage(Locale.getDefault())
                ttsReady = true
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isAudioPlaying.value = true
                    _ttsStatus.value = "الراوي يقرأ بصوت عذب 🎵"
                }

                override fun onDone(utteranceId: String?) {
                    _isAudioPlaying.value = false
                    _ttsStatus.value = "اكتملت قراءة هذه الصفحة ✨"
                }

                override fun onError(utteranceId: String?) {
                    _isAudioPlaying.value = false
                    _ttsStatus.value = "الراوي جاهز للقراءة 🎙️"
                }
            })
        } else {
            ttsReady = false
            _ttsStatus.value = "محرك الصوت غير متوفر"
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setAdminTab(tab: AdminTab) {
        _adminTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectAgeFilter(age: String?) {
        _selectedAgeFilter.value = if (_selectedAgeFilter.value == age) null else age
    }

    fun openStory(story: Story, initialPage: Int? = null) {
        if (story.isPremium && !userAccount.value.isPremium) {
            _showUpgradePrompt.value = true
            return
        }
        _selectedStory.value = story
        val startPage = (initialPage ?: 0).coerceIn(0, (story.pages.size - 1).coerceAtLeast(0))
        _readerPageIndex.value = startPage
        _currentScreen.value = AppScreen.STORY_LOADING

        // Record initial progress
        repository.updateReadingProgress(story.id, startPage)

        // Simulate magical loading experience
        viewModelScope.launch {
            delay(1200)
            _currentScreen.value = AppScreen.STORY_READER
            repository.recordStoryRead(story.id)
            speakCurrentPage()
        }
    }

    fun resumeStory(story: Story) {
        val savedBookmark = userAccount.value.bookmarks[story.id]
        val savedProgress = userAccount.value.readingProgress[story.id]
        val resumePage = savedBookmark?.pageIndex ?: savedProgress ?: 0
        openStory(story, initialPage = resumePage)
    }

    fun jumpToPage(pageIndex: Int) {
        val story = _selectedStory.value ?: return
        val validPage = pageIndex.coerceIn(0, story.pages.size - 1)
        if (_readerPageIndex.value != validPage) {
            _readerPageIndex.value = validPage
            repository.updateReadingProgress(story.id, validPage)
            if (_isAudioPlaying.value) {
                speakCurrentPage()
            } else {
                stopAudioNarration()
            }
        }
    }

    fun togglePageBookmark(storyId: String, pageIndex: Int) {
        val story = _selectedStory.value ?: repository.stories.value.find { it.id == storyId }
        val title = story?.title ?: "قصة"
        val isNowBookmarked = repository.togglePageBookmark(storyId, pageIndex)
        
        _bookmarkBannerMessage.value = if (isNowBookmarked) {
            "تم تثبيت الإشارة المرجعية في الصفحة ${pageIndex + 1}! 🔖 يمكنك استئناف القراءة من هنا في أي وقت."
        } else {
            "تمت إزالة الإشارة المرجعية من هذه الصفحة."
        }

        viewModelScope.launch {
            delay(3500)
            if (_bookmarkBannerMessage.value?.contains("${pageIndex + 1}") == true || _bookmarkBannerMessage.value?.contains("إزالة") == true) {
                _bookmarkBannerMessage.value = null
            }
        }
    }

    fun dismissBookmarkBanner() {
        _bookmarkBannerMessage.value = null
    }

    fun nextPage() {
        val story = _selectedStory.value ?: return
        if (_readerPageIndex.value < story.pages.size - 1) {
            val newPage = _readerPageIndex.value + 1
            _readerPageIndex.value = newPage
            repository.updateReadingProgress(story.id, newPage)
            if (_isAudioPlaying.value) {
                speakCurrentPage()
            } else {
                stopAudioNarration()
            }
        }
    }

    fun prevPage() {
        val story = _selectedStory.value ?: return
        if (_readerPageIndex.value > 0) {
            val newPage = _readerPageIndex.value - 1
            _readerPageIndex.value = newPage
            repository.updateReadingProgress(story.id, newPage)
            if (_isAudioPlaying.value) {
                speakCurrentPage()
            } else {
                stopAudioNarration()
            }
        }
    }

    fun toggleAudioNarration() {
        if (_isAudioPlaying.value) {
            stopAudioNarration()
        } else {
            speakCurrentPage()
        }
    }

    fun replayCurrentPage() {
        stopAudioNarration()
        speakCurrentPage()
    }

    fun setSpeechRate(rate: Float) {
        _readerSettings.update { it.copy(speechRate = rate.coerceIn(0.5f, 2.0f)) }
        tts?.setSpeechRate(rate)
        if (_isAudioPlaying.value) {
            replayCurrentPage()
        }
    }

    fun setSpeechPitch(pitch: Float) {
        _readerSettings.update { it.copy(speechPitch = pitch.coerceIn(0.5f, 2.0f)) }
        tts?.setPitch(pitch)
        if (_isAudioPlaying.value) {
            replayCurrentPage()
        }
    }

    private fun speakCurrentPage() {
        val story = _selectedStory.value ?: return
        val pages = story.pages
        val pageIdx = _readerPageIndex.value
        if (pageIdx in pages.indices) {
            val text = pages[pageIdx].text
            _isAudioPlaying.value = true
            readingJob?.cancel()

            readingJob = viewModelScope.launch {
                if (ttsReady && tts != null) {
                    tts?.setSpeechRate(_readerSettings.value.speechRate)
                    tts?.setPitch(_readerSettings.value.speechPitch)
                    val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "story_tts_page_$pageIdx")
                    if (result == TextToSpeech.ERROR) {
                        // Fallback duration
                        val readTime = (text.length * 75L).coerceAtLeast(3500L)
                        delay(readTime)
                        _isAudioPlaying.value = false
                    }
                } else {
                    // Fallback simulated duration if TTS hardware not initialized
                    val readTime = (text.length * 75L).coerceAtLeast(3500L)
                    delay(readTime)
                    _isAudioPlaying.value = false
                }
            }
        }
    }

    fun stopAudioNarration() {
        readingJob?.cancel()
        tts?.stop()
        _isAudioPlaying.value = false
        _ttsStatus.value = "الراوي متوقف مؤقتاً"
    }

    fun toggleFavorite(storyId: String) {
        repository.toggleFavorite(storyId)
    }

    fun upgradeToPremium() {
        repository.upgradeToPremium()
        _showUpgradePrompt.value = false
    }

    fun togglePlan() {
        repository.togglePlan()
    }

    fun triggerSecurityAlert() {
        _showSecurityAlert.value = true
    }

    fun dismissSecurityAlert() {
        _showSecurityAlert.value = false
    }

    fun dismissUpgradePrompt() {
        _showUpgradePrompt.value = false
    }

    // Admin Draft actions
    fun updateDraftTitle(title: String) = repository.updateDraftTitle(title)
    fun updateDraftTargetAge(age: String) = repository.updateDraftTargetAge(age)
    fun updateDraftPremium(isPremium: Boolean) = repository.updateDraftPremium(isPremium)
    fun toggleDraftTag(tag: String) = repository.toggleDraftTag(tag)
    fun updateDraftPageText(pageNumber: Int, text: String) = repository.updateDraftPageText(pageNumber, text)
    fun addDraftPage() = repository.addDraftPage()

    fun simulateDocxUpload() {
        viewModelScope.launch {
            _uploadProgress.value = 0.1f
            delay(300)
            _uploadProgress.value = 0.45f
            delay(400)
            _uploadProgress.value = 0.85f
            delay(300)
            _uploadProgress.value = 1.0f
            delay(200)
            _uploadProgress.value = null
        }
    }

    fun publishDraft() {
        viewModelScope.launch {
            val story = repository.publishDraft()
            _isPublishSuccess.value = true
            delay(1200)
            _isPublishSuccess.value = false
            _selectedStory.value = story
            _readerPageIndex.value = 0
            _currentScreen.value = AppScreen.LIBRARY
        }
    }

    /**
     * Generates a new illustrated fairy tale using Firebase Genkit AI / Firebase AI Logic
     * based on user-provided themes, character names, and educational values.
     */
    fun generateFairyTaleWithGenkit(
        theme: String,
        characterName: String,
        companionName: String = "",
        ageGroup: String = "6-8 سنوات",
        moralLesson: String = "التعاون والشجاعة"
    ) {
        val effectiveTheme = if (theme.isBlank()) "مغامرة سحرية مشوقة" else theme.trim()
        val effectiveHero = if (characterName.isBlank()) {
            userAccount.value.name.ifBlank { "البطل الصغير" }
        } else {
            characterName.trim()
        }

        viewModelScope.launch {
            _aiGenerationState.value = AiStoryGenerationState.Generating(
                theme = effectiveTheme,
                heroName = effectiveHero,
                stepMessage = "جاري تأليف حكاية ساحرة للبطل $effectiveHero حول \"$effectiveTheme\" عبر الذكاء الاصطناعي (Firebase Genkit)... ✨🪄"
            )

            try {
                val story = aiGenerator.generateFairyTale(
                    theme = effectiveTheme,
                    heroName = effectiveHero,
                    companionName = companionName.trim(),
                    ageGroup = ageGroup,
                    moralLesson = moralLesson
                )
                repository.addGeneratedStory(story)
                _aiGenerationState.value = AiStoryGenerationState.Success(story)
            } catch (e: Exception) {
                _aiGenerationState.value = AiStoryGenerationState.Error(
                    e.message ?: "تعذر تأليف القصة حالياً. يرجى المحاولة لاحقاً."
                )
            }
        }
    }

    fun clearAiGenerationState() {
        _aiGenerationState.value = AiStoryGenerationState.Idle
    }

    fun openGeneratedStory(story: Story) {
        clearAiGenerationState()
        openStory(story, initialPage = 0)
    }

    // Reader Settings Actions
    fun setReaderTheme(theme: ReaderTheme) {
        _readerSettings.update { it.copy(theme = theme) }
    }

    fun setReaderFontSize(sizeSp: Float) {
        _readerSettings.update { it.copy(fontSizeSp = sizeSp.coerceIn(14f, 32f)) }
    }

    fun increaseFontSize() {
        _readerSettings.update { it.copy(fontSizeSp = (it.fontSizeSp + 2f).coerceAtMost(32f)) }
    }

    fun decreaseFontSize() {
        _readerSettings.update { it.copy(fontSizeSp = (it.fontSizeSp - 2f).coerceAtLeast(14f)) }
    }

    fun toggleReaderBold(isBold: Boolean) {
        _readerSettings.update { it.copy(isBoldText = isBold) }
    }

    fun setReaderLineSpacing(multiplier: Float) {
        _readerSettings.update { it.copy(lineSpacingMultiplier = multiplier) }
    }

    fun toggleReaderNightMode() {
        _readerSettings.update { current ->
            val nextTheme = when (current.theme) {
                ReaderTheme.LIGHT, ReaderTheme.SEPIA -> ReaderTheme.DARK
                ReaderTheme.DARK, ReaderTheme.MIDNIGHT -> ReaderTheme.LIGHT
            }
            current.copy(theme = nextTheme)
        }
    }

    fun selectAvatar(avatarId: String) {
        repository.selectAvatar(avatarId)
    }

    fun updateChildName(name: String) {
        repository.updateChildName(name)
    }

    fun setDailyReadingLimit(minutes: Int) {
        repository.setDailyReadingLimit(minutes)
    }

    fun setDailyTimeLimitEnabled(enabled: Boolean) {
        repository.setDailyTimeLimitEnabled(enabled)
    }

    fun setBedtimeModeEnabled(enabled: Boolean) {
        repository.setBedtimeModeEnabled(enabled)
    }

    fun resetTodayReadingTime() {
        repository.resetTodayReadingTime()
    }

    fun addReadingTime(minutes: Int) {
        repository.addReadingTime(minutes)
    }

    fun rateStory(storyId: String, ratingOptionId: String) {
        repository.rateStory(storyId, ratingOptionId)
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioNarration()
        tts?.shutdown()
    }
}
