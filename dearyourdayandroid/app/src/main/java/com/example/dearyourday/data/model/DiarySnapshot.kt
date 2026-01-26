package com.example.dearyourday.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiarySnapshot(
    val content: String = "",
    val moodCode: String = ""
) : Parcelable
