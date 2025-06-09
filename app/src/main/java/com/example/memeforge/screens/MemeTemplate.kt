package com.example.memeforge.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.memeforge.MainActivity
import com.example.memeforge.network.memedata.MemeX
import com.example.memeforge.R
import com.example.memeforge.authentication.User
import com.example.memeforge.data.StoredMeme
import com.example.memeforge.network.MemeService
import com.example.memeforge.network.implementation.MockMemeService
import com.example.memeforge.requestNotificationPermission
import com.example.memeforge.ui.theme.MemeForgeTheme
import com.example.memeforge.viewmodels.TemplateViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("InlinedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateScreen(
    user: User,
    onSignOut: () -> Unit,
    sendImageFromDevice: (String) -> Unit,
    navigateToFav: () -> Unit,
    openMeme: (String, String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val templateViewModel = hiltViewModel<TemplateViewModel>()

    val memes = templateViewModel.memes.collectAsState(emptyList())
    val notificationsEnabled = templateViewModel.notificationEnabled.collectAsState(true)
    val darkModeEnabled = templateViewModel.darkModeEnabled.collectAsState(true)

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        templateViewModel.toggleNotifications(isGranted)
    }

    LaunchedEffect(Unit) {
        requestNotificationPermission(context,notificationPermissionLauncher) {
            templateViewModel.toggleNotifications(it)
        }
    }



    if (notificationsEnabled.value) {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to 'all' topic")
                } else {
                    Log.e("FCM", "Subscription failed", task.exception)
                }
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onSignOut = { onSignOut() },
                image = user.image,
                name = user.name,
                email = user.email,
                navigateToFav = navigateToFav,
                close = { coroutineScope.launch { drawerState.close() } },
                notificationsEnabled = notificationsEnabled.value,
                onNotificationToggle = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        templateViewModel.toggleNotifications(!notificationsEnabled.value)
                    }
                },
                darkModeEnabled = darkModeEnabled.value,
                onDarkModeEnabled = {
                    templateViewModel.toggleDarkMode(!darkModeEnabled.value)
                }
            )
        },
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainContent(
                    scrollBehavior = scrollBehavior,
                    searchQuery = searchQuery,
                    isSearchActive = isSearchActive,
                    onQueryChange = { searchQuery = it },
                    onSearchActiveChange = { isSearchActive = it },
                    onAccountClick = {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    image = user.image,
                    name = user.name,
                    memes = if (!isSearchActive) memes.value else templateViewModel.search(searchQuery),
                    templateViewModel = templateViewModel,
                    openMeme = { url, name -> openMeme(url, name) },
                    sendImageFromDevice = { sendImageFromDevice(it) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    scrollBehavior: TopAppBarScrollBehavior,
    searchQuery: String,
    isSearchActive: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onAccountClick: () -> Unit,
    image: String?,
    name: String = "aryan",
    memes: List<StoredMeme>?,
    templateViewModel: TemplateViewModel,
    sendImageFromDevice: (String) -> Unit,
    openMeme: (String, String) -> Unit
) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                onAccountClick = onAccountClick,
                image = image,
                name = name
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()
                .padding(paddingValues)) {
                Column {
                    SearchBar(
                        query = searchQuery,
                        isActive = isSearchActive,
                        onQueryChange = onQueryChange,
                        onActiveChange = onSearchActiveChange
                    )
                    TemplatesGrid(
                        memes,
                        templateViewModel
                    ) { url, name ->
                        openMeme(url, name)
                    }
                }

                ChooseFromStorageFAB(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    sendImageFromDevice = { sendImageFromDevice(it) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesGrid(
    memes: List<StoredMeme>?,
    memeViewModel: TemplateViewModel,
    openMeme: (String, String) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val isLoading by memeViewModel.isLoading.collectAsState()
    var showRequestTimedOut by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Animation for loading state
    val pulseAnimation by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(memes) {
        showRequestTimedOut = false
    }

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isLoading,
        onRefresh = {
            showRequestTimedOut = false
            memeViewModel.updateMemes()
            refreshTrigger++
        }
    ) {
        when {
            memes == null -> {
                EmptyStateContent(
                    icon = Icons.Outlined.SearchOff,
                    title = "No Results Found",
                    subtitle = "Try adjusting your search terms"
                )
            }

            memes.isEmpty() -> {
                LaunchedEffect(refreshTrigger) {
                    delay(10_000)
                    showRequestTimedOut = true
                    memeViewModel.setLoadingFalse()
                }

                if (!showRequestTimedOut) {
                    LoadingStateContent(pulseAnimation)
                } else {
                    ErrorStateContent {
                        showRequestTimedOut = false
                        memeViewModel.updateMemes()
                        refreshTrigger++
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(memes) { meme ->
                        TemplateGridItem(
                            memeUrl = meme.url,
                            memeName = meme.name,
                            onClick = { openMeme(meme.url, meme.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateContent(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingStateContent(pulseAnimation: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .alpha(pulseAnimation),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Loading Templates...",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = pulseAnimation)
        )
    }
}

@Composable
private fun ErrorStateContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.WifiOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Connection Timeout",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please check your internet connection",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onRetry,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

@Composable
fun TemplateGridItem(
    memeUrl: String,
    memeName: String,
    onClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.95f else 1f,
        animationSpec = tween(300),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = memeUrl,
                contentDescription = memeName,
                error = painterResource(R.drawable.loadingerror),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onLoading = {
                    isLoading = true
                    isError = false
                },
                onSuccess = {
                    isLoading = false
                    isError = false
                },
                onError = {
                    isLoading = false
                    isError = true
                }
            )

            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            }

            // Text overlay
            if (!isLoading && !isError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0.6f
                            )
                        )
                )

                Text(
                    text = memeName,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun ChooseFromStorageFAB(
    modifier: Modifier,
    sendImageFromDevice: (String) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
            uri?.let { sendImageFromDevice(it.toString()) }
        }
    )

    ExtendedFloatingActionButton(
        onClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        ),
        icon = {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        text = {
            Text(
                text = "Pick from Storage",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    )
}

@Composable
fun SearchBar(
    query: String,
    isActive: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                "Search templates...",
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        trailingIcon = {
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close search",
                    modifier = Modifier
                        .clickable {
                            onQueryChange("")
                            onActiveChange(false)
                            focusManager.clearFocus() // hides caret
                        }
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
                onActiveChange(false)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                onActiveChange(focusState.isFocused)
            }
    )
}


@Composable
private fun DrawerContent(
    onSignOut: () -> Unit,
    image: String?,
    name: String,
    email: String,
    navigateToFav: () -> Unit,
    close: () -> Unit,
    notificationsEnabled: Boolean,
    onNotificationToggle: (Boolean) -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeEnabled: (Boolean) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = close) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Close drawer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Profile Section
            UserProfileSection(name = name, email = email, image = image)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 24.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Navigation Items
            DrawerMenuItem(
                icon = Icons.Outlined.Favorite,
                title = "Favourites",
                onClick = navigateToFav
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Settings Section
            Text(
                text = "Settings",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingsItem(
                title = "Notifications",
                isEnabled = notificationsEnabled,
                onToggle = onNotificationToggle
            )

            SettingsItem(
                title = "Dark Mode",
                isEnabled = darkModeEnabled,
                onToggle = onDarkModeEnabled
            )

            Spacer(modifier = Modifier.weight(1f))

            // Enhanced Sign Out Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSignOut() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isDestructive)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = if (isDestructive)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun UserProfileSection(
    name: String,
    email: String,
    image: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = image ?: painterResource(R.drawable.fallbackaccounticon),
            contentDescription = "User Profile",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            error = painterResource(R.drawable.fallbackaccounticon),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = email,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onAccountClick: () -> Unit,
    image: String?,
    name: String
) {
    TopAppBar(
        title = {
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = "Hello, $name",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose your perfect template",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            AsyncImage(
                model = image,
                error = painterResource(R.drawable.fallbackaccounticon),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAccountClick)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        scrollBehavior = scrollBehavior
    )
}

@Preview(showSystemUi = true)
@Composable
fun TemplateScreenPreview() {
    val user = User(
        image = null,
        name = "John Doe",
        email = "mockuser@example.com"
    )
    MemeForgeTheme(true) {
        TemplateScreen(
            user = user,
            onSignOut = {},
            navigateToFav = {},
            openMeme = { _, _ -> },
            sendImageFromDevice = {}
        )
    }
}