package com.example.memeforge.screencomponenets

import android.util.Log
import android.view.KeyEvent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memeforge.R
import com.example.memeforge.viewmodels.EditScreenViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddingCaption(captionState: String,onValueChange : (String) -> Unit, onAddCaption: () -> Unit) {
    Column {
        TitleText("Add Caption",Modifier.padding(start = 12.dp, top = 12.dp, bottom = 8.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = captionState,
                onValueChange = { onValueChange(it) },
                placeholder = {
                    Text("Enter Caption")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Default // Allows Enter key
                ),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth(0.84f)
            )
            IconButton(
                onClick = onAddCaption,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Magenta
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Bottom)
                    .aspectRatio(1f)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun AdditionalOptions(
    addedToFavourite: Boolean,
    onAddToFav: () -> Unit,
    onDownload: () -> Boolean,
    undo: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TaskButton(
            icon = painterResource(R.drawable.baseline_undo_24),
            Modifier.align(Alignment.Bottom),
            action = undo
        )
        Row {
            TaskButton(
                icon = if (!addedToFavourite) painterResource(R.drawable.baseline_bookmark_border_24)
                else painterResource(R.drawable.baseline_bookmark_added_24),
                action = onAddToFav,
            )
            TaskButton(
                icon = painterResource(R.drawable.download_icon),
                Modifier.align(Alignment.Bottom)
            ) {
                onDownload()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        navigationIcon = {
            val interactionSource = remember { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()

            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (pressed) {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onNavigateBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val borderSize = 1.dp.toPx()
                drawLine(
                    color = Color.Gray.copy(alpha = 0.3f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderSize
                )
            }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

    )
}






@Composable
fun TaskButton(icon: Painter, modifier: Modifier = Modifier, action: () -> Unit) {
    Surface(
        modifier = modifier
            .size(58.dp)
            .padding(5.dp)
            .clip(CircleShape)
            .aspectRatio(1f)
            .clickable { action() },
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TitleText(title: String,modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
    )
}

@Composable
fun CaptionColors(
    editViewModel: EditScreenViewModel,
    modifier: Modifier = Modifier,
    changeColor: (Color) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        Text(
            text = "Text Color",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))
            editViewModel.colors.forEach { colorItem ->
                ColorSwatch(
                    color = colorItem.color,
                    isSelected = colorItem.selected.value,
                    onClick = {
                        editViewModel.colors.forEach { it.selected.value = false }
                        colorItem.selected.value = true
                        changeColor(colorItem.color)
                    }
                )
                Spacer(Modifier.width(12.dp))
            }
            Spacer(Modifier.width(16.dp))
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val checkmarkColor = getContrastColor(color)

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) borderColor else MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.3f
                ),
                shape = CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = checkmarkColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Helper function to determine appropriate checkmark color based on background
private fun getContrastColor(backgroundColor: Color): Color {
    // Calculate relative luminance (perceived brightness)
    val luminance = 0.2126f * backgroundColor.red +
            0.7152f * backgroundColor.green +
            0.0722f * backgroundColor.blue

    // Use white text on dark backgrounds, black on light backgrounds
    return if (luminance < 0.5f) Color.White else Color.Black
}
