package com.tinaut1986.pricesmart

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
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
import com.tinaut1986.pricesmart.model.Product
import com.tinaut1986.pricesmart.navigation.ProductScreen
import com.tinaut1986.pricesmart.ui.screens.AddEditProductScreen
import com.tinaut1986.pricesmart.ui.screens.CompareScreen
import com.tinaut1986.pricesmart.ui.screens.SettingsScreen
import com.tinaut1986.pricesmart.ui.theme.PriceSmartTheme
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PriceSmartTheme {
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
                val appLocales = AppCompatDelegate.getApplicationLocales()
                val currentLocale = if (!appLocales.isEmpty) {
                    appLocales.get(0)
                } else {
                    java.util.Locale.getDefault()
                }

                when (currentLocale?.language) {
                    "en" -> "English"
                    "ca" -> "Català"
                    "gl" -> "Galego"
                    "eu" -> "Euskara"
                    "fr" -> "Français"
                    "pt" -> "Português"
                    "de" -> "Deutsch"
                    "nl" -> "Nederlands"
                    "zh" -> "中文"
                    "ja" -> "日本語"
                    "es" -> "Español"
                    else -> "Español"
                }
            }       
        )
    }

    PriceSmartTheme(darkTheme = isDarkMode) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // Root Row handles safe areas for the side navigation
        Row(modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing) // This is the key fix for the notch!
        ) {
            if (isLandscape) {
                NavigationRail(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)
                ) {
                    listOf(ProductScreen.Compare, ProductScreen.Settings).forEach { screen ->
                        NavigationRailItem(
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
                    if (!isLandscape) {
                        NavigationBar(
                            containerColor = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)
                        ) {
                            listOf(ProductScreen.Compare, ProductScreen.Settings).forEach { screen ->
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
                },
                floatingActionButton = {
                    if (currentRoute == ProductScreen.Compare.route) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(ProductScreen.Add.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White,
                            shape = androidx.compose.foundation.shape.CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_product_title)
                            )
                        }
                    }
                },
                // We use WindowInsets.none() because we already handle safeDrawing in the outer Row
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    NavHost(
                        navController = navController, 
                        startDestination = ProductScreen.Compare.route,
                        modifier = Modifier.fillMaxSize()
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
                                },
                                onTutorialFinish = {
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
                                        "Français" -> "fr"
                                        "Português" -> "pt"
                                        "Deutsch" -> "de"
                                        "Nederlands" -> "nl"
                                        "中文" -> "zh"
                                        "日本語" -> "ja"
                                        else -> "es"
                                    }
                                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
                                },
                                onNavigateToAdd = {
                                    navController.navigate(ProductScreen.Add.route) {
                                        popUpTo(ProductScreen.Settings.route) {
                                            inclusive = true
                                        }
                                    }
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
                                    },
                                    onTutorialFinish = {
                                        navController.navigate(ProductScreen.Compare.route) {
                                            popUpTo(ProductScreen.Compare.route) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
