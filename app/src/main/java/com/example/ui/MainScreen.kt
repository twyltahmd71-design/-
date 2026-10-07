package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.screens.DeveloperScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.TranslatorScreen
import com.example.ui.theme.BabylonGold
import com.example.ui.theme.IraqiNavyCard
import com.example.ui.theme.IraqiNavyDeep

@Composable
fun MainScreen(
    viewModel: TranslatorViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()

    // Handle back button: if in History or Developer tab, navigate back to Translator tab (Home)
    BackHandler(enabled = currentTab != 0) {
        viewModel.selectTab(0)
    }

    // Force RTL layout direction for Arabic app feel
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = IraqiNavyDeep,
            bottomBar = {
                NavigationBar(
                    containerColor = IraqiNavyCard,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "المترجم"
                            )
                        },
                        label = { Text("المترجم") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IraqiNavyDeep,
                            selectedTextColor = BabylonGold,
                            indicatorColor = BabylonGold,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_translator")
                    )

                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "سجل الترجمات"
                            )
                        },
                        label = { Text("سجل الترجمات") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IraqiNavyDeep,
                            selectedTextColor = BabylonGold,
                            indicatorColor = BabylonGold,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_history")
                    )

                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "عن المطور"
                            )
                        },
                        label = { Text("عن المطور") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IraqiNavyDeep,
                            selectedTextColor = BabylonGold,
                            indicatorColor = BabylonGold,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_developer")
                    )
                }
            }
        ) { innerPadding ->
            when (currentTab) {
                0 -> TranslatorScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                1 -> HistoryScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                2 -> DeveloperScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
