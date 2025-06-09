package com.example.memeforge.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memeforge.R
import com.example.memeforge.design.getResponsiveDimensions
import com.example.memeforge.utils.getWindowSizeClass
import kotlinx.coroutines.delay
@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val sizeClass = getWindowSizeClass()
    LaunchedEffect(Unit) {
        Log.d("ScreenSize",sizeClass.toString())
    }
    val dimensions = getResponsiveDimensions(sizeClass)

    var isVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse),
        label = "gradient_offset"
    )

    val rotationAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    val gradientColors = listOf(
        Color(0xFF6366F1),
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFFF59E0B),
        Color(0xFF1E1E2F)
    )

    val monoTextColor = Color(0xFFEEEEEE)
    val monoLightGray = Color(0xFFBBBBBB)
    val monoBackgroundCard = Color(0x22FFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = Offset(
                        x = 0.5f + animatedOffset * 0.3f,
                        y = 0.5f + animatedOffset * 0.2f
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(dimensions.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(tween(800)) + fadeIn(tween(800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(dimensions.iconSize)
                            .rotate(rotationAnimation * 0.1f)
                            .clip(RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(R.drawable.memeforgeicon),
                            contentDescription = "App Icon"
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensions.itemSpacing))

                    Text(
                        text = "Meme Forge",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = monoTextColor,
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    Text(
                        text = "UNLEASH YOUR CREATIVITY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = monoLightGray,
                            letterSpacing = 3.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(tween(1000, delayMillis = 400)) + fadeIn(tween(1000, delayMillis = 400))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = monoBackgroundCard),
                    shape = RoundedCornerShape(dimensions.cornerRadius),
                ) {
                    Text(
                        text = "Welcome to Meme Forge — Your ultimate meme-making companion! Use templates and unleash AI-powered captions to craft the funniest memes in seconds.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = monoTextColor,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.padding(dimensions.cardPadding)
                    )
                }
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(tween(1200, delayMillis = 800)) + fadeIn(tween(1200, delayMillis = 800))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing)
                ) {
                    Button(
                        onClick = onSignUpClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(dimensions.buttonHeight / 2),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = dimensions.cardElevation)
                    ) {
                        Text(
                            text = "GET STARTED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = monoTextColor),
                        border = BorderStroke(dimensions.borderWidth, monoTextColor.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(dimensions.buttonHeight / 2)
                    ) {
                        Text(
                            text = "LOGIN",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        repeat(5) { index ->
            val floatAnimation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000 + index * 500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "float_$index"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (50 + index * 70).dp,
                        y = (100 + index * 120 + floatAnimation).dp
                    )
                    .size((20 + index * 5).dp)
                    .background(
                        color = monoTextColor.copy(alpha = 0.1f + index * 0.05f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen()
    }
}