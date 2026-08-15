package com.example.data.repository

import com.example.R
import com.example.data.models.ActivityItem
import com.example.data.models.ExtractedDraftPage
import com.example.data.models.NewStoryDraft
import com.example.data.models.Story
import com.example.data.models.StoryBookmark
import com.example.data.models.StoryPage
import com.example.data.models.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StoryRepository {

    private val _stories = MutableStateFlow<List<Story>>(createInitialStories())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _userAccount = MutableStateFlow(UserAccount())
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    private val _newStoryDraft = MutableStateFlow(NewStoryDraft())
    val newStoryDraft: StateFlow<NewStoryDraft> = _newStoryDraft.asStateFlow()

    private val _recentActivities = MutableStateFlow<List<ActivityItem>>(
        listOf(
            ActivityItem("1", "مشترك جديد: أحمد علي (الباقة المميزة)", "منذ 5 دقائق", "user"),
            ActivityItem("2", "المؤلفة منى أضافت قصة: \"رحلة الفضاء\"", "منذ 15 دقيقة", "author"),
            ActivityItem("3", "تقييم جديد 5 نجوم لقصة \"أصدقاء الغابة\"", "منذ ساعة واحدة", "star"),
            ActivityItem("4", "مشترك جديد: ليلى ناصر (الباقة المميزة)", "منذ ساعتين", "user")
        )
    )
    val recentActivities: StateFlow<List<ActivityItem>> = _recentActivities.asStateFlow()

    fun saveBookmark(storyId: String, pageIndex: Int) {
        val story = _stories.value.find { it.id == storyId }
        val title = story?.title ?: "قصة"
        val newBookmark = StoryBookmark(
            storyId = storyId,
            pageIndex = pageIndex,
            savedAtTimestamp = System.currentTimeMillis(),
            storyTitle = title
        )
        _userAccount.update { current ->
            val updatedBookmarks = current.bookmarks.toMutableMap()
            updatedBookmarks[storyId] = newBookmark
            val updatedProgress = current.readingProgress.toMutableMap()
            updatedProgress[storyId] = pageIndex
            current.copy(
                bookmarks = updatedBookmarks,
                readingProgress = updatedProgress
            )
        }

        _recentActivities.update { current ->
            val activity = ActivityItem(
                id = System.currentTimeMillis().toString(),
                title = "تم حفظ علامة قراءة في الصفحة ${pageIndex + 1} لقصة \"$title\" 🔖",
                timeAgo = "الآن",
                iconType = "star"
            )
            listOf(activity) + current.take(15)
        }
    }

    fun removeBookmark(storyId: String) {
        _userAccount.update { current ->
            val updatedBookmarks = current.bookmarks.toMutableMap()
            updatedBookmarks.remove(storyId)
            current.copy(bookmarks = updatedBookmarks)
        }
    }

    fun togglePageBookmark(storyId: String, pageIndex: Int): Boolean {
        val existing = _userAccount.value.bookmarks[storyId]
        return if (existing != null && existing.pageIndex == pageIndex) {
            removeBookmark(storyId)
            false
        } else {
            saveBookmark(storyId, pageIndex)
            true
        }
    }

    fun updateReadingProgress(storyId: String, pageIndex: Int) {
        _userAccount.update { current ->
            val updatedProgress = current.readingProgress.toMutableMap()
            updatedProgress[storyId] = pageIndex
            current.copy(readingProgress = updatedProgress)
        }
    }

    fun toggleFavorite(storyId: String) {
        _userAccount.update { current ->
            val favs = current.favoriteStoryIds.toMutableSet()
            if (favs.contains(storyId)) {
                favs.remove(storyId)
            } else {
                favs.add(storyId)
            }
            current.copy(favoriteStoryIds = favs)
        }
    }

    fun upgradeToPremium() {
        _userAccount.update { it.copy(isPremium = true) }
    }

    fun togglePlan() {
        _userAccount.update { it.copy(isPremium = !it.isPremium) }
    }

    fun selectAvatar(avatarId: String) {
        _userAccount.update { it.copy(selectedAvatarId = avatarId) }
    }

    fun updateChildName(name: String) {
        _userAccount.update { it.copy(name = name) }
    }

    fun setDailyReadingLimit(minutes: Int) {
        _userAccount.update { it.copy(dailyReadingLimitMinutes = minutes) }
    }

    fun setDailyTimeLimitEnabled(enabled: Boolean) {
        _userAccount.update { it.copy(isDailyTimeLimitEnabled = enabled) }
    }

    fun setBedtimeModeEnabled(enabled: Boolean) {
        _userAccount.update { it.copy(isBedtimeModeEnabled = enabled) }
    }

    fun resetTodayReadingTime() {
        _userAccount.update { it.copy(todayReadingMinutes = 0) }
    }

    fun addReadingTime(minutes: Int) {
        _userAccount.update { 
            it.copy(
                todayReadingMinutes = it.todayReadingMinutes + minutes,
                readingMinutesThisWeek = it.readingMinutesThisWeek + minutes
            ) 
        }
    }

    fun recordStoryRead(storyId: String) {
        _userAccount.update {
            val newExp = it.currentExp + 60
            val shouldLevelUp = newExp >= it.nextLevelExp
            val newLevel = if (shouldLevelUp) it.currentLevel + 1 else it.currentLevel
            val levelTitle = when (newLevel) {
                1 -> "القارئ المبتدئ 🌟"
                2 -> "محب المغامرات 🚀"
                3 -> "بطل القراءة الفضي 🛡️"
                4 -> "فارس الحكايات الذهبي 👑"
                else -> "حكيم الأساطير الأسطوري 🧙‍♂️"
            }

            it.copy(
                storiesReadCount = it.storiesReadCount + 1,
                todayReadingMinutes = it.todayReadingMinutes + 10,
                readingMinutesThisWeek = it.readingMinutesThisWeek + 10,
                currentExp = if (shouldLevelUp) newExp - it.nextLevelExp else newExp,
                currentLevel = newLevel,
                levelTitle = levelTitle
            )
        }
    }

    fun rateStory(storyId: String, ratingOptionId: String) {
        val story = _stories.value.find { it.id == storyId }
        val storyTitle = story?.title ?: "قصة"
        val ratingObj = com.example.data.models.availableChildRatings.find { it.id == ratingOptionId }
        val emoji = ratingObj?.emoji ?: "⭐"

        _userAccount.update { current ->
            val updatedRatings = current.storyRatings.toMutableMap()
            val isFirstTimeRating = !updatedRatings.containsKey(storyId)
            updatedRatings[storyId] = ratingOptionId

            val bonusExp = if (isFirstTimeRating) 40 else 10
            val newExp = current.currentExp + bonusExp
            val shouldLevelUp = newExp >= current.nextLevelExp
            val newLevel = if (shouldLevelUp) current.currentLevel + 1 else current.currentLevel
            val levelTitle = when (newLevel) {
                1 -> "القارئ المبتدئ 🌟"
                2 -> "محب المغامرات 🚀"
                3 -> "بطل القراءة الفضي 🛡️"
                4 -> "فارس الحكايات الذهبي 👑"
                else -> "حكيم الأساطير الأسطوري 🧙‍♂️"
            }

            current.copy(
                storyRatings = updatedRatings,
                currentExp = if (shouldLevelUp) newExp - current.nextLevelExp else newExp,
                currentLevel = newLevel,
                levelTitle = levelTitle,
                storiesReadCount = if (isFirstTimeRating) current.storiesReadCount + 1 else current.storiesReadCount
            )
        }

        // Add to activities feed
        _recentActivities.update { current ->
            val newActivity = ActivityItem(
                id = System.currentTimeMillis().toString(),
                title = "تقييم $emoji لقصة \"$storyTitle\" من البطل ${_userAccount.value.name}",
                timeAgo = "الآن",
                iconType = "star"
            )
            listOf(newActivity) + current.take(9)
        }
    }

    fun updateDraftTitle(title: String) {
        _newStoryDraft.update { it.copy(title = title) }
    }

    fun updateDraftTargetAge(age: String) {
        _newStoryDraft.update { it.copy(targetAge = age) }
    }

    fun updateDraftPremium(isPremium: Boolean) {
        _newStoryDraft.update { it.copy(isPremium = isPremium) }
    }

    fun toggleDraftTag(tag: String) {
        _newStoryDraft.update { draft ->
            val tags = draft.selectedTags.toMutableSet()
            if (tags.contains(tag)) {
                tags.remove(tag)
            } else {
                tags.add(tag)
            }
            draft.copy(selectedTags = tags)
        }
    }

    fun updateDraftPageText(pageNumber: Int, newText: String) {
        _newStoryDraft.update { draft ->
            val updatedPages = draft.pages.map {
                if (it.pageNumber == pageNumber) it.copy(text = newText) else it
            }
            draft.copy(pages = updatedPages)
        }
    }

    fun addDraftPage() {
        _newStoryDraft.update { draft ->
            val newNum = draft.pages.size + 1
            val newPage = ExtractedDraftPage(
                pageNumber = newNum,
                text = "الصفحة الجديدة: استمرت المغامرة في أعماق الغابة واكتشف الأصدقاء سراً مدهشاً...",
                imageRes = R.drawable.story_forest_girl
            )
            draft.copy(pages = draft.pages + newPage)
        }
    }

    fun publishDraft(): Story {
        val draft = _newStoryDraft.value
        val newStory = Story(
            id = "story_${System.currentTimeMillis()}",
            title = draft.title,
            subtitle = draft.pages.firstOrNull()?.text?.take(60) ?: "قصة أطفال تفاعلية ممتعة ومميزة",
            ageRange = draft.targetAge,
            category = draft.selectedTags.firstOrNull() ?: "مغامرة",
            tags = draft.selectedTags.toList(),
            coverRes = R.drawable.story_forest_friends,
            isPremium = draft.isPremium,
            pages = draft.pages.map {
                StoryPage(
                    pageNumber = it.pageNumber,
                    text = it.text,
                    imageRes = it.imageRes
                )
            }
        )

        _stories.update { listOf(newStory) + it }
        _recentActivities.update {
            listOf(
                ActivityItem(
                    id = System.currentTimeMillis().toString(),
                    title = "تم نشر قصة جديدة بنجاح: \"${draft.title}\"",
                    timeAgo = "الآن",
                    iconType = "author"
                )
            ) + it
        }
        return newStory
    }

    fun addGeneratedStory(story: Story) {
        _stories.update { existing ->
            if (existing.none { it.id == story.id }) {
                listOf(story) + existing
            } else {
                existing
            }
        }
        _recentActivities.update { current ->
            listOf(
                ActivityItem(
                    id = System.currentTimeMillis().toString(),
                    title = "✨ تم تأليف حكاية جديدة بالذكاء الاصطناعي: \"${story.title}\"",
                    timeAgo = "الآن",
                    iconType = "star"
                )
            ) + current.take(15)
        }
    }

    private fun createInitialStories(): List<Story> {
        return listOf(
            Story(
                id = "forest_friends",
                title = "أصدقاء الغابة",
                subtitle = "تعلم الألوان وقيمة التعاون مع حيوانات الغابة اللطيفة",
                ageRange = "3-5 سنوات",
                category = "حيوانات",
                tags = listOf("حيوانات", "صداقة", "تعليمي"),
                coverRes = R.drawable.story_forest_friends,
                isPremium = false,
                viewsCount = 24500,
                completionRatePercent = 94,
                rating = 4.9,
                pages = listOf(
                    StoryPage(
                        pageNumber = 1,
                        text = "في غابة خضراء جميلة ودافئة، اجتمع الدب الكبير والأرنب السريع والثعلب اللطيف بجانب النار الدافئة يتناولون الحلوى ويتحدثون عن مغامراتهم الممتعة.",
                        imageRes = R.drawable.story_forest_friends
                    ),
                    StoryPage(
                        pageNumber = 2,
                        text = "قال الدب: \"ما رأيكم أن نصنع معاً لوحة ألوان كبيرة لأزهار الغابة؟\" فرح الأصدقاء بالفكرة وقرروا البحث عن أجمل الألوان بين الأشجار والتلال.",
                        imageRes = R.drawable.story_forest_girl
                    ),
                    StoryPage(
                        pageNumber = 3,
                        text = "جلس الثعلب تحت الشجرة الكبيرة يقرأ كتاب الألوان العجيب، واستطاع الجميع بالتعاون والمحبة إكمال أروع لوحة فنية رأتها الغابة في تاريخها!",
                        imageRes = R.drawable.story_fox_reading
                    )
                )
            ),
            Story(
                id = "magic_carpet",
                title = "رحلة البساط السحري",
                subtitle = "قصة مشوقة عن الشجاعة واكتشاف المدن العربية القديمة والتاريخ العريق",
                ageRange = "6-8 سنوات",
                category = "مغامرة",
                tags = listOf("مغامرة", "خيال", "تاريخ"),
                coverRes = R.drawable.story_magic_carpet,
                isPremium = true,
                viewsCount = 18900,
                completionRatePercent = 89,
                rating = 4.8,
                pages = listOf(
                    StoryPage(
                        pageNumber = 1,
                        text = "وجد الفتى الشجاع بساطاً قديماً في خزانة جده، وفجأة بدأ البساط يتوهج بنور ذهبي دافئ وارتفع في الهواء بلطف نحو السماء الصافية.",
                        imageRes = R.drawable.story_magic_carpet
                    ),
                    StoryPage(
                        pageNumber = 2,
                        text = "حلق البساط فوق القباب الذهبية والمآذن الشامخة والواحات الخضراء، وشاهد الفتى جمال العمران والأسواق التراثية القديمة من الأعلى.",
                        imageRes = R.drawable.story_magic_carpet
                    ),
                    StoryPage(
                        pageNumber = 3,
                        text = "تعلم الفتى أن الشجاعة وحب الاستكشاف هما مفتاح كل معرفة، وعاد إلى منزله يحمل في قلبه ذكريات رحلة لا تُنسى.",
                        imageRes = R.drawable.story_fox_reading
                    )
                )
            ),
            Story(
                id = "blue_whale",
                title = "أغنية الحوت الأزرق",
                subtitle = "قصة قبل النوم هادئة تأخذك في أعماق المحيط الساحر وتساعد على الاسترخاء",
                ageRange = "3-5 سنوات",
                category = "قبل النوم",
                tags = listOf("قبل النوم", "استرخاء", "حيوانات"),
                coverRes = R.drawable.story_blue_whale,
                isPremium = false,
                viewsCount = 31200,
                completionRatePercent = 97,
                rating = 5.0,
                pages = listOf(
                    StoryPage(
                        pageNumber = 1,
                        text = "في أعماق المحيط الهادئ الأزرق، يسبح الحوت اللطيف بهدوء بين الشعاب المرجانية الملونة، مرسلاً أنغاماً عذبة تهدهد أسماك البحر الصغيرة.",
                        imageRes = R.drawable.story_blue_whale
                    ),
                    StoryPage(
                        pageNumber = 2,
                        text = "تتراقص فقاعات الماء مع أضواء القمر الخافتة المتسللة عبر الأمواج، وتستعد جميع الكائنات البحرية لنوم عميق وهانئ في سلام وأمان.",
                        imageRes = R.drawable.story_blue_whale
                    ),
                    StoryPage(
                        pageNumber = 3,
                        text = "أغمض الحوت الأزرق عينيه بسلام، وتمنى لكل طفل صغير نوماً هادئاً وأحلاماً وردية مليئة بالسعادة والمحبة.",
                        imageRes = R.drawable.story_fox_reading
                    )
                )
            ),
            Story(
                id = "space_station",
                title = "محطة الفضاء والمستكشف الصغير",
                subtitle = "مغامرة علمية شيقة تكشف أسرار النجوم والمجرات البعيدة",
                ageRange = "9-12 سنة",
                category = "تعليمي",
                tags = listOf("تعليمي", "علوم", "فضاء", "مغامرة"),
                coverRes = R.drawable.story_space_station,
                isPremium = true,
                viewsCount = 14300,
                completionRatePercent = 86,
                rating = 4.7,
                pages = listOf(
                    StoryPage(
                        pageNumber = 1,
                        text = "ارتدى رائد الفضاء الصغير بدلته البيضاء وصعد إلى محطة الفضاء الدولية، حيث تطفو الأشياء بانعدام الجاذبية في مشهد ساحر ومدهش.",
                        imageRes = R.drawable.story_space_station
                    ),
                    StoryPage(
                        pageNumber = 2,
                        text = "نظر من خلال النافذة الزجاجية الكبيرة فرأى كوكب الأرض كأنه جوهرة زرقاء تلمع في ظلام الكون الواسع المليء بالنجوم المتلألئة.",
                        imageRes = R.drawable.story_space_station
                    ),
                    StoryPage(
                        pageNumber = 3,
                        text = "أجرى تجربة علمية ممتعة وسجل ملاحظاته في مذكرات المستكشف، واعداً نفسه بأن يواصل دراسة العلوم لتحقيق أحلامه الكبيرة.",
                        imageRes = R.drawable.story_fox_reading
                    )
                )
            ),
            Story(
                id = "nora_enchanted_forest",
                title = "نورة في الغابة المسحورة",
                subtitle = "مغامرة نورة واكتشاف أسرار الفطر المضيء وروح الغابة الطيبة",
                ageRange = "6-8 سنوات",
                category = "خيالي",
                tags = listOf("خيال", "مغامرة", "طبيعة"),
                coverRes = R.drawable.story_forest_girl,
                isPremium = false,
                viewsCount = 28700,
                completionRatePercent = 95,
                rating = 4.9,
                pages = listOf(
                    StoryPage(
                        pageNumber = 1,
                        text = "سارت نورة بمعطفها الأصفر الأنيق داخل الغابة المسحورة، وكانت الفراشات المضيئة ترشدها إلى دروب خفية لم يرها أحد من قبل.",
                        imageRes = R.drawable.story_forest_girl
                    ),
                    StoryPage(
                        pageNumber = 2,
                        text = "توقفت عند شجرة عملاقة نمت حولها نباتات فطر عملاقة تشع نوراً بنفسجياً وذهبياً، وظهر لها ثعلب أزرق صغير يرحب بها بابتسامة دافئة.",
                        imageRes = R.drawable.story_forest_girl
                    ),
                    StoryPage(
                        pageNumber = 3,
                        text = "علمت نورة أن الغابة تحمي من يحبها ويحافظ على أشجارها وحيواناتها، ووعدت أصدقاءها الجدد بزيارتهم كل ربيع.",
                        imageRes = R.drawable.story_fox_reading
                    )
                )
            )
        )
    }
}
