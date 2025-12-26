package com.example.testapplication.navigation

import androidx.annotation.StringRes
import com.example.testapplication.R

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ProductScreen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    object Compare : ProductScreen("compare", R.string.screen_compare, Icons.Filled.CompareArrows)
    object Add : ProductScreen("add", R.string.screen_add, Icons.Filled.AddCircle)
    object Settings : ProductScreen("settings", R.string.screen_settings, Icons.Filled.Settings)
    object Edit : ProductScreen("edit/{productId}", R.string.screen_edit, Icons.Filled.Edit) {
        fun createRoute(productId: Long) = "edit/$productId"
    }
}
