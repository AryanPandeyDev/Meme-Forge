package com.example.memeforge.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.memeforge.authentication.Authentication
import com.example.memeforge.authentication.User
import com.example.memeforge.network.implementation.MemeServiceImpl
import com.example.memeforge.screens.EditMemeScreen
import com.example.memeforge.screens.FavMemeScreen
import com.example.memeforge.screens.LoginScreen
import com.example.memeforge.screens.SignUpScreen

import com.example.memeforge.screens.TemplateScreen
import com.example.memeforge.screens.WelcomeScreen
import kotlinx.coroutines.launch


@Composable
fun Navigation() {
    val context = LocalContext.current
    val authentication = remember { Authentication(context) }
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (authentication.isSingedIn()) Route.TemplateScreen else Route.WelcomeScreen
    ) {

        composable<Route.WelcomeScreen> {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Route.LoginScreen) },
                onSignUpClick = { navController.navigate(Route.SignUpScreen) }
            )
        }


        composable<Route.LoginScreen> {
            LoginScreen(
                nextScreen = {
                    navController.navigate(Route.TemplateScreen) {
                        popUpTo(Route.LoginScreen) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.navigate(Route.WelcomeScreen) {
                        popUpTo(Route.LoginScreen) {inclusive = true}
                    }
                }
            )
        }

        composable<Route.SignUpScreen> {
            SignUpScreen(
                nextScreen = {
                    navController.navigate(Route.TemplateScreen) {
                        popUpTo(Route.LoginScreen) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.navigate(Route.WelcomeScreen) {
                        popUpTo(Route.LoginScreen) {inclusive = true}
                    }
                }
            )
        }

        composable<Route.TemplateScreen> {
            TemplateScreen(
                user = User(
                    image = authentication.firebaseAuth.currentUser?.photoUrl.toString(),
                    name = authentication.firebaseAuth.currentUser?.displayName.toString()
                        .split(" ").firstOrNull() ?: "Aryan",
                    email = authentication.firebaseAuth.currentUser?.email.toString()
                ),
                onSignOut = {
                    navController.navigate(Route.WelcomeScreen) {
                        popUpTo(Route.TemplateScreen) { inclusive = true }
                    }
                    coroutineScope.launch {
                        authentication.signOut()
                    }
                },
                navigateToFav = {
                    navController.navigate(Route.FavMemeScreen)
                },
                openMeme = { url, imageName ->
                    navController.navigate(Route.EditMemeScreen(
                        imageName = imageName,
                        url = url
                    ))
                },
                sendImageFromDevice = { uri ->
                    navController.navigate(Route.EditMemeScreen(
                        imageFromDevice = uri,
                        url = "null",
                        imageName = "image from device"
                    ))
                }
            )
        }
        composable<Route.EditMemeScreen> {
            val args = it.toRoute<Route.EditMemeScreen>()
            EditMemeScreen(
                imageFromDevice = args.imageFromDevice,
                navigateBack = {
                    navController.navigate(Route.TemplateScreen) {
                        popUpTo(Route.TemplateScreen) { inclusive = true }
                    }
                },
                imageName = args.imageName,
                url = args.url
            )
        }
        composable<Route.FavMemeScreen> {
            FavMemeScreen {
                navController.navigate(Route.TemplateScreen) {
                    popUpTo(Route.TemplateScreen) { inclusive = true }
                }
            }
        }
    }
}