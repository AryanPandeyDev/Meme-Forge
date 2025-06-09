package com.example.memeforge.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memeforge.R
import com.example.memeforge.authentication.Authentication
import com.example.memeforge.design.getResponsiveDimensions
import com.example.memeforge.ui.theme.MemeForgeTheme
import com.example.memeforge.utils.getWindowSizeClass
import kotlinx.coroutines.launch

@Composable
@Preview(
    showSystemUi = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode Preview"
)
fun LoginScreenPreview(){
    MemeForgeTheme(true) {
        LoginScreen(
            {}
        )
    }
}

@Composable
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode Preview"
)
fun LightLoginScreenPreview(){
    MemeForgeTheme(false) {
        LoginScreen (
            {}
        )
    }
}

@Composable
fun LoginScreen(
    nextScreen: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val authenticator = remember { Authentication(context) }
    val coroutineScope = rememberCoroutineScope()
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isSignInError by authenticator.signInError.collectAsState()
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    val scrollState = rememberScrollState()
    val isSigningIn = authenticator.isSigningIn.collectAsState()

    // Use Responsive Dimensions
    val sizeClass = getWindowSizeClass()
    val dimensions = getResponsiveDimensions(sizeClass)

    // monochrome gradient background
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3A3A3A), // lighter top
            Color(0xFF2A2A2A), // smooth mid
            Color(0xFF1A1A1A)  // deep bottom
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(dimensions.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Back Arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensions.sectionSpacing),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // App Logo and Title
            Card(
                modifier = Modifier.size(dimensions.iconSize - 20.dp),
                shape = RoundedCornerShape(dimensions.cornerRadius + 4.dp)
            ) {
                Image(
                    painterResource(R.drawable.memeforgeicon),
                    contentDescription = "App Icon"
                )
            }

            Spacer(modifier = Modifier.height(dimensions.itemSpacing))

            Text(
                text = "Meme Forge",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensions.sectionSpacing))

            // Login Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadius + 6.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
            ) {
                Column(
                    modifier = Modifier.padding(dimensions.cardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = dimensions.itemSpacing)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = emailState,
                        onValueChange = { emailState = it },
                        singleLine = true,
                        label = {
                            Text(
                                "Email Address",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF666666)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = isSignInError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight + 24.dp)
                            .padding(bottom = dimensions.itemSpacing),
                        shape = RoundedCornerShape(dimensions.cornerRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF333333),
                            focusedLabelColor = Color(0xFF333333),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Black
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = passwordState,
                        onValueChange = { passwordState = it },
                        label = {
                            Text(
                                "Password",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF666666)
                            )
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF666666)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = isSignInError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight + 24.dp)
                            .padding(bottom = dimensions.itemSpacing),
                        shape = RoundedCornerShape(dimensions.cornerRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF333333),
                            focusedLabelColor = Color(0xFF333333),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            errorTextColor = Color.Black
                        )
                    )

                    // Sign In Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                when {
                                    !emailState.matches(emailPattern.toRegex()) ->
                                        Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                                    passwordState.length < 6 ->
                                        Toast.makeText(context, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
                                    else -> {
                                        authenticator.login(emailState, passwordState)
                                        if (authenticator.isSingedIn()) nextScreen()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(dimensions.cornerRadius + 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = dimensions.cardElevation / 2)
                    ) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensions.itemSpacing))

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = dimensions.itemSpacing),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                        Divider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
                    }

                    Spacer(modifier = Modifier.height(dimensions.itemSpacing))

                    // Google Sign In Button
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                authenticator.signIn()
                                if (authenticator.isSingedIn()) nextScreen()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(dimensions.cornerRadius + 2.dp),
                        border = BorderStroke(dimensions.borderWidth, Color.Gray.copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.googleicon),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(dimensions.itemSpacing))
                            Text(
                                text = "Continue with Google",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensions.sectionSpacing))
        }

        if (isSigningIn.value) {
            ScreenLoading()
        }
    }
}

@Composable
fun ScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.5f)), // Semi-transparent background
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Blue // You can customize the color here
        )
    }
}