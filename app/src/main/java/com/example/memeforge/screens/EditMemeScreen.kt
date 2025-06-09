package com.example.memeforge.screens


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.opengl.Visibility
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.text.webvtt.WebvttCssStyle.FontSizeUnit
import com.example.memeforge.R

import com.example.memeforge.screencomponenets.AddingCaption
import com.example.memeforge.screencomponenets.AdditionalOptions
import com.example.memeforge.screencomponenets.AiPromptDialog
import com.example.memeforge.screencomponenets.CaptionColors
import com.example.memeforge.screencomponenets.TitleText
import com.example.memeforge.screencomponenets.TopBar
import com.example.memeforge.ui.theme.MemeForgeTheme
import com.example.memeforge.viewmodels.EditScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URI
import java.util.UUID
import kotlin.math.roundToInt

@Composable
fun EditMemeScreen(
    imageFromDevice : String? = null,
    url : String,
    imageName : String,
    navigateBack : () -> Unit
) {
    val context = LocalContext.current
    val editViewModel = viewModel<EditScreenViewModel>()
    var image by remember { mutableStateOf<Bitmap>(BitmapFactory.decodeResource(context.resources,R.drawable.loading_image)) }
    var sliderPosition by remember { mutableFloatStateOf(64f) }
    var captionColor by remember { mutableStateOf(Color.Black) }
    var displayState by remember { mutableStateOf("") }
    var saveIconVisibility by remember { mutableStateOf(false) }
    var AiDialogVisibility by remember { mutableStateOf(false) }

    var captionLoading = editViewModel.captionLoading.collectAsState()
    var responseState = editViewModel.response.collectAsState()
    val density = LocalDensity.current
    val textSizeSp = with(density) { sliderPosition.toSp() }
    val scrollState = rememberScrollState()


    LaunchedEffect(url,imageFromDevice) {
        image = if (imageFromDevice != null) {
            editViewModel.uriToBitmap(context, Uri.parse(imageFromDevice))!!
        }else {
            editViewModel.createImage(url, context)
        }
        editViewModel.imageStack.push(image)
    }


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    "Edit Screen",
                    onNavigateBack = { navigateBack() },
                )
            }
        ) { paddingValues ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface))
            {
                Column(
                    Modifier.fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    AdditionalOptions(
                        addedToFavourite = editViewModel.addedToFavourite.value,
                        onAddToFav = {
                            if (!editViewModel.addedToFavourite.value) {
                                editViewModel.savePhotoToInternalStorage("$imageName ${UUID.randomUUID()}",image,context)
                                editViewModel.addedToFavourite.value = true
                            }else {
                                editViewModel.addedToFavourite.value = false
                            }
                        },
                        onDownload = {
                            editViewModel.savePhotoToExternalStorage(image,"$imageName ${UUID.randomUUID()}",context)
                        },
                        undo = {
                            if (editViewModel.imageStack.size > 1 ) {
                                editViewModel.imageStack.pop()
                                image = editViewModel.imageStack.peek()
                                Log.d("stack", "${editViewModel.imageStack.size}")
                            }
                        }
                    )



                    EditableImageScreen(
                        caption = displayState,
                        bitmap = image.asImageBitmap(),
                        captionColor = captionColor,
                        clearCaption = {
                            displayState = ""
                            saveIconVisibility = false
                        },
                        previewTextSizePx = sliderPosition,
                        previewTextSizeSp = textSizeSp,
                        saveIconVisibility = saveIconVisibility,
                        onSave = { scaledPosition, bitmapTextSizePx ->
                            image = editViewModel.drawTextOnBitmap(
                                image,
                                displayState,
                                scaledPosition,
                                bitmapTextSizePx,
                                captionColor,
                            )
                            editViewModel.imageStack.push(image)
                            Log.d("stack", "${editViewModel.imageStack.size}")
                        },
                    )


                    AddingCaption(
                        editViewModel.captionState.value,
                        onValueChange =  {editViewModel.captionState.value = it}
                    ){
                        if (editViewModel.captionState.value.isNotEmpty()) {
                            displayState = editViewModel.captionState.value
                            editViewModel.captionState.value = ""
                            saveIconVisibility = true
                        }
                    }

                    Button(
                        onClick = {
                            AiDialogVisibility = true
                        },
                        modifier = Modifier
                            .padding(end = 12.dp, top = 8.dp, bottom = 0.dp)
                            .align(Alignment.End)
                    ) {
                        Text(
                            "AI caption",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp
                        )
                    }
                    TitleText("Caption Size",Modifier.padding(start = 12.dp))
                    Slider(
                        valueRange = 40f..140f,
                        value = sliderPosition,
                        onValueChange = {sliderPosition = it},
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    CaptionColors(editViewModel) {
                        captionColor = it
                    }
                }
                if (AiDialogVisibility) {
                    AiPromptDialog(
                        onDismiss = {
                            AiDialogVisibility = false
                            editViewModel.resetState()
                        },
                        onSend = { prompt ->
                            editViewModel.captionLoading.value = true
                            editViewModel.getCaptionFromAI(prompt , imageName)
                        },
                        aiResponse = responseState.value,
                        isLoading = captionLoading.value
                    )
                }
            }
        }
    }
}



@Composable
fun EditableImageScreen(
    caption: String,
    bitmap: ImageBitmap,
    captionColor: Color,
    clearCaption: () -> Unit,
    previewTextSizePx: Float,
    previewTextSizeSp: TextUnit,
    saveIconVisibility: Boolean,
    onSave: (Offset, Float) -> Unit
) {
    var textPosition by remember { mutableStateOf(Offset(0f, 400f)) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.Gray)
            .clipToBounds()
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    textPosition += dragAmount
                }
            }
    ) {
        // Image scaling calculations
        val imageAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        val containerAspect = containerSize.width.toFloat() / containerSize.height.toFloat()

        val (scaleFactor, contentSize) = if (imageAspect > containerAspect) {
            val scale = containerSize.width.toFloat() / bitmap.width.toFloat()
            Pair(scale, IntSize(containerSize.width, (bitmap.height * scale).toInt()))
        } else {
            val scale = containerSize.height.toFloat() / bitmap.height.toFloat()
            Pair(scale, IntSize((bitmap.width * scale).toInt(), containerSize.height))
        }

        val contentLeft = (containerSize.width - contentSize.width) / 2f
        val contentTop = (containerSize.height - contentSize.height) / 2f

        Image(
            bitmap = bitmap,
            contentDescription = "Editable Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(contentSize.width.dp, contentSize.height.dp)
                .align(Alignment.Center)
        )


        if (saveIconVisibility) {
            // Clear button (top-left corner)
            IconButton(
                onClick = {
                    clearCaption()
                    textPosition = Offset(0f,400f)
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }

            IconButton(
                onClick = {
                    val relativeX = textPosition.x - contentLeft
                    val relativeY = textPosition.y - contentTop
                    val scaledPosition = Offset(
                        relativeX / scaleFactor,
                        relativeY / scaleFactor
                    )
                    onSave(scaledPosition, previewTextSizePx / scaleFactor)
                    clearCaption()
                    textPosition = Offset(0f,400f)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White)
            }
        }

        // Draggable text
        Text(
            text = caption,
            color = captionColor,
            fontSize = previewTextSizeSp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .offset { IntOffset(textPosition.x.roundToInt(), textPosition.y.roundToInt()) }
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
}




@Composable
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
fun PreviewEdit() {
    MemeForgeTheme(true) {
        EditMemeScreen(
            "", "",
            imageName = ""
        ){}
    }
}