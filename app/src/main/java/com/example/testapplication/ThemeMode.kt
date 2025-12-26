package com.example.testapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ThemeMode : Parcelable {
    SYSTEM, LIGHT, DARK
}
