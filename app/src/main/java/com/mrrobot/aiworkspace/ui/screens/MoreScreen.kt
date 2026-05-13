package com.mrrobot.aiworkspace.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    navController: NavController,
    parentPadding: PaddingValues = PaddingValues()
) {
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("More") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = mergedScreenPadding(innerPadding, parentPadding),
            verticalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            item {
                SectionHeader(
                    title = "Workspace tools",
                    subtitle = "Access the terminal, file manager, marketplace, settings, and profile."
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                MoreCard(
                    iconRes = Route.Terminal.iconRes,
                    title = "Live Terminal",
                    subtitle = "View simulated build logs and command output.",
                    button = "Open Terminal",
                    onClick = { navController.navigate(Route.Terminal.path) }
                )
            }

            item {
                MoreCard(
                    iconRes = Route.Files.iconRes,
                    title = "File Manager",
                    subtitle = "Browse project files and design references.",
                    button = "Open Files",
                    onClick = { navController.navigate(Route.Files.path) }
                )
            }

            item {
                MoreCard(
                    iconRes = Route.Market.iconRes,
                    title = "Marketplace",
                    subtitle = "Manage models, tools, agents, workflows, and integrations.",
                    button = "Open Marketplace",
                    onClick = { navController.navigate(Route.Market.path) }
                )
            }

            item {
                MoreCard(
                    iconRes = Route.Settings.iconRes,
                    title = "Settings",
                    subtitle = "Configure AI providers, model selection, and theme.",
                    button = "Open Settings",
                    onClick = { navController.navigate(Route.Settings.path) }
                )
            }

            item {
                MoreCard(
                    iconRes = Route.Profile.iconRes,
                    title = "Profile",
                    subtitle = "View workspace identity, stats, and export summary.",
                    button = "Open Profile",
                    onClick = { navController.navigate(Route.Profile.path) }
                )
            }
        }
    }
}

@Composable
private fun MoreCard(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    button: String,
    onClick: () -> Unit
) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            GroupTitle(title)
        }

        Spacer(Modifier.height(8.dp))

        BodyText(subtitle)

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.height(40.dp)
        ) {
            Text(button)
        }
    }
}
