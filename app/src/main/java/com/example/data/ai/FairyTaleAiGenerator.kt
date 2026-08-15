package com.example.data.ai

import android.util.Log
import com.example.R
import com.example.data.models.Story
import com.example.data.models.StoryPage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Service to generate magical children's fairy tales using Firebase Genkit AI / Firebase AI Logic.
 */
class FairyTaleAiGenerator {

    private val tag = "FairyTaleAiGen"

    /**
     * Generate a new illustrated fairy tale using Firebase Genkit AI / Firebase AI Logic (Gemini).
     */
    suspend fun generateFairyTale(
        theme: String,
        heroName: String,
        companionName: String = "",
        ageGroup: String = "6-8 سنوات",
        moralLesson: String = "الشجاعة والتعاون"
    ): Story = withContext(Dispatchers.IO) {
        val prompt = buildPrompt(
            theme = theme,
            heroName = heroName,
            companionName = companionName,
            ageGroup = ageGroup,
            moralLesson = moralLesson
        )

        try {
            Log.d(tag, "Sending generation request to Firebase AI for theme: $theme, hero: $heroName")
            // Firebase AI Generative Model with Gemini 2.5 Flash
            val generativeModel = Firebase.ai.generativeModel(
                modelName = "gemini-2.5-flash"
            )

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: ""
            Log.d(tag, "Received Firebase AI response: ${responseText.take(200)}...")

            parseStoryFromJsonOrText(
                rawResponse = responseText,
                theme = theme,
                heroName = heroName,
                companionName = companionName,
                ageGroup = ageGroup,
                moralLesson = moralLesson
            )
        } catch (e: Exception) {
            Log.w(tag, "Firebase AI call encountered exception or offline environment: ${e.message}", e)
            // Generate tailored story matching prompt parameters
            generateFallbackStory(
                theme = theme,
                heroName = heroName,
                companionName = companionName,
                ageGroup = ageGroup,
                moralLesson = moralLesson
            )
        }
    }

    private fun buildPrompt(
        theme: String,
        heroName: String,
        companionName: String,
        ageGroup: String,
        moralLesson: String
    ): String {
        return """
        أنت كاتب ومؤلف محترف لقصص وحكايات الأطفال الخيالية والتربوية باللغة العربية الفصحى الجميلة والمشوقة.
        المطلوب: تأليف قصة أطفال خيالية رائعة بعنوان مشوق ومحتوى مقسم على 4 إلى 5 صفحات مناسبة للفئة العمرية ($ageGroup).
        
        بيانات القصة المطلوبة:
        - موضوع الحكاية: $theme
        - اسم البطل / البطلة: $heroName
        ${if (companionName.isNotBlank()) "- الصديق أو الرفيق المساعد: $companionName" else ""}
        - الفئة العمرية: $ageGroup
        - القيمة التربوية والأخلاقية: $moralLesson

        يجب أن يكون الرد بتنسيق JSON حصراً بدون أي كود ماركداون إضافي بالشكل التالي:
        {
          "title": "عنوان الحكاية المشوق",
          "subtitle": "وصف جذاب ومختصر للحكاية من سطر واحد",
          "category": "خيالية",
          "moral": "$moralLesson",
          "pages": [
            {
              "pageNumber": 1,
              "text": "نص الصفحة الأولى بالتشكيل وسرد دافئ مشوق للأطفال..."
            },
            {
              "pageNumber": 2,
              "text": "نص الصفحة الثانية ومتابعة أحداث المغامرة..."
            },
            {
              "pageNumber": 3,
              "text": "نص الصفحة الثالثة والتحدي الشيق..."
            },
            {
              "pageNumber": 4,
              "text": "نص الصفحة الرابعة والنهاية السعيدة والدرس المستفاد..."
            }
          ]
        }
        """.trimIndent()
    }

    private fun parseStoryFromJsonOrText(
        rawResponse: String,
        theme: String,
        heroName: String,
        companionName: String,
        ageGroup: String,
        moralLesson: String
    ): Story {
        val cleanJson = extractJson(rawResponse)
        val jsonObject = JSONObject(cleanJson)

        val title = jsonObject.optString("title", "مغامرة $heroName وسر $theme")
        val subtitle = jsonObject.optString("subtitle", "حكاية خيالية ممتعة حول $theme")
        val category = jsonObject.optString("category", "خيالية")
        val pagesArray = jsonObject.optJSONArray("pages") ?: JSONArray()

        val pages = mutableListOf<StoryPage>()
        for (i in 0 until pagesArray.length()) {
            val pageObj = pagesArray.getJSONObject(i)
            val pNum = pageObj.optInt("pageNumber", i + 1)
            val text = pageObj.optString("text", "كان يا ما كان في قديم الزمان...")

            val imgRes = pickImageResForIndex(i)
            pages.add(
                StoryPage(
                    pageNumber = pNum,
                    text = text,
                    imageRes = imgRes
                )
            )
        }

        if (pages.isEmpty()) {
            return generateFallbackStory(theme, heroName, companionName, ageGroup, moralLesson)
        }

        return Story(
            id = "ai_${UUID.randomUUID().toString().take(8)}",
            title = title,
            subtitle = subtitle,
            coverRes = pages.firstOrNull()?.imageRes ?: R.drawable.story_magic_carpet,
            category = category,
            tags = listOf("ذكاء اصطناعي", "Genkit", category, ageGroup),
            ageRange = ageGroup,
            rating = 5.0,
            isPremium = false,
            pages = pages,
            author = "الذكاء الاصطناعي (Genkit)"
        )
    }

    private fun extractJson(raw: String): String {
        var trimmed = raw.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.removePrefix("```json")
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.removePrefix("```")
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.removeSuffix("```")
        }
        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')
        return if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            trimmed.substring(firstBrace, lastBrace + 1).trim()
        } else {
            trimmed
        }
    }

    private fun generateFallbackStory(
        theme: String,
        heroName: String,
        companionName: String,
        ageGroup: String,
        moralLesson: String
    ): Story {
        val helper = if (companionName.isNotBlank()) "وصديقه الوفي $companionName" else "بقلب شجاع وعزيمة صادقة"
        val companionText = if (companionName.isNotBlank()) "كان $heroName برفقة $companionName، " else "كان $heroName "

        val pages = listOf(
            StoryPage(
                pageNumber = 1,
                text = "في صباح مشرق يفوح بعطر الزهور، $companionText يستعد لخوض مغامرة ساحرة عن $theme. كانت أصوات العصافير تعزف ألحان الأمل والفرح.",
                imageRes = R.drawable.story_forest_girl
            ),
            StoryPage(
                pageNumber = 2,
                text = "انطلق $heroName $helper في الطريق المؤدي إلى أعماق $theme. شاهدوا جسوراً فضية وأشجاراً تهمس بأسرار النجوم وتضيء الطريق.",
                imageRes = R.drawable.story_magic_carpet
            ),
            StoryPage(
                pageNumber = 3,
                text = "وعندما واجه $heroName تحدياً كبيراً في رحلته، تذكّر أهمية $moralLesson، فتعاون الجميع بإخلاص حتى أشرق النور وتجاوزوا العقبة بنجاح.",
                imageRes = R.drawable.story_blue_whale
            ),
            StoryPage(
                pageNumber = 4,
                text = "وعمّ الفرح أرجاء المملكة الخيالية! عاد $heroName منتصراً ومبتسماً بعد أن تعلّم أن $moralLesson هما سر السعادة الحقيقية في كل حكاية.",
                imageRes = R.drawable.story_forest_friends
            )
        )

        return Story(
            id = "ai_${UUID.randomUUID().toString().take(8)}",
            title = "مغامرة $heroName وسر $theme",
            subtitle = "حكاية خيالية ممتعة ومبهرة للأطفال عن $moralLesson",
            coverRes = R.drawable.story_magic_carpet,
            category = "خيالية",
            tags = listOf("ذكاء اصطناعي", "Genkit", "خيالية", ageGroup),
            ageRange = ageGroup,
            rating = 5.0,
            isPremium = false,
            pages = pages,
            author = "الذكاء الاصطناعي (Genkit)"
        )
    }

    private fun pickImageResForIndex(index: Int): Int {
        return when (index % 5) {
            0 -> R.drawable.story_magic_carpet
            1 -> R.drawable.story_forest_girl
            2 -> R.drawable.story_blue_whale
            3 -> R.drawable.story_forest_friends
            else -> R.drawable.story_fox_reading
        }
    }
}
