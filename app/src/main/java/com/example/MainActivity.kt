package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BoostViewModel
import com.example.ui.screens.BoostCheckoutScreen
import com.example.ui.screens.OrdersHistoryScreen
import com.example.ui.screens.ServerSettingsSheet
import com.example.ui.theme.BoostKETheme

enum class BoostDestination {
    BOOST,
    ORDERS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoostKETheme {
                BoostKEApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoostKEApp(
    viewModel: BoostViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentDestination by rememberSaveable { mutableStateOf(BoostDestination.BOOST) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.openServerSettings() },
                            modifier = Modifier.testTag("topbar_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.server_settings_title)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationBarItem(
                            selected = currentDestination == BoostDestination.BOOST,
                            onClick = { currentDestination = BoostDestination.BOOST },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == BoostDestination.BOOST) {
                                        Icons.Rounded.Bolt
                                    } else {
                                        Icons.Outlined.Bolt
                                    },
                                    contentDescription = stringResource(R.string.nav_boost)
                                )
                            },
                            label = { Text(stringResource(R.string.nav_boost)) },
                            modifier = Modifier.testTag("nav_boost")
                        )
                        NavigationBarItem(
                            selected = currentDestination == BoostDestination.ORDERS,
                            onClick = { currentDestination = BoostDestination.ORDERS },
                            icon = {
                                OrdersNavIcon(
                                    selected = currentDestination == BoostDestination.ORDERS,
                                    count = uiState.orders.size
                                )
                            },
                            label = { Text(stringResource(R.string.nav_orders)) },
                            modifier = Modifier.testTag("nav_orders")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationRailItem(
                            selected = currentDestination == BoostDestination.BOOST,
                            onClick = { currentDestination = BoostDestination.BOOST },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == BoostDestination.BOOST) {
                                        Icons.Rounded.Bolt
                                    } else {
                                        Icons.Outlined.Bolt
                                    },
                                    contentDescription = stringResource(R.string.nav_boost)
                                )
                            },
                            label = { Text(stringResource(R.string.nav_boost)) },
                            modifier = Modifier.testTag("nav_boost")
                        )
                        NavigationRailItem(
                            selected = currentDestination == BoostDestination.ORDERS,
                            onClick = { currentDestination = BoostDestination.ORDERS },
                            icon = {
                                OrdersNavIcon(
                                    selected = currentDestination == BoostDestination.ORDERS,
                                    count = uiState.orders.size
                                )
                            },
                            label = { Text(stringResource(R.string.nav_orders)) },
                            modifier = Modifier.testTag("nav_orders")
                        )
                    }
                }

                when (currentDestination) {
                    BoostDestination.BOOST -> {
                        BoostCheckoutScreen(
                            uiState = uiState,
                            onSelectPlatform = viewModel::selectPlatform,
                            onSelectPackage = viewModel::selectPackage,
                            onTargetLinkChange = viewModel::updateTargetLink,
                            onPhoneChange = viewModel::updatePhone,
                            onSubmitStkPush = viewModel::submitStkPush,
                            onDismissStatus = viewModel::dismissStatus,
                            onOpenServerSettings = viewModel::openServerSettings,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BoostDestination.ORDERS -> {
                        OrdersHistoryScreen(
                            orders = uiState.orders,
                            selectedOrderDetail = uiState.selectedOrderDetail,
                            onSelectOrderDetail = viewModel::selectOrderDetail,
                            onReuseOrder = { order ->
                                viewModel.reuseOrder(order)
                                currentDestination = BoostDestination.BOOST
                            },
                            onDeleteOrder = viewModel::deleteOrder,
                            onClearAll = viewModel::clearAllOrders,
                            onGoToBoost = { currentDestination = BoostDestination.BOOST },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    if (uiState.showServerSettings) {
        ServerSettingsSheet(
            currentBaseUrl = uiState.serverBaseUrl,
            onDismiss = viewModel::closeServerSettings,
            onSaveUrl = viewModel::saveServerUrl
        )
    }
}

@Composable
private fun OrdersNavIcon(
    selected: Boolean,
    count: Int
) {
    val icon = if (selected) Icons.Rounded.ReceiptLong else Icons.Outlined.ReceiptLong
    if (count > 0) {
        BadgedBox(
            badge = {
                Badge {
                    Text(if (count > 99) "99+" else count.toString())
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.nav_orders)
            )
        }
    } else {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.nav_orders)
        )
    }
}
