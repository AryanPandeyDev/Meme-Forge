package com.example.memeforge.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.memeforge.authentication.Authentication
import com.example.memeforge.screens.LoginScreen

import com.example.memeforge.screens.TemplateScreen
import kotlinx.coroutines.launch


@Composable
fun Navigation() {
    val context = LocalContext.current
    val authentication = remember { Authentication(context) }
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (authentication.isSingedIn()) Route.TemplateScreen else Route.LoginScreen
    ) {
        composable<Route.LoginScreen> {
            LoginScreen {
                navController.navigate(Route.TemplateScreen)
                navController.clearBackStack<Route.LoginScreen>()
            }
        }
        composable<Route.TemplateScreen> {
            TemplateScreen(
                authentication
            ){
                navController.navigate(Route.LoginScreen)
                navController.clearBackStack<Route.TemplateScreen>()
                coroutineScope.launch {
                    authentication.signOut()
                }
            }
        }
    }
}