package com.example.m_commerce_admin.core.shared.components.bottom_nav_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.m_commerce_admin.config.theme.DarkestGray
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import com.example.m_commerce_admin.core.shared.components.SvgImage

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    val navigationItems = listOf(
        NavigationItem.Home,
        NavigationItem.Product,
        NavigationItem.Inventory,
        NavigationItem.Coupons,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
          //  .padding(start = 32.dp, end = 32.dp, bottom = 12.dp)
            .padding(vertical = 0.dp, horizontal = 16.dp)
            .navigationBarsPadding()
            ,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .clip(CircleShape)
                .background(color = DarkestGray)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationItems.forEachIndexed { _, item ->
                val isSelected =
                    currentDestination?.contains(item.route::class.simpleName.toString()) ?: false

                AddItem(item, isSelected) {

                    navController.navigate(item.route) {
                        popUpTo(0) {
                            inclusive = true
                            saveState = true

                        }

                    }

                }
            }
        }
    }
}


@Composable
fun AddItem(
    item: NavigationItem,
    isSelected: Boolean,
    onNavigateItem: () -> Unit
) {

    val container = if (isSelected) Teal else DarkestGray
    val background = if (isSelected) White else Transparent

    Box(
        modifier = Modifier
            .height(height = 42.dp)
            .clip(shape = CircleShape)
            .background(background)
            .clickable(onClick = {
                onNavigateItem()
            })
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SvgImage(
                resId = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(if (isSelected) Teal else Gray)
            )
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = item.title, color = container
                )
            }
        }
    }
}

