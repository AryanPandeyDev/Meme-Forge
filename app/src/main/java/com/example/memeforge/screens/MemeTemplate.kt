package com.example.memeforge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.memeforge.R
import com.example.memeforge.authentication.Authentication
import com.example.memeforge.ui.theme.MemeForgeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateScreen(
    authentication: Authentication,
    onSignOut: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val image = authentication.firebaseAuth.currentUser?.photoUrl.toString()
    val name = authentication.firebaseAuth.currentUser?.displayName.toString().split(" ").firstOrNull() ?: "nigga"
    val email = authentication.firebaseAuth.currentUser?.email.toString()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(
            onSignOut = { onSignOut() },
            image,
            name = name,
            email = email
        )},
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
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
                    image = image,
                    name = name
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
    name: String = "nigga"
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                onAccountClick = onAccountClick,
                image = image,
                name = name
            )
        },
        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                SearchBar(
                    query = searchQuery,
                    isActive = isSearchActive,
                    onQueryChange = onQueryChange,
                    onActiveChange = onSearchActiveChange
                )
                TemplatesGrid()
            }
        }
    )
}

@Composable
private fun TemplatesGrid() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(15) {
            TemplateGridItem()
        }
    }
}

@Composable
private fun TemplateGridItem() {
    Box(
        modifier = Modifier
            .size(400.dp)
            .clip(RoundedCornerShape(20.dp))
            .padding(20.dp)
            .background(Color.Cyan)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    isActive: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    DockedSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { onActiveChange(false) },
        active = isActive,
        onActiveChange = onActiveChange,
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        placeholder = { Text("Search templates...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (isActive) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onQueryChange("")
                        onActiveChange(false)
                    }
                )
            }
        }
    ) {}
}

@Composable
private fun DrawerContent(
    onSignOut: () -> Unit,
    image: String?,
    name: String,
    email: String
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserProfileSection(
                name, email,
                image = image
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            // Additional drawer items can be added here
            Spacer(Modifier.weight(1f))  // Pushes content to bottom

            // Sign Out Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSignOut)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign Out",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun UserProfileSection(
    name: String,
    email: String,
    image : String?
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        AsyncImage(
            model = image ?: R.drawable.fallbackaccounticon ,
            contentDescription = "User Profile",
            modifier = Modifier
                .size(65.dp)
                .clip(RoundedCornerShape(100)) ,
        )
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 30.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
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
        modifier = Modifier.padding(top = 10.dp),
        title = {
            Column {
                Text(
                    "Hello $name,",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 10.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "Choose a template of your choice",
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior,
        actions = {
            AsyncImage(
                model = image,
                error = painterResource(R.drawable.fallbackaccounticon),
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(100))
                    .clickable(onClick = onAccountClick),
                contentScale = ContentScale.FillBounds
            )
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun TemplateScreenPreview() {
    MemeForgeTheme {
       // TemplateScreen { }
    }
}