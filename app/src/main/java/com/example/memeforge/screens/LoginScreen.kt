package com.example.memeforge.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memeforge.R
import com.example.memeforge.authentication.Authentication
import com.example.memeforge.ui.theme.MemeForgeTheme
import kotlinx.coroutines.launch

@Composable
@Preview(
    showSystemUi = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_NIGHT_YES, // Enables Dark Theme
    name = "Dark Mode Preview"
)
fun LoginScreenPreview(){
    MemeForgeTheme {
        LoginScreen {  }
    }
}

@Composable
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO, // Enables Dark Theme
    name = "Dark Mode Preview"
)
fun LightLoginScreenPreview(){
    MemeForgeTheme {
        LoginScreen{}
    }
}

@Composable
fun LoginScreen(
    nextScreen : () -> Unit
) {
    val context = LocalContext.current
    val authenticator = remember { Authentication(context) }
    var isSignIn by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    val isSignInError by authenticator.signInError.collectAsState()
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    val googleSignInError by authenticator.signInError.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ){
            Image(
                painter = painterResource(id = R.drawable.loginbackground),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopEnd)
                    .alpha(0.1f),
                contentScale = ContentScale.Crop
            )
            Image(
                modifier = Modifier.size(350.dp)
                    .align(Alignment.TopCenter)
                    .padding(
                        top = 20.dp,
                        start = 30.dp,
                        end = 30.dp,
                        bottom = 50.dp
                    )
                    .height(300.dp),
                painter = painterResource(id = if (isSignIn) R.drawable.login else R.drawable.signup),
                contentDescription = null,
            )
            Box(
                Modifier.height(490.dp).fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)

            )
            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
                    .wrapContentSize()
                    .padding(bottom = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (isSignIn) "Login" else "Sign Up",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                        .padding(start = 40.dp),
                    fontSize = 35.sp
                )
                OutlinedTextField(
                    value = emailState,
                    onValueChange = { emailState = it },
                    isError = isSignInError,
                    label = {
                        Row {
                            Icon(
                                modifier = Modifier.padding(end = 10.dp),
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                            Text("Email ID")
                        }
                    }
                        ,
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            top = 20.dp,
                            start = 35.dp,
                            end = 35.dp,
                            bottom = 10.dp
                        ),
                )
                OutlinedTextField(
                    value = passwordState,
                    onValueChange = { passwordState = it },
                    isError = isSignInError,
                    label = {
                        Row {
                            Icon(
                                modifier = Modifier.padding(end = 10.dp),
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                            Text("Password")
                        } },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 35.dp),
                )
                Box(Modifier.padding(top = 35.dp, start = 35.dp, end = 35.dp, bottom = 18.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp)
                        ,
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            coroutineScope.launch {
                                if (!emailState.matches(emailPattern.toRegex()))
                                    Toast.makeText(context,"Please enter a valid email or password",Toast.LENGTH_SHORT).show()
                                else if (passwordState.length < 6)
                                    Toast.makeText(context,"the password should atleast be of 6 characters",Toast.LENGTH_SHORT).show()
                                else {
                                    if (isSignIn) {
                                        authenticator.login(emailState,passwordState)
                                        if (authenticator.isSingedIn()) nextScreen()
                                    }
                                    else {
                                        authenticator.register(emailState,passwordState)
                                        if (authenticator.isSingedIn()) nextScreen()
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            if (isSignIn) "Login" else "Sign Up",
                            fontSize = 18.sp
                        )
                    }
                }
                Text(if (isSignIn) "Or, login with..." else "Or, Sign Up using...")
                Box(Modifier.padding(top = 18.dp, start = 35.dp, end = 35.dp, bottom = 20.dp)) {
                    Button(
                        border = BorderStroke(
                            color = MaterialTheme.colorScheme.outline,
                            width = 1.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp)
                        ,
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            coroutineScope.launch {
                                authenticator.signIn()
                                if (authenticator.isSingedIn()) nextScreen()
                            }
                        }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.googleicon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(2.dp)
                        )
                    }
                }
                Row {
                    Text("New to MemeForge?")
                    Spacer(Modifier.width(8.dp))
                    Text("Register",
                        modifier = Modifier.clickable {
                            emailState = ""
                            passwordState = ""
                            isSignIn = !isSignIn
                        },
                        color = MaterialTheme.colorScheme.primary)
                }
            }

        }
    }
}