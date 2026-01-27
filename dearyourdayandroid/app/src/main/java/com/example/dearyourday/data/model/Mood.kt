package com.example.dearyourday.data.model

enum class Mood(val description: String, val emoji: String) {
    HAPPY("행복해요", "😊"),
    GOOD("좋아요", "😌"),
    SOSO("그저 그래요", "😐"),
    TIRED("지쳤어요", "😴"),
    SAD("슬퍼요", "😢"),
    ANGRY("화가 나요", "😡"),
    CONFUSED("잘 모르겠어요", "🤔");

    companion object {
        fun from(name: String?): Mood? {
            return entries.find { it.name == name }
        }
    }
}