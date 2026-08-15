package com.example.data.models

import androidx.annotation.DrawableRes
import com.example.R

data class StoryPage(
    val pageNumber: Int,
    val text: String,
    @DrawableRes val imageRes: Int = R.drawable.story_forest_girl,
    val customImageUrl: String? = null,
    val narrationAudioUrl: String? = null
)

data class Story(
    val id: String,
    val title: String,
    val subtitle: String,
    val ageRange: String,
    val category: String,
    val tags: List<String>,
    @DrawableRes val coverRes: Int,
    val isPremium: Boolean = false,
    val pages: List<StoryPage> = emptyList(),
    val viewsCount: Int = 12450,
    val completionRatePercent: Int = 92,
    val rating: Double = 4.9,
    val author: String = "حكاية"
)

enum class ReaderTheme(
    val title: String,
    val cardBackground: Long,
    val textColor: Long,
    val secondaryTextColor: Long,
    val audioBarBackground: Long,
    val isDark: Boolean
) {
    LIGHT(
        title = "نهاري",
        cardBackground = 0xFFFFFFFF,
        textColor = 0xFF1E293B,
        secondaryTextColor = 0xFF64748B,
        audioBarBackground = 0xFFE0F2FE,
        isDark = false
    ),
    SEPIA(
        title = "ورقي دافئ",
        cardBackground = 0xFFFDF6E2,
        textColor = 0xFF3D2C1E,
        secondaryTextColor = 0xFF7D6548,
        audioBarBackground = 0xFFF5E6C8,
        isDark = false
    ),
    DARK(
        title = "ليلي هادئ",
        cardBackground = 0xFF1E293B,
        textColor = 0xFFF8FAFC,
        secondaryTextColor = 0xFF94A3B8,
        audioBarBackground = 0xFF0F172A,
        isDark = true
    ),
    MIDNIGHT(
        title = "أسود داكن",
        cardBackground = 0xFF0B0F17,
        textColor = 0xFFF1F5F9,
        secondaryTextColor = 0xFF64748B,
        audioBarBackground = 0xFF182234,
        isDark = true
    )
}

data class ReaderSettings(
    val theme: ReaderTheme = ReaderTheme.LIGHT,
    val fontSizeSp: Float = 18f,
    val isBoldText: Boolean = false,
    val lineSpacingMultiplier: Float = 1.6f,
    val speechRate: Float = 0.9f,
    val speechPitch: Float = 1.05f
)

data class ChildAvatar(
    val id: String,
    val name: String,
    val emoji: String,
    val backgroundColorHex: Long,
    val title: String
)

data class ReadingBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val progressPercent: Int = 100
)

data class ChildRatingOption(
    val id: String,
    val emoji: String,
    val label: String,
    val description: String,
    val colorHex: Long,
    val praiseMessage: String
)

val availableChildRatings = listOf(
    ChildRatingOption(
        id = "awesome",
        emoji = "🤩",
        label = "أسطورية ورائعة!",
        description = "أحببتها كثيراً جداً",
        colorHex = 0xFFFFD54F,
        praiseMessage = "يا للروعة! 🎉 قصة مشوقة جداً ستظل في ذاكرتك!"
    ),
    ChildRatingOption(
        id = "happy",
        emoji = "😊",
        label = "ممتعة وجميلة",
        description = "استمتعت بقراءتها",
        colorHex = 0xFF81C784,
        praiseMessage = "رائع! 🌟 قراءة ممتعة أضافت لخيالم البهجة!"
    ),
    ChildRatingOption(
        id = "sleepy",
        emoji = "😴",
        label = "هادئة ومريحة",
        description = "قصة نوم لطيفة",
        colorHex = 0xFF90CAF9,
        praiseMessage = "أحلاماً سعيدة! 🌙 حكاية هادئة لنوم عميق ومريح."
    ),
    ChildRatingOption(
        id = "curious",
        emoji = "🤔",
        label = "ملهمة ومفكرة",
        description = "فيها أفكار ذكية",
        colorHex = 0xFFBA68C8,
        praiseMessage = "أحسنت التفكير! 💡 قصة مليئة بالحكمة والعبر الجميلة."
    ),
    ChildRatingOption(
        id = "funny",
        emoji = "😂",
        label = "مضحكة ومرحة",
        description = "رسمت البسمة",
        colorHex = 0xFFFFB74D,
        praiseMessage = "ضحكة دائمة! 😄 الضحك والمرح أجمل ما في الحكايات!"
    ),
    ChildRatingOption(
        id = "okay",
        emoji = "🥱",
        label = "عادية وبسيطة",
        description = "أنتظر مغامرة أقوى",
        colorHex = 0xFFB0BEC5,
        praiseMessage = "شكراً لرأيك الصادق! 👍 سنجد لك مغامرة أقوى في المرة القادمة!"
    )
)

data class StoryBookmark(
    val storyId: String,
    val pageIndex: Int,
    val savedAtTimestamp: Long = System.currentTimeMillis(),
    val storyTitle: String = ""
)

data class UserAccount(
    val name: String = "سارة",
    val email: String = "parent@example.com",
    val isPremium: Boolean = false,
    val selectedAvatarId: String = "fox",
    val currentLevel: Int = 3,
    val levelTitle: String = "بطل القراءة الفضي",
    val currentExp: Int = 320,
    val nextLevelExp: Int = 500,
    val readingStreakDays: Int = 5,
    val storiesReadCount: Int = 14,
    val readingMinutesThisWeek: Int = 85,
    val todayReadingMinutes: Int = 18,
    val dailyReadingLimitMinutes: Int = 30,
    val isDailyTimeLimitEnabled: Boolean = true,
    val isBedtimeModeEnabled: Boolean = true,
    val favoriteStoryIds: Set<String> = setOf("forest_friends", "magic_carpet"),
    val favoriteGenre: String = "مغامرة",
    val bookmarks: Map<String, StoryBookmark> = mapOf(
        "forest_friends" to StoryBookmark("forest_friends", 1, System.currentTimeMillis(), "أصدقاء الغابة"),
        "magic_carpet" to StoryBookmark("magic_carpet", 2, System.currentTimeMillis(), "بساط الريح العجيب")
    ),
    val readingProgress: Map<String, Int> = mapOf(
        "forest_friends" to 1,
        "magic_carpet" to 2
    ),
    val storyRatings: Map<String, String> = mapOf(
        "forest_friends" to "awesome",
        "magic_carpet" to "happy"
    )
)

val availableChildAvatars = listOf(
    ChildAvatar("fox", "ثعلوب القارئ", "🦊", 0xFFFFE0B2, "المستكشف الذكي"),
    ChildAvatar("astronaut", "رائد الفضاء", "🧑‍🚀", 0xFFE1BEE7, "مستكشف الكواكب"),
    ChildAvatar("lion", "ليث الشجاع", "🦁", 0xFFFFECB3, "بطل الغابة"),
    ChildAvatar("bear", "دبدوب اللطيف", "🐻", 0xFFD7CCC8, "محب القصص"),
    ChildAvatar("owl", "بومة الحكمة", "🦉", 0xFFC8E6C9, "صديقة الكتب"),
    ChildAvatar("rabbit", "أرنوب السريع", "🐰", 0xFFFFCDD2, "القارئ المتحمس"),
    ChildAvatar("cat", "قطقوط المرح", "🐱", 0xFFB3E5FC, "المكتشف الصغير"),
    ChildAvatar("dragon", "تنين الأحلام", "🐲", 0xFFDCEDC8, "فارس الأساطير")
)

val defaultReadingBadges = listOf(
    ReadingBadge("first_story", "القارئ المبتدئ", "قراءة أول قصة بنجاح", "📖", true, 100),
    ReadingBadge("streak_3", "شعلة الحماس", "قراءة 3 أيام متتالية", "🔥", true, 100),
    ReadingBadge("bedtime_listener", "حارس الأحلام", "استماع لـ 5 قصص قبل النوم", "🌙", true, 100),
    ReadingBadge("adventure_seeker", "مستكشف المغامرات", "إكمال 5 قصص مغامرات", "🚀", true, 100),
    ReadingBadge("master_reader", "ملك الحكايات", "قراءة 20 قصة كاملة", "👑", false, 70),
    ReadingBadge("super_listener", "المستمع الذهبي", "قضاء 120 دقيقة في الاستماع", "🎧", false, 70)
)

data class ActivityItem(
    val id: String,
    val title: String,
    val timeAgo: String,
    val iconType: String // "user", "author", "star"
)

data class ExtractedDraftPage(
    val pageNumber: Int,
    var text: String,
    @DrawableRes var imageRes: Int = R.drawable.story_forest_girl
)

data class NewStoryDraft(
    var title: String = "نورة والغابة المسحورة",
    var targetAge: String = "6-8 سنوات",
    var isPremium: Boolean = true,
    var selectedTags: Set<String> = setOf("حيوانات", "صداقة"),
    var pages: List<ExtractedDraftPage> = listOf(
        ExtractedDraftPage(
            pageNumber = 1,
            text = "في غابة بعيدة، كان هناك دب صغير يحب استكشاف الأماكن الجديدة كل يوم. وفي يوم...",
            imageRes = R.drawable.story_forest_friends
        ),
        ExtractedDraftPage(
            pageNumber = 2,
            text = "التقى الدب الصغير بعصفور أزرق جميل يغرد أجمل الألحان. سأله الدب: \"إلى أين أنت ذاهب؟\"",
            imageRes = R.drawable.story_forest_girl
        ),
        ExtractedDraftPage(
            pageNumber = 3,
            text = "قال العصفور: \"أنا ذاهب للبحث عن صديقي الأرنب، هل تريد مرافقتي في هذه الرحلة؟\"",
            imageRes = R.drawable.story_fox_reading
        )
    )
)

sealed interface AiStoryGenerationState {
    data object Idle : AiStoryGenerationState
    data class Generating(
        val theme: String,
        val heroName: String,
        val stepMessage: String = "جاري تأليف أحداث الحكاية الساحرة بالذكاء الاصطناعي... ✨"
    ) : AiStoryGenerationState
    data class Success(val generatedStory: Story) : AiStoryGenerationState
    data class Error(val errorMessage: String) : AiStoryGenerationState
}

