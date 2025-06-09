package com.example.memeforge.screencomponenets


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.memeforge.R
import com.example.memeforge.screens.FavMemeScreen
import com.example.memeforge.ui.theme.MemeForgeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@Composable
fun AiPromptDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    aiResponse: String?,
    isLoading: Boolean
) {
    val clipboardManager = LocalClipboardManager.current
    var prompt by remember { mutableStateOf("") }
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Caption Generator",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                AnimatedVisibility(
                    visible = aiResponse != null && !isLoading
                ) {
                    IconButton(onClick = {
                        if (aiResponse != null) {
                            clipboardManager.setText(AnnotatedString(aiResponse))
                            copied = true
                        }
                    }) {
                        if (copied) {
                            Icon(
                                imageVector = Icons.Default.Check ,
                                contentDescription = "Copied"
                            )
                        }else {
                            Icon(
                                painter = painterResource(R.drawable.baseline_content_copy_24),
                                contentDescription = "Copy Response"
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter your prompt") },
                    placeholder = {
                        Text("For eg: give me a funny caption regarding [topic]")
                    },
                    enabled = aiResponse == null && !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading || aiResponse != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Generating caption...")
                                }
                            }
                        } else {
                            Text(text = aiResponse ?: "")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (aiResponse != null) {
                Button(onClick = onDismiss) {
                    Text("Done")
                }
            } else {
                Button(
                    onClick = { if (prompt.isNotBlank()) onSend(prompt) },
                    enabled = prompt.isNotBlank() && !isLoading
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Send")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewAiCaption() {
    MemeForgeTheme(true) {
        AiPromptDialog(
            {},
            {},
            "",
            false
        )
    }
}