package com.example.testapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.testapplication.model.Product
import com.example.testapplication.navigation.ProductScreen
import com.example.testapplication.ui.screens.AddEditProductScreen
import com.example.testapplication.ui.screens.CompareScreen
import com.example.testapplication.ui.screens.SettingsScreen
import com.example.testapplication.ui.theme.TestApplicationTheme
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestApplicationTheme {
                PriceComparatorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceComparatorApp() {
    val navController = rememberNavController()
    val products = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<Product>() }
    
    // Global states
    val themeModeState = rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    val isDarkMode = when (themeModeState.value) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    val languageState = rememberSaveable { 
        mutableStateOf(
            run {
                val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)
                when (currentLocale?.language) {
                    "en" -> "English"
                    "ca" -> "Català"
                    "gl" -> "Galego"
                    "eu" -> "Euskara"
                    else -> "Español"
                }
            }       
        )
    }

    TestApplicationTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            stringResource(R.string.app_name), 
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2E7D32),
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    listOf(ProductScreen.Compare, ProductScreen.Add, ProductScreen.Settings).forEach { screen ->
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    screen.icon, 
                                    contentDescription = null,
                                    tint = if (currentRoute == screen.route) Color(0xFF2E7D32) else Color.Gray
                                ) 
                            },
                            label = { 
                                Text(
                                    stringResource(screen.titleRes),
                                    color = if (currentRoute == screen.route) Color(0xFF2E7D32) else Color.Gray
                                ) 
                            },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController, 
                startDestination = ProductScreen.Compare.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(ProductScreen.Compare.route) { 
                    CompareScreen(
                        isDarkMode = isDarkMode,
                        products = products,
                        onAddClick = { 
                            navController.navigate(ProductScreen.Add.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onEditClick = { product -> 
                            navController.navigate(ProductScreen.Edit.createRoute(product.id))
                        }
                    ) 
                }
                composable(ProductScreen.Add.route) { 
                    AddEditProductScreen(
                        isDarkMode = isDarkMode,
                        onProductAction = { product ->
                            products.add(product)
                            navController.navigate(ProductScreen.Compare.route) {
                                popUpTo(ProductScreen.Compare.route) { inclusive = true }
                            }
                        }
                    ) 
                }
                composable(ProductScreen.Settings.route) {
                    SettingsScreen(
                        themeMode = themeModeState.value,
                        onThemeModeChange = { themeModeState.value = it },
                        isDarkMode = isDarkMode,
                        currentLanguage = languageState.value,
                        onLanguageChange = { newLanguage ->
                            languageState.value = newLanguage
                            val localeTag = when (newLanguage) {
                                "English" -> "en"
                                "Català" -> "ca"
                                "Galego" -> "gl"
                                "Euskara" -> "eu"
                                else -> "es"
                            }
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
                        }
                    )
                }
                composable(ProductScreen.Edit.route) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
                    val product = products.find { it.id == productId }
                    if (product != null) {
                        AddEditProductScreen(
                            isDarkMode = isDarkMode,
                            existingProduct = product,
                            onProductAction = { updatedProduct ->
                                val index = products.indexOfFirst { it.id == updatedProduct.id }
                                if (index != -1) {
                                    products[index] = updatedProduct
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
